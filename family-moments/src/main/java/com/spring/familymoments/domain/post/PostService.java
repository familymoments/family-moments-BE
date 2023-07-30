package com.spring.familymoments.domain.post;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.model.MultiPostRes;
import com.spring.familymoments.domain.post.model.PostReq;
import com.spring.familymoments.domain.post.model.SinglePostRes;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.spring.familymoments.config.BaseResponseStatus.minnie_POSTS_EMPTY_POST;
import static com.spring.familymoments.config.BaseResponseStatus.minnie_POSTS_WRONG_POST_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public SinglePostRes createPosts(User user, PostReq postReq) throws BaseException {
        List<String> urls = awsS3Service.uploadImages(postReq.getImgs());

        Post.PostBuilder postBuilder = Post.builder()
                .writer(user).familyId(new Family(postReq.getFamilyId()))
                .content(postReq.getContent());

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
        Post params = postBuilder.build();

        Post result = postRepository.save(params);

        // postId로 연관된 테이블을 다시 검색하지 않음
        SinglePostRes singlePostRes = SinglePostRes.builder()
                .postId(result.getPostId())
                .writer(result.getWriter().getNickname()).profileImg(result.getWriter().getProfileImg())
                .content(result.getContent()).imgs(result.getImgs()).createdAt(result.getCreatedAt())
                .countLove(0).loved(false) // 새로 생성된 정보이므로 default return
                .build();

        return singlePostRes;
    }

    // 현재 가족의 모든 게시물 중 최근 10개를 조회
    public List<MultiPostRes> getPosts(long userId, long familyId) throws BaseException {
        Pageable pageable = PageRequest.of(0, 10);
        List<MultiPostRes> multiPostReses = postRepository.findByFamilyId(familyId, userId, pageable);

        if(multiPostReses.isEmpty()) {
            throw new BaseException(minnie_POSTS_EMPTY_POST);
        }

        return multiPostReses;
    }

    // 현재 가족의 모든 게시물 중 특정 postId 보다 작은 10개를 조회
    public List<MultiPostRes> getPosts(long userId, long familyId, long postId) throws BaseException {
        Pageable pageable = PageRequest.of(0, 10);
        List<MultiPostRes> multiPostReses = postRepository.findByFamilyId(familyId, userId, postId, pageable);

        if(multiPostReses.isEmpty()) {
            throw new BaseException(minnie_POSTS_EMPTY_POST);
        }

        return multiPostReses;
    }

    // 특정 post 조회
    @Transactional
    public SinglePostRes getPost(long userId, long postId) throws BaseException {
        // countLove 칼럼 갱신
        postRepository.updateCountLove(postId);

        // post 정보 받아오기
        SinglePostRes singlePostRes = postRepository.findByPostId(userId, postId);

        if(singlePostRes == null) {
            throw new BaseException(minnie_POSTS_WRONG_POST_ID);
        }

        return singlePostRes;
    }
}
