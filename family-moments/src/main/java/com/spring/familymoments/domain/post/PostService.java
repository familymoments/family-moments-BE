package com.spring.familymoments.domain.post;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.document.PostDocument;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.entity.PostReport;
import com.spring.familymoments.domain.post.entity.ReportReason;
import com.spring.familymoments.domain.post.model.*;
import com.spring.familymoments.domain.postLove.PostLoveService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostReportRepository postReportRepository;
    private final PostDocumentRepository postDocumentRepository;
    private final PostLoveService postLoveService;
    private final FamilyRepository familyRepository;
    private final AwsS3Service awsS3Service;

    private static final int MAX_IMAGE_SIZE = 4;
    private static final int POST_PAGES = 10;
    private static final int ALBUM_PAGES = 30;

    @Transactional
    public SinglePostRes createPost(User user, PostReq postReq) {
        // familyID 유효성 검사
        Family family = familyRepository.findById(postReq.getFamilyId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 가족의 구성원이 아닌 경우, val
        if(!familyRepository.isFamilyMember(family, user))
            throw new BaseException(minnie_FAMILY_INVALID_USER);

        if(postReq.getImgs().size() > MAX_IMAGE_SIZE) {
            throw new BaseException(minnie_POSTS_FULL_IMAGE);
        }
        // image 업로드
        List<String> urls = awsS3Service.uploadImages(postReq.getImgs());

        // Post builder 생성
        Post params = Post.builder()
                .writer(user)
                .familyId(family)
                .build();

        Post result = postRepository.save(params);

        // '최근 게시물 업로드 시각' 현재 시각으로 업데이트
        family.updateLatestUploadAt();

        // PostDocument builder 생성
        PostDocument docParams = PostDocument.builder()
                .entityId(result.getPostId())
                .content(postReq.getContent())
                .urls(urls)
                .build();

        PostDocument docResult = postDocumentRepository.save(docParams);

        // 새로 생성된 Post
        SinglePostRes singlePostRes = SinglePostRes.builder()
                .postId(result.getPostId())
                .writer(result.getWriter().getNickname())
                .profileImg(result.getWriter().getProfileImg())
                .content(docResult.getContent())
                .imgs(docResult.getUrls())
                //.createdAt(result.getCreatedAt().toLocalDate())
                .createdAt(result.getCreatedAt())
                .countLove(0).loved(false) // 새로 생성된 Post 이므로 default return
                .written(true) // 새로 생성된 Post 이므로 default return
                .build();

        // 새로 만들어진 객체 반환
        return singlePostRes;
    }

    // post update
    @Transactional
    public SinglePostRes editPost(User user, long postId, PostEditReq postEditReq) {
        // 수정할 Post 정보 불러오기
        Post editedPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        // 수정할 Post Document 정보 불러오기
        PostDocument editedPostDocument = postDocumentRepository.findPostDocumentByEntityId(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(editedPost.getStatus() == BaseEntity.Status.INACTIVE) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        if(!Objects.equals(editedPost.getWriter().getUserId(), user.getUserId())) {
            throw new BaseException(minnie_POSTS_EDIT_INVALID_USER);
        }

        // 기존 이미지
        List<String> originImgs = postEditReq.getUrls();
        // 새로운 이미지: 이미지를 추가하지 않고도 수정이 되도록 허용
        List<MultipartFile> newFiles = postEditReq.getNewImgs();
        List<String> newImgs = new ArrayList<>();

        // 새로운 이미지를 추가하지 않고도 수정이 되도록 허용
        if(postEditReq.getNewImgs() != null) {
            if (originImgs.size() + postEditReq.getNewImgs().size() > MAX_IMAGE_SIZE) {
                throw new BaseException(minnie_POSTS_FULL_IMAGE);
            }

            // S3에 새로운 이미지 업로드
            for(MultipartFile img : newFiles) {
                if(img.getSize() > 0) {
                    String url = null;
                    url = awsS3Service.uploadImage(img);
                    newImgs.add(url);
                }
            }
        }

        // 기존 이미지와 새로운 이미지를 하나의 필드로 병합
        List<String> editedImgs = Stream.concat(originImgs.stream(), newImgs.stream())
                .collect(Collectors.toList());

        // MongoDB에 수정된 이미지 및 내용 저장
        postDocumentRepository.findPostDocumentByEntityId(editedPostDocument.getEntityId())
                .ifPresent(postDocument -> { // 일치하는 post document 가 있는 경우에만 수정
                    postDocument.updateContent(postEditReq.getContent());
                    postDocument.updateUrls(editedImgs);
                    postDocumentRepository.save(postDocument);
                });

        boolean isLoved = postLoveService.checkPostLoveByUser(editedPost.getPostId(), editedPost.getWriter().getUserId());
        boolean isWritten = editedPost.isWriter(user);

        SinglePostDocumentRes singlePostDocumentRes = SinglePostDocumentRes.builder()
                .content(postEditReq.getContent())
                .urls(editedImgs)
                .build();

        // 수정된 SinglePostRes 객체 반환
        return toSinglePostRes(
                editedPost.getPostId(),
                editedPost.getWriter().getNickname(),
                editedPost.getWriter().getProfileImg(),
                editedPost.getCreatedAt(),
                editedPost.getCountLove(),
                isLoved, isWritten, singlePostDocumentRes
        );
    }

    // post delete
    @Transactional
    public void deletePost(User user, long postId) {
        // 삭제할 Post 정보 불러오기
        Post deletedPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        // 삭제할 Post Document 정보 불러오기
        PostDocument deletedPostDocument = postDocumentRepository.findPostDocumentByEntityId(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(deletedPost.getStatus() == BaseEntity.Status.INACTIVE) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        if(!deletedPost.getWriter().getUserId().equals(user.getUserId())) {
            throw new BaseException(minnie_POSTS_DELETE_INVALID_USER);
        }

        postRepository.delete(deletedPost);
        postDocumentRepository.delete(deletedPostDocument);
    }

    // 현재 가족의 모든 게시물 중 최근 10개를 조회
    @Transactional(readOnly = true)
    public List<SinglePostRes> getPosts(User user, long familyId) {
        Pageable pageable = PageRequest.of(0, POST_PAGES);

        List<SinglePostRes> posts = getCombinedPosts(user, familyId, pageable);

        return posts;
    }

    // 현재 가족의 모든 게시물 중 특정 postId 보다 작은 10개를 조회
    @Transactional(readOnly = true)
    public List<SinglePostRes> getPosts(User user, long familyId, long postId) {
        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<Post> filteredPosts = postRepository.findByFamilyIdAfterPostId(familyId, postId, pageable);

        if(filteredPosts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        // 로그인 유저의 post love 정보 받아오기
        Long loginUser = user.getUserId();

        List<SinglePostRes> posts = new ArrayList<>();
        for(Post p: filteredPosts){
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());
            boolean isLoved = postLoveService.checkPostLoveByUser(p.getPostId(), loginUser);
            boolean isWritten = p.isWriter(user);

            Long filteredPostId = p.getPostId();
            String writer = p.getWriter().getNickname();
            String profileImg = p.getWriter().getProfileImg();
            LocalDateTime datetime = p.getCreatedAt();
            int countLove = p.getCountLove();

            SinglePostRes singlePostRes = toSinglePostRes(filteredPostId, writer, profileImg,
                    datetime, countLove, isLoved, isWritten, singlePostDocumentRes);

            posts.add(singlePostRes);
        }

        return posts;
    }

    // 특정 post 조회
    @Transactional
    public SinglePostRes getPost(User user, long postId) {
        // countLove 칼럼 갱신
        postRepository.updateCountLove(postId);

        // post 정보 받아오기
        Post post = postRepository.findByPostIdAndStatus(postId, BaseEntity.Status.ACTIVE);
        // post document 정보 받아오기
        SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(postId);

        if(post == null || singlePostDocumentRes == null) {
            throw new BaseException(minnie_POSTS_INVALID_POST_ID);
        }

        // 로그인 유저의 post love 정보 받아오기
        Long userId = user.getUserId();
        boolean isLoved = postLoveService.checkPostLoveByUser(postId, userId);
        // 로그인 유저가 게시물의 작성자인지 확인하기
        boolean isWritten = post.isWriter(user);

        Long filteredPostId = post.getPostId();
        String writer = post.getWriter().getNickname();
        String profileImg = post.getWriter().getProfileImg();
        LocalDateTime datetime = post.getCreatedAt();
        int countLove = post.getCountLove();

        return toSinglePostRes(filteredPostId, writer, profileImg,
                datetime, countLove, isLoved, isWritten, singlePostDocumentRes);
    }

    // 특정 일 최신 post 조회
    @Transactional(readOnly = true)
    public List<SinglePostRes> getPostsOfDate(User user, long familyId, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime dummy = LocalTime.MIDNIGHT; // LocalDateTime 변수 생성을 위한 dummy 값

        LocalDateTime dateTime = LocalDateTime.of(date, dummy);
        Pageable pageable = PageRequest.of(0, POST_PAGES);

        List<SinglePostRes> posts = getCombinedPostsByDate(user, familyId, dateTime, pageable);

        return posts;
    }

    // 특정 일 postId 이후 post 조회
    @Transactional(readOnly = true)
    public List<SinglePostRes> getPostsOfDate(User user, long familyId, int year, int month, int day, long postId) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime dummy = LocalTime.MIDNIGHT; // LocalDateTime 변수 생성을 위한 dummy 값

        LocalDateTime dateTime = LocalDateTime.of(date, dummy);

        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<Post> filteredPosts = postRepository.findByFamilyIdWithDateAfterPostId(familyId, dateTime, postId, pageable);

        if(filteredPosts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        // 로그인 유저의 post love 정보 받아오기
        Long loginUser = user.getUserId();

        List<SinglePostRes> posts = new ArrayList<>();
        for(Post p: filteredPosts){
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());
            boolean isLoved = postLoveService.checkPostLoveByUser(p.getPostId(), loginUser);
            boolean isWritten = p.isWriter(user);

            Long filteredPostId = p.getPostId();
            String writer = p.getWriter().getNickname();
            String profileImg = p.getWriter().getProfileImg();
            LocalDateTime datetime = p.getCreatedAt();
            int countLove = p.getCountLove();

            SinglePostRes singlePostRes = toSinglePostRes(filteredPostId, writer, profileImg,
                    datetime, countLove, isLoved, isWritten, singlePostDocumentRes);

            posts.add(singlePostRes);
        }

        return posts;
    }

    @Transactional
    public List<LocalDate> getDayExistsPost(long familyId, int year, int month) {
        // date 정보 생성
        YearMonth month_info = YearMonth.of(year, month);
        LocalDate start_date = month_info.atDay(1);
        LocalDate end_date = month_info.atEndOfMonth();
        LocalDateTime start = start_date.atStartOfDay();
        LocalDateTime end = end_date.atTime(LocalTime.MAX);

        List<LocalDateTime> dateTimes = postRepository.getDateExistPost(familyId, BaseEntity.Status.ACTIVE, start, end);

        if(dateTimes.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        List<LocalDate> dates = new ArrayList<>();

        for(LocalDateTime dateTime : dateTimes) {
            dates.add(dateTime.toLocalDate());
        }

        return dates;
    }

    @Transactional(readOnly = true)
    public List<AlbumRes> getAlbum (long familyId) {
        Pageable pageable = PageRequest.of(0, ALBUM_PAGES);
        List<Post> filteredPosts = postRepository.findByFamilyIdOrderByCreatedAtDesc(familyId, pageable);

        List<AlbumRes> albumResList = new ArrayList<>();
        for(Post p : filteredPosts) {
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());

            AlbumRes albumRes = AlbumRes.builder()
                    .postId(p.getPostId())
                    .img1(singlePostDocumentRes.getUrls().get(0))
                    .build();

            albumResList.add(albumRes);
        }

        return albumResList;
    }

    @Transactional(readOnly = true)
    public List<AlbumRes> getAlbum (long familyId, long postId) {
        Pageable pageable = PageRequest.of(0, ALBUM_PAGES);
        List<Post> filteredPosts = postRepository.findByFamilyIdAndBeforePostId(familyId, postId, pageable);

        if(filteredPosts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        List<AlbumRes> albumResList = new ArrayList<>();
        for(Post p : filteredPosts) {
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());

            AlbumRes albumRes = AlbumRes.builder()
                    .postId(p.getPostId())
                    .img1(singlePostDocumentRes.getUrls().get(0))
                    .build();

            albumResList.add(albumRes);
        }

        return albumResList;
    }

    @Transactional(readOnly = true)
    public List<String> getPostImages(long postId) {
        SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(postId);

        List<String> imgs = singlePostDocumentRes.getUrls();

        return imgs;
    }

    /**
     * getCombinedPosts
     * Paging 기능이 포함된 API 중 날짜 정보가 필요 없는 메서드에서 사용
     * @return List<SinglePostRes>
     */
    @Transactional(readOnly = true)
    private List<SinglePostRes> getCombinedPosts(User user, long familyId, Pageable pageable) {
        // 1. familyId에 따라서 post 목록 받아오기
        List<Post> filteredPosts = postRepository.findByFamilyIdOrderByCreatedAtDesc(familyId, pageable);

        if(filteredPosts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        // 로그인 유저의 post love 정보 받아오기
        Long loginUser = user.getUserId();

        List<SinglePostRes> posts = new ArrayList<>();
        for(Post p: filteredPosts){
            // 2. 1단계에서 구한 post 목록의 postId와 일치하는 post document 받아오기
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());
            // 3. 로그인 유저의 post love 정보 받아오기
            boolean isLoved = postLoveService.checkPostLoveByUser(p.getPostId(), loginUser);
            // 4. 로그인 유저가 게시물의 작성자인지 확인하기
            boolean isWritten = p.isWriter(user);

            Long filteredPostId = p.getPostId();
            String writer = p.getWriter().getNickname();
            String profileImg = p.getWriter().getProfileImg();
            LocalDateTime datetime = p.getCreatedAt();
            int countLove = p.getCountLove();

            // 4. 반환된 SinglePostRes 객체 목록 생성
            SinglePostRes singlePostRes = toSinglePostRes(filteredPostId, writer, profileImg,
                    datetime, countLove, isLoved, isWritten, singlePostDocumentRes);

            posts.add(singlePostRes);
        }

        // 5. SinglePostRes 객체 목록 반환
        return posts;
    }

    /**
     * getCombinedPostsByDate
     * Paging 기능이 포함된 API 중 날짜 정보가 필요한 메서드에서 사용
     * 로직은 getCombinedPosts 메서드와 유사
     * @return List<SinglePostRes>
     */
    @Transactional(readOnly = true)
    private List<SinglePostRes> getCombinedPostsByDate(User user, long familyId, LocalDateTime dateTime, Pageable pageable) {
        List<Post> filteredPosts = postRepository.findByFamilyIdAndCreatedAtDesc(familyId, dateTime, pageable);

        // 로그인 유저의 post love 정보 받아오기
        Long loginUser = user.getUserId();

        List<SinglePostRes> posts = new ArrayList<>();
        for(Post p: filteredPosts){
            SinglePostDocumentRes singlePostDocumentRes = postDocumentRepository.findByEntityId(p.getPostId());
            boolean isLoved = postLoveService.checkPostLoveByUser(p.getPostId(), loginUser);
            boolean isWritten = p.isWriter(user);

            Long filteredPostId = p.getPostId();
            String writer = p.getWriter().getNickname();
            String profileImg = p.getWriter().getProfileImg();
            LocalDateTime datetime = p.getCreatedAt();
            int countLove = p.getCountLove();

            SinglePostRes singlePostRes = toSinglePostRes(filteredPostId, writer, profileImg,
                    datetime, countLove, isLoved, isWritten, singlePostDocumentRes);

            posts.add(singlePostRes);
        }

        return posts;
    }

    /**
     * toSinglePostRes
     * 인자로 post, post document 정보를 받아서 builder 패턴을 이용해 SinglePostRes 객체를 생성하는 함수
     * @return SinglePostRes
     */
    private static SinglePostRes toSinglePostRes(long postId, String writer, String profileImg,
                                                 LocalDateTime dateTime, int countLove, boolean isLoved,
                                                 boolean isWritten, SinglePostDocumentRes singlePostDocumentRes) {

        return SinglePostRes.builder()
                .postId(postId)
                .writer(writer)
                .profileImg(profileImg)
                .content(singlePostDocumentRes.getContent())
                .imgs(singlePostDocumentRes.getUrls())
                //.createdAt(dateTime.toLocalDate())
                .createdAt(dateTime)
                .countLove(countLove)
                .loved(isLoved)
                .written(isWritten)
                .build();
    }
    @Transactional
    public void reportPost(User fromUser, Long postId, ContentReportReq contentReportReq) {;
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        //신고 사유 저장
        PostReport reportedPost = PostReport.createPostReport(
                fromUser,
                post,
                ReportReason.getEnumTypeFromStringReportReason(contentReportReq.getReportReason()),
                contentReportReq.getDetails()
        );
        postReportRepository.save(reportedPost);

        //누적 횟수 3회차일 때 게시물 삭제
        if(post.getReported() == 2) {
            postRepository.delete(post);
        } else {
            //신고 횟수 업데이트
            post.updateReported(post.getReported() + 1);
            postRepository.save(post);
        }

    }

}
