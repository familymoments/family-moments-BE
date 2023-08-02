package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
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
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.minnie_POSTLOVES_NON_EXISTS_LOVE;
import static com.spring.familymoments.config.BaseResponseStatus.minnie_POSTS_WRONG_POST_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLoveService {

    private final PostLoveRepository postLoveRepository;
    private final UserRepository userRepository;
    private final PostWithLoveRepository postWithLoveRepository;
    private final PostRepository postRepository;

    /**
     * createLove
     * [POST]
     * @return
     */
    @Transactional
    public void createLove(User user, PostLoveReq postLoveReq) {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 누르기] 존재하지 않는 아이디입니다."));

        Post post = postWithLoveRepository.findByPostId(postLoveReq.getPostId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 누르기] 존재하지 않는 게시물입니다."));

        if(postLoveRepository.existsByPostIdAndUserId(post, member)){
            throw new InternalServerErrorException("이미 좋아요를 누른 게시물입니다.");
        }

        PostLove postLove = PostLove.builder()
                .postId(post)
                .userId(member)
                .build();

        postLoveRepository.save(postLove);
    }

    /**
     * deleteLove
     * [DELETE]
     * @return
     */
    @Transactional
    public void deleteLove(User user, PostLoveReq postLoveReq) {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 취소] 존재하지 않는 아이디입니다."));

        Post post = postWithLoveRepository.findByPostId(postLoveReq.getPostId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 취소] 존재하지 않는 게시물입니다."));

        if(!postLoveRepository.existsByPostIdAndUserId(post, member)){
            throw new InternalServerErrorException("좋아요를 누르지 않아 취소할 수 없습니다.");
        }

        PostLove postLove = postLoveRepository.findByPostIdAndUserId(post, member)
                .orElseThrow(() -> new NoSuchElementException("좋아요를 누르지 않아 취소할 수 없습니다."));

        postLoveRepository.delete(postLove);
    }

    // 좋아요한 user의 nickName 및 profileImg return
    @Transactional
    public List<CommentRes> getHeartList(long postId) throws BaseException {
        Post post = postRepository.findByPostIdAndStatus(postId, BaseEntity.Status.ACTIVE);

        if(post == null) {
            throw new BaseException(minnie_POSTS_WRONG_POST_ID);
        }

        List<CommentRes> users = postLoveRepository.findByPost(post);

        if(users.isEmpty()) {
            throw new BaseException(minnie_POSTLOVES_NON_EXISTS_LOVE);
        }

        return users;
    }
}
