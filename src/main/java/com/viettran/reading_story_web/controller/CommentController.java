package com.viettran.reading_story_web.controller;

import com.viettran.reading_story_web.dto.request.CommentRequest;
import com.viettran.reading_story_web.dto.request.CommentUpdationRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.CommentResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentService commentService;

    @PostMapping
    ApiResponse<CommentResponse> createComment(@Valid @RequestBody CommentRequest request){
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @PatchMapping("/{commentId}/users/{userId}")
    ApiResponse<CommentResponse> updateComment(
            @Valid @RequestBody CommentUpdationRequest request,
            @PathVariable String commentId,
            @PathVariable String userId
            ){
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.updateComment(request,commentId, userId))
                .build();
    }

    @DeleteMapping("/{commentId}")
    ApiResponse<String> deleteComment(
            @PathVariable String commentId,
            @PathVariable String userId
    ){
        commentService.deleteComment(commentId);
        return ApiResponse.<String>builder()
                .result("Comment has been deleted")
                .build();
    }

    // call in story detail
    @GetMapping("/stories/{storyId}")
    ApiResponse<PageResponse<CommentResponse>> getCommentsByStoryId(
            @PathVariable Integer storyId,
            @RequestParam(value = "page",required = false,  defaultValue = "1") int page,
            @RequestParam(value = "size",required = false, defaultValue = "15") int size

    )
    {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getCommentsByStoryId(storyId, page, size))
                .build();
    }

    // call in chapter detail
    @GetMapping("/chapters/{chapterId}")
    ApiResponse<PageResponse<CommentResponse>> getCommentsByChapterId(
            @PathVariable String chapterId,
            @RequestParam(value = "page",required = false,  defaultValue = "1") int page,
            @RequestParam(value = "size",required = false, defaultValue = "15") int size
    )
    {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getCommentsByChapterId(chapterId, page, size))
                .build();
    }

    @GetMapping("/new-comments")
    ApiResponse<PageResponse<CommentResponse>> getNewComment(){
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getNewComments())
                .build();
    }

    @GetMapping("/my")
    ApiResponse<PageResponse<CommentResponse>> getMyComment(

    ){
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getMyComment())
                .build();
    }
}
