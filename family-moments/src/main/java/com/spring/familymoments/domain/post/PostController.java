package com.spring.familymoments.domain.post;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.post.model.*;
import com.spring.familymoments.domain.postLove.PostLoveService;
import com.spring.familymoments.domain.postLove.model.PostLoveRes;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시물 API Document")
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    public BaseResponse<SinglePostRes> createPost(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                  @RequestParam("familyId") long familyId,
                                                  @RequestPart("postInfo") PostInfoReq postInfoReq,
                                                  @RequestPart("imgs") List<MultipartFile> imgs){
        if(postInfoReq == null) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_POST_INFO);
        }

        if(postInfoReq.getContent() == null) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_CONTENT);
        }

        if(imgs.isEmpty()) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_IMAGE);
        }

        PostReq postReq = PostReq.builder()
                .familyId(familyId)
                .imgs(imgs)
                .content(postInfoReq.getContent())
                .build();

        SinglePostRes singlePostRes = postService.createPost(user, postReq);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 게시글 수정 API
     * [POST] /posts/{postId}/edit
     * @return BaseResponse<SinglePostRes>
     */
    @ResponseBody
    @PostMapping(value = "/{postId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public BaseResponse<SinglePostRes> editPost(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                @PathVariable long postId,
                                                @RequestPart(name = "postInfo", required = false) PostEditInfoReq postEditInfoReq,
                                                @RequestPart("imgs") List<MultipartFile> imgs) {
        if(postEditInfoReq == null && imgs.isEmpty()) {
            return new BaseResponse<>(minnie_POSTS_EMPTY_UPDATE);
        }

        PostEditReq postEditReq = PostEditReq.builder()
                .urls(postEditInfoReq != null ? postEditInfoReq.getUrls() : null)
                .imgs(imgs)
                .content((postEditInfoReq != null) ? postEditInfoReq.getContent() : null)
                .build();

        SinglePostRes singlePostRes = postService.editPost(user, postId, postEditReq);
        return new BaseResponse<>(singlePostRes);

    }

    /**
     * 게시글 삭제 API
     * [DELETE] /posts/{postId}
     * @return BaseResponse<null>
     */
    @ResponseBody
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public BaseResponse<SinglePostRes> deletePost(@AuthenticationPrincipal @Parameter(hidden = true) User user, @PathVariable long postId) {
        postService.deletePost(user, postId);
        return new BaseResponse<>(SUCCESS);
    }

    /**
     * 최근 10개 게시글 조회 API
     * [GET] /posts?familyId={가족인덱스}
     * @return BaseResponse<List<SinglePostRes>>
     */
    @ResponseBody
    @GetMapping(params = {"familyId"})
    @Operation(summary = "게시글 10건 조회", description = "최근 10개의 게시글을 조회합니다.")
    public BaseResponse<List<SinglePostRes>> getRecentPosts(@AuthenticationPrincipal @Parameter(hidden = true) User user, @RequestParam("familyId") long familyId) {
        List<SinglePostRes> singlePostRes = postService.getPosts(user, familyId);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 10개 게시글 조회 API
     * [GET] /posts?familyId={가족인덱스}&postId={이전 게시물의 최소 postId}
     * @return BaseResponse<List<SinglePostRes>>
     */
    @ResponseBody
    @GetMapping(params = {"familyId", "postId"})
    @Operation(summary = "게시글 조회(with paging)", description = "커서 이전의 게시물 10건을 조회합니다.")
    public BaseResponse<List<SinglePostRes>> getNextPosts(@AuthenticationPrincipal @Parameter(hidden = true) User user, @RequestParam("familyId") long familyId, @RequestParam("postId") long postId) {
        List<SinglePostRes> singlePostRes = postService.getPosts(user, familyId, postId);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 특정 게시글 조회 API
     * [GET] /posts/{postId}
     * @return BaseResponse<SinglePostRes>
     */
    @ResponseBody
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글 1건을 조회합니다.")
    public BaseResponse<SinglePostRes> getPost(@AuthenticationPrincipal @Parameter(hidden = true) User user, @PathVariable long postId) {
        SinglePostRes singlePostRes = postService.getPost(user, postId);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 특정 일 최신 10개 게시글 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}&day={일}
     * @return BaseResponse<List<MultiPostRes>>
     */
    @ResponseBody
    @GetMapping(value = "/calendar", params = {"familyId", "year", "month", "day"})
    @Operation(summary = "특정 일자 게시글 10건 조회", description = "특정 일자에 작성된 게시글을 최근 순으로 10건 조회합니다.")
    public  BaseResponse<List<SinglePostRes>> getPostsWithDate(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                              @RequestParam("familyId") long familyId,
                                                              @RequestParam("year") int year,
                                                              @RequestParam("month") int month,
                                                              @RequestParam("day") int day) {
        List<SinglePostRes> singlePostRes = postService.getPostsOfDate(user, familyId, year, month, day);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 특정 일 최신 10개 게시글 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}&day={일}
     * @return BaseResponse<List<SinglePostRes>>
     */
    @ResponseBody
    @GetMapping(value = "/calendar", params = {"familyId", "year", "month", "day", "postId"})
    @Operation(summary = "특정 일자 게시글 10건 조회(with paging)", description = "특정 일자의 커서 이후 게시글을 10건 조회합니다.")
    public  BaseResponse<List<SinglePostRes>> getPostsWithDate(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                              @RequestParam("familyId") long familyId,
                                                              @RequestParam("year") int year,
                                                              @RequestParam("month") int month,
                                                              @RequestParam("day") int day,
                                                              @RequestParam("postId") long postId) {
        List<SinglePostRes> singlePostRes = postService.getPostsOfDate(user, familyId, year, month, day, postId);
        return new BaseResponse<>(singlePostRes);
    }

    /**
     * 특정 월 게시물 작성일 조회 API
     * [GET] /posts/calendar?familyId={가족인덱스}&year={년}&month={월}
     * @return BaseResponse<List<LocalDate>>
     */
   @GetMapping(value = "/calendar", params = {"familyId", "year", "month"})
   @Operation(summary = "작성일자 리스트 조회", description = "해당 월 중 게시물이 작성된 날짜 리스트를 조회합니다.")
   public BaseResponse<List<LocalDate>> getDatesExistPost(@RequestParam("familyId") long familyId, @RequestParam("year") int year, @RequestParam("month") int month) {
       if(month < 1 || month > 12 || year > LocalDate.now().getYear()) {
           return new BaseResponse<>(minnie_POSTS_INVALID_POST_ID);
       }

       List<LocalDate> dates = null;
       dates = postService.getDayExistsPost(familyId, year, month);
       return new BaseResponse<>(dates);
   }

    /**
     * 앨범 조회 API - 최근 30건
     * [GET] /posts/album?familyId={가족인덱스}
     * @return BaseResponse<List<AlbumRes>>
     */
    @GetMapping(value = "/album")
    @Operation(summary = "앨범 30건 조회", description = "최근 30건의 게시물을 앨범 형태에 맞춰 조회합니다.")
    public BaseResponse<List<AlbumRes>> getRecentAlbum(@RequestParam("familyId") long familyId) {
        List<AlbumRes> album = postService.getAlbum(familyId);
        return new BaseResponse<>(album);
    }

    /**
     * 앨범 조회 API - 기준 이후 30건
     * [GET] /posts/album?familyId={가족인덱스}&postId={post인덱스}
     * @return BaseResponse<List<AlbumRes>>
     */
    @GetMapping(value = "/album", params = {"familyId", "postId"})
    @Operation(summary = "앨범 30건 조회(with paging)", description = "커서 이전의 30건의 게시물을 앨범 형태에 맞춰 조회합니다.")
    public BaseResponse<List<AlbumRes>> getRecentAlbum(@RequestParam("familyId") long familyId, @RequestParam("postId") long postId) {
        List<AlbumRes> album = postService.getAlbum(familyId, postId);
        return new BaseResponse<>(album);
    }

    /**
     * 앨범 상세 조회 API
     * [GET] /posts/album/{post인덱스}
     * @return BaseResponse<List<String>>
     */
    @GetMapping(value = "/album/{postId}")
    @Operation(summary = "앨범 상세 조회", description = "앨범의 상세 페이지를 조회합니다.")
    public BaseResponse<List<String>> getAlbumImages(@PathVariable long postId) {
        List<String> imgs = postService.getPostImages(postId);
        return new BaseResponse<>(imgs);
    }

    /**
     * 좋아요 목록 조회 API
     * [GET] /posts/{postId}/post-loves
     * @return BaseResponse<List<PostLoveRes>>
     */
    @GetMapping("/{postId}/post-loves")
    @Operation(summary = "좋아요 명단 조회", description = "특정 게시물의 좋아요를 누른 사람의 명단을 조회합니다.")
    public BaseResponse<List<PostLoveRes>> getLovedList(@PathVariable long postId) {
        List<PostLoveRes> heartedList = postLoveService.getHeartList(postId);
        return new BaseResponse<>(heartedList);
    }

    /**
     * 게시글 신고 API
     * [POST] /posts/report/{postId}
     *
     */
    @PostMapping("/report/{postId}")
    public BaseResponse<String> reportPost(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                           @PathVariable Long postId, @RequestBody ContentReportReq contentReportReq) {
        postService.reportPost(user, postId, contentReportReq);
        return new BaseResponse<>("게시글을 신고했습니다.");
    }
}
