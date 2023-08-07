package com.spring.familymoments;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.post.PostRepository;
import com.spring.familymoments.domain.post.PostService;
import com.spring.familymoments.domain.post.model.AlbumRes;
import com.spring.familymoments.domain.post.model.MultiPostRes;
import com.spring.familymoments.domain.post.model.SinglePostRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PostServiceTest {
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;

    @Test
    void findOne() {
        SinglePostRes post = postRepository.findByPostId(1, 5);

        System.out.println(post.toString());
    }

    @Test
    void countPostLove() {
        postRepository.updateCountLove(5);
    }

    @Test
    void multiplePost() throws BaseException {
        List<AlbumRes> multiPostRes= postService.getAlbum(1, 15);

        if(multiPostRes == null) {
            System.out.print("Is null");
        } else if(multiPostRes.isEmpty()){
            System.out.print("Is empty");
        }

        for(AlbumRes res : multiPostRes) {
            System.out.println("id : " + res.getPostId() + "\t" + res.getImg1());
        }
    }
}
