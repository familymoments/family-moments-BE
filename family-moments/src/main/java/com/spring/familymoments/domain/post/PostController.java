package com.spring.familymoments.domain.post;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.post.model.*;
import com.spring.familymoments.domain.postLove.PostLoveService;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.CommentRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostLoveService postLoveService;
    private final AuthService authService;

    /**
     * 게시글 작성 API
     * [POST] /posts?familyId={가족인덱스}
     * @return BaseResponse<SinglePostRes>
     */
    @ResponseBody
    @PostMapping
    public BaseResponse<SinglePostRes> createPost(@AuthenticationPrincipal User user,
                                                  @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                                  @RequestParam("familyId") long familyId,
                                                  @RequestPart("postInfo") PostInfoReq postInfoReq,
                                                  @RequestPart("img1")MultipartFile img1,
                                                  @RequestPart(name = "img2", required = false)MultipartFile img2,
                                                  @RequestPart(name = "img3", required = false)MultipartFile img3,
                                                  @RequestPart(name = "img4", required = false)MultipartFile img4){
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        if(postInfoReq == null) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_POST_INFO);
        }

        if(postInfoReq.getContent() == null) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_CONTENT);
        }

        List<MultipartFile> imgs = new ArrayList<>();

        Stream.of(img1, img2, img3, img4)
                .filter(img -> img != null)
                .forEach(img -> imgs.add(img));

        if(imgs.isEmpty()) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_IMAGE);
        }

        System.out.println(user.getUserId());

        PostReq postReq = PostReq.builder()
                .familyId(familyId)
                .imgs(imgs)
                .content(postInfoReq.getContent())
                .build();

        try {
            SinglePostRes singlePostRes = postService.createPosts(user, postReq);
            return new BaseResponse<>(singlePostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (RuntimeException e) {
            return new BaseResponse<>(FIND_FAIL_FAMILY);
        }
    }

    /**
     * 게시글 수정 API
     * [PATCH] /posts?{postId}
     * @return BaseResponse<SinglePostRes>
     */
    @ResponseBody
    @PatchMapping("/{postId}")
    public BaseResponse<SinglePostRes> editPost(@AuthenticationPrincipal User user,
                                                @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                                @PathVariable long postId,
                                                @RequestPart(name = "postInfo", required = false) PostInfoReq postInfoReq,
                                                @RequestPart(name = "img1", required = false)MultipartFile img1,
                                                @RequestPart(name = "img2", required = false)MultipartFile img2,
                                                @RequestPart(name = "img3", required = false)MultipartFile img3,
                                                @RequestPart(name = "img4", required = false)MultipartFile img4) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        if(postInfoReq == null && img1 == null && img2 == null && img3 == null && img4 == null) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_UPDATE);
        }

        List<MultipartFile> imgs = new ArrayList<>();

        Stream.of(img1, img2, img3, img4)
                .forEach(img -> imgs.add(img));

        PostReq postReq = PostReq.builder()
                .content((postInfoReq != null) ? postInfoReq.getContent() : null)
                .imgs(imgs)
                .build();

        try {
            SinglePostRes singlePostRes = postService.editPost(user, postId, postReq);
            return new BaseResponse<>(singlePostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 게시글 삭제 API
     * [DELETE] /posts?{postId}
     * @return BaseResponse<null>
     */
    @ResponseBody
    @DeleteMapping("/{postId}")
    public BaseResponse<SinglePostRes> editPost(@AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @PathVariable long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            postService.deletePost(user, postId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 최근 10개 게시글 조회 API
     * [GET] /posts?familyId={가족인덱스}
     * @return BaseResponse<List<MultiPostRes>>
     */
    @ResponseBody
    @GetMapping(params = {"familyId"})
    public BaseResponse<List<MultiPostRes>> getRecentPosts(@AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @RequestParam("familyId") long familyId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            List<MultiPostRes> multiPostRes = postService.getPosts(user.getUserId(), familyId);
            return new BaseResponse<>(multiPostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 10개 게시글 조회 API
     * [GET] /posts?familyId={가족인덱스}&postId={이전 게시물의 최소 postId}
     * @return BaseResponse<List<MultiPostRes>>
     */
    @ResponseBody
    @GetMapping(params = {"familyId", "postId"})
    public BaseResponse<List<MultiPostRes>> getNextPosts(@AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @RequestParam("familyId") long familyId, @RequestParam("postId") long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            List<MultiPostRes> multiPostRes = postService.getPosts(user.getUserId(), familyId, postId);
            return new BaseResponse<>(multiPostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 특정 게시글 조회 API
     * [GET] /posts/{postId}
     * @return BaseResponse<SinglePostRes>
     */
    @ResponseBody
    @GetMapping("/{postId}")
    public BaseResponse<SinglePostRes> getPost(@AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @PathVariable long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            SinglePostRes singlePostRes = postService.getPost(user.getUserId(), postId);

            return new BaseResponse<>(singlePostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 특정 일 최신 10개 게시글 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}&day={일}
     * @return BaseResponse<List<MultiPostRes>>
     */
    @ResponseBody
    @GetMapping(value = "/calendar", params = {"familyId", "year", "month", "day"})
    public  BaseResponse<List<MultiPostRes>> getPostsWithDate(@AuthenticationPrincipal User user,
                                                              @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                                              @RequestParam("familyId") long familyId,
                                                              @RequestParam("year") int year,
                                                              @RequestParam("month") int month,
                                                              @RequestParam("day") int day) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            List<MultiPostRes> multiPostRes = postService.getPostsOfDate(user.getUserId(), familyId, year, month, day);
            return new BaseResponse<>(multiPostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 특정 일 최신 10개 게시글 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}&day={일}
     * @return BaseResponse<List<MultiPostRes>>
     */
    @ResponseBody
    @GetMapping(value = "/calendar", params = {"familyId", "year", "month", "day", "postId"})
    public  BaseResponse<List<MultiPostRes>> getPostsWithDate(@AuthenticationPrincipal User user,
                                                              @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                                              @RequestParam("familyId") long familyId,
                                                              @RequestParam("year") int year,
                                                              @RequestParam("month") int month,
                                                              @RequestParam("day") int day,
                                                              @RequestParam("postId") long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            List<MultiPostRes> multiPostRes = postService.getPostsOfDate(user.getUserId(), familyId, year, month, day, postId);
            return new BaseResponse<>(multiPostRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 특정 월 게시물 작성일 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}
     * @return BaseResponse<List<MultiPostRes>>
     */
   @GetMapping(value = "/calendar", params = {"familyId", "year", "month"})
   public BaseResponse<List<LocalDate>> getDatesExistPost(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @RequestParam("familyId") long familyId, @RequestParam("year") int year, @RequestParam("month") int month) {
       if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
           return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
       }

       if(month < 1 || month > 12 || year > LocalDate.now().getYear()) {
           return new BaseResponse<>(minnie_POSTS_INVALIED_POST_ID);
       }

       List<LocalDate> dates = null;
       try {
           dates = postService.getDayExistsPost(familyId, year, month);
           return new BaseResponse<>(dates);
       } catch (BaseException e) {
           return new BaseResponse<>(e.getStatus());
       }
   }

    /**
     * 앨범 조회 API - 최근 30건
     * [GET] /posts/album?familyId={가족인덱스}
     * @return BaseResponse<List<AlbumRes>>
     */
    @GetMapping(value = "/album")
    public BaseResponse<List<AlbumRes>> getRecentAlbum(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @RequestParam("familyId") long familyId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

        try {
            List<AlbumRes> album = postService.getAlbum(familyId);
            return new BaseResponse<>(album);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 앨범 조회 API - 기준 이후 30건
     * [GET] /posts/album?familyId={가족인덱스}&postId={post인덱스}
     * @return BaseResponse<List<AlbumRes>>
     */
    @GetMapping(value = "/album", params = {"familyId", "postId"})
    public BaseResponse<List<AlbumRes>> getRecentAlbum(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @RequestParam("familyId") long familyId, @RequestParam("postId") long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

        try {
            List<AlbumRes> album = postService.getAlbum(familyId, postId);
            return new BaseResponse<>(album);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 앨범 상세 조회 API
     * [GET] /posts/album/{post인덱스}
     * @return BaseResponse<List<String>>
     */
    @GetMapping(value = "/album/{postId}")
    public BaseResponse<List<String>> getAlbumImages(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @PathVariable long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

        try {
            List<String> imgs = postService.getPostImages(postId);
            return new BaseResponse<>(imgs);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 좋아요 목록 조회 API
     * [GET] /posts/{postId}/postLoves
     * @return BaseResponse<List<CommentRes>>
     */
    @GetMapping("/{postId}/post-loves")
    public BaseResponse<List<CommentRes>> getLovedList(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken, @PathVariable long postId) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

        try {
            List<CommentRes> heartedList = postLoveService.getHeartList(postId);

            return new BaseResponse<>(heartedList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
