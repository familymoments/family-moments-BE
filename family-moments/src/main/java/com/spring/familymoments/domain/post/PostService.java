package com.spring.familymoments.domain.post;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.model.AlbumRes;
import com.spring.familymoments.domain.post.model.MultiPostRes;
import com.spring.familymoments.domain.post.model.PostReq;
import com.spring.familymoments.domain.post.model.SinglePostRes;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final AwsS3Service awsS3Service;
    private final FamilyRepository familyRepository;

    private static final int POST_PAGES = 10;
    private static final int ALBUM_PAGES = 30;

    @Transactional
    public SinglePostRes createPosts(User user, PostReq postReq) {
        // familyID 유효성 검사
        Family family = familyRepository.findById(postReq.getFamilyId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 가족의 구성원이 아닌 경우, val
        if(!familyRepository.isFamilyMember(family, user))
            throw new BaseException(minnie_FAMILY_INVALID_USER);


        // image 업로드
        List<String> urls = awsS3Service.uploadImages(postReq.getImgs());

        // Post builder 생성
        Post.PostBuilder postBuilder = Post.builder()
                .writer(user).familyId(family)
                .content(postReq.getContent());

        // image url <-> post entity로 convert
        for(int i = 0 ; i < urls.size(); i++) {
            String url = urls.get(i);

            switch(i) {
                case 0:
                    postBuilder.img1(url);
                    break;
                case 1:
                    postBuilder.img2(url);
                    break;
                case 2:
                    postBuilder.img3(url);
                    break;
                case 3:
                    postBuilder.img4(url);
                    break;
                default:
                    break;
            }
        }
        // build
        Post params = postBuilder.build();

        Post result = postRepository.save(params);

        // 저장에 실패하는 경우 error 처리
        if(result == null) {
            throw new BaseException(minnie_POST_SAVE_FAIL);
        }

        // postId로 연관된 테이블을 다시 검색하지 않음
        SinglePostRes singlePostRes = SinglePostRes.builder()
                .postId(result.getPostId())
                .writer(result.getWriter().getNickname()).profileImg(result.getWriter().getProfileImg())
                .content(result.getContent()).imgs(result.getImgs()).createdAt(result.getCreatedAt().toLocalDate())
                .countLove(0).loved(false) // 새로 생성된 정보이므로 default return
                .build();

        return singlePostRes;
    }

    // post update
    @Transactional
    public SinglePostRes editPost(User user, long postId, PostReq postReq) {
        Post editedPost = postRepository.findById(postId).orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(editedPost.getStatus() == BaseEntity.Status.INACTIVE) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        if(!Objects.equals(editedPost.getWriter().getUserId(), user.getUserId())) {
            throw new BaseException(minnie_POSTS_INVALID_USER);
        }

        SinglePostRes singlePostRes = getPost(user.getUserId(), postId);
        ArrayList resImgs = new ArrayList<String>();

        if(postReq.getContent() != null) {
            singlePostRes.setContent(postReq.getContent());
            editedPost.updateContent(singlePostRes.getContent());
        }

        for(int i = 0 ; i < 4; i++) {
            String url;

            if(postReq.getImgs().size() > i && postReq.getImgs().get(i) != null) {
                MultipartFile img = postReq.getImgs().get(i);
                url = awsS3Service.uploadImage(img);
            } else {
                url = null;
            }

            if(url != null) {
                resImgs.add(url);
            }

            switch(i) {
                case 0:
                    editedPost.updateImg1(url);
                    break;
                case 1:
                    editedPost.updateImg2(url);
                    break;
                case 2:
                    editedPost.updateImg3(url);
                    break;
                case 3:
                    editedPost.updateImg4(url);
                    break;
                default:
                    break;
            }
        }

        singlePostRes.setImgs(resImgs);

        return singlePostRes;
    }

    // post delete
    @Transactional
    public void deletePost(User user, long postId) {
        Post deletedPost = postRepository.findById(postId).orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(deletedPost.getStatus() == BaseEntity.Status.INACTIVE) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        if(!deletedPost.getWriter().getUserId().equals(user.getUserId())) {
            throw new BaseException(minnie_POSTS_INVALID_USER);
        }

        deletedPost.delete();
    }

    // 현재 가족의 모든 게시물 중 최근 10개를 조회
    public List<MultiPostRes> getPosts(long userId, long familyId) {
        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<MultiPostRes> multiPostReses = postRepository.findByFamilyId(familyId, userId, pageable);

        if(multiPostReses.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        return multiPostReses;
    }

    // 현재 가족의 모든 게시물 중 특정 postId 보다 작은 10개를 조회
    public List<MultiPostRes> getPosts(long userId, long familyId, long postId) {
        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<MultiPostRes> multiPostReses = postRepository.findByFamilyId(familyId, userId, postId, pageable);

        if(multiPostReses.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        return multiPostReses;
    }

    // 특정 post 조회
    @Transactional
    public SinglePostRes getPost(long userId, long postId) {
        // countLove 칼럼 갱신
        postRepository.updateCountLove(postId);

        // post 정보 받아오기
        SinglePostRes singlePostRes = postRepository.findByPostId(userId, postId);

        if(singlePostRes == null) {
            throw new BaseException(minnie_POSTS_INVALID_POST_ID);
        }

        return singlePostRes;
    }

    // 특정 일 최신 post 조회
    @Transactional
    public List<MultiPostRes> getPostsOfDate(long userId, long familyId, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime dummy = LocalTime.MIDNIGHT; // LocalDateTime 변수 생성을 위한 dummy 값

        LocalDateTime dateTime = LocalDateTime.of(date, dummy);

        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<MultiPostRes> posts = postRepository.findByFamilyIdWithDate(familyId, userId, dateTime, pageable);

        if(posts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        return posts;
    }

    // 특정 일 postId 이후 post 조회
    @Transactional
    public List<MultiPostRes> getPostsOfDate(long userId, long familyId, int year, int month, int day, long postId) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime dummy = LocalTime.MIDNIGHT; // LocalDateTime 변수 생성을 위한 dummy 값

        LocalDateTime dateTime = LocalDateTime.of(date, dummy);

        Pageable pageable = PageRequest.of(0, POST_PAGES);
        List<MultiPostRes> posts = postRepository.findByFamilyIdWithDateAfterPostId(familyId, userId, dateTime, postId, pageable);

        if(posts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
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

    @Transactional
    public List<AlbumRes> getAlbum (long familyId) {
        Pageable pageable = PageRequest.of(0, ALBUM_PAGES);
        List<Post> posts = postRepository.findByFamilyIdAndStatusOrderByPostIdDesc(new Family(familyId), BaseEntity.Status.ACTIVE, pageable);

        if(posts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        List<AlbumRes> albumResList = new ArrayList<>();
        for(Post post : posts) {
            AlbumRes albumRes = AlbumRes.builder().postId(post.getPostId()).img1(post.getImg1()).build();
            albumResList.add(albumRes);
        }

        return albumResList;
    }

    @Transactional
    public List<AlbumRes> getAlbum (long familyId, long postId) {
        Pageable pageable = PageRequest.of(0, ALBUM_PAGES);
        List<Post> posts = postRepository.findByFamilyIdAndPostIdLessThanAndStatusOrderByPostIdDesc(new Family(familyId), postId, BaseEntity.Status.ACTIVE, pageable);

        if(posts.isEmpty()) {
            throw new BaseException(minnie_POSTS_NON_EXISTS_POST);
        }

        List<AlbumRes> albumResList = new ArrayList<>();
        for(Post post : posts) {
            AlbumRes albumRes = AlbumRes.builder().postId(post.getPostId()).img1(post.getImg1()).build();
            albumResList.add(albumRes);
        }

        return albumResList;
    }

    @Transactional
    public List<String> getPostImages(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(minnie_POSTS_INVALID_POST_ID));

        List<String> imgs = post.getImgs();

        return imgs;
    }
}
