package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.post.PostRepository;
import com.spring.familymoments.domain.post.PostWithLoveRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.postLove.entity.PostLove;
import com.spring.familymoments.domain.postLove.model.PostLoveReq;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.CommentRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLoveService {

    private final PostLoveRepository postLoveRepository;
    private final UserRepository userRepository;
    private final PostWithLoveRepository postWithLoveRepository;
    private final PostRepository postRepository;

    /**
     * checkUserPostLove
     * [GET]
     * @return
     */
    public boolean checkPostLoveByUser(Long postId, Long userId) {

        User member = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));

        Post post = postWithLoveRepository.findByPostId(postId)
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        return postLoveRepository.existsByPostIdAndUserId(post, member);
    }

    /**
     * createLove
     * [POST]
     * @return
     */
    @Transactional
    public void createLove(User user, PostLoveReq postLoveReq) throws BaseException {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));

        Post post = postWithLoveRepository.findByPostId(postLoveReq.getPostId())
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(checkDuplicatePostLove(post, member)){
            throw new BaseException(POSTLOVE_ALREADY_EXISTS);
        }

        PostLove postLove = PostLove.builder()
                .postId(post)
                .userId(member)
                .build();

        int countLove = post.getCountLove();
        post.increaseCountLove(countLove);
        postLoveRepository.save(postLove);
    }

    /**
     * deleteLove
     * [DELETE]
     * @return
     */
    @Transactional
    public void deleteLove(User user, PostLoveReq postLoveReq) throws BaseException {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));

        Post post = postWithLoveRepository.findByPostId(postLoveReq.getPostId())
                .orElseThrow(() -> new BaseException(minnie_POSTS_NON_EXISTS_POST));

        if(!checkDuplicatePostLove(post, member)){
            throw new BaseException(FIND_FAIL_POSTLOVE);
        }

        PostLove postLove = postLoveRepository.findByPostIdAndUserId(post, member)
                .orElseThrow(() -> new BaseException(FIND_FAIL_POSTLOVE));

        int countLove = post.getCountLove();
        post.decreaseCountLove(countLove);
        postLoveRepository.delete(postLove);
    }

    // 좋아요한 user의 nickName 및 profileImg return
    @Transactional
    public List<CommentRes> getHeartList(long postId) throws BaseException {
        Post post = postRepository.findByPostIdAndStatus(postId, BaseEntity.Status.ACTIVE);

        if(post == null) {
            throw new BaseException(minnie_POSTS_INVALID_POST_ID);
        }

        List<CommentRes> users = postLoveRepository.findByPost(post);

        if(users.isEmpty()) {
            throw new BaseException(minnie_POSTLOVES_NON_EXISTS_LOVE);
        }

        return users;
    }

    /**
     * checkDuplicatePostLove
     * [GET]
     * @return
     */
    private boolean checkDuplicatePostLove(Post post, User member) throws BaseException {
        return postLoveRepository.existsByPostIdAndUserId(post, member);
    }
}
