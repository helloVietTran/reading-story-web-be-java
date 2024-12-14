package com.viettran.reading_story_web.service;

import com.viettran.reading_story_web.dto.request.CommentRequest;
import com.viettran.reading_story_web.dto.request.CommentUpdationRequest;
import com.viettran.reading_story_web.dto.response.CommentResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.dto.response.StoryResponse;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.entity.mysql.*;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.CommentMapper;
import com.viettran.reading_story_web.mapper.LevelMapper;
import com.viettran.reading_story_web.mapper.StoryMapper;
import com.viettran.reading_story_web.mapper.UserMapper;
import com.viettran.reading_story_web.repository.*;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    StoryRepository storyRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;
    ChapterRepository chapterRepository;
    InventoryRepository inventoryRepository;
    AuthenticationService authenticationService;

    DateTimeFormatUtil dateTimeFormatUtil;

    CommentMapper commentMapper;
    StoryMapper storyMapper;
    UserMapper userMapper;
    LevelMapper levelMapper;

    SimpMessagingTemplate messagingTemplate;

    public CommentResponse createComment(CommentRequest request){
        String userId =  authenticationService.getCurrentUserId();
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(()-> new AppException(ErrorCode.STORY_NOT_EXISTED));

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        Chapter chapter = chapterRepository
                .findByStoryIdAndChap( request.getStoryId(),request.getAtChapter())
                .orElseThrow(()-> new AppException((ErrorCode.CHAPTER_NOT_EXISTED)));

        Comment comment = commentMapper.toComment(request);
        comment.setStory(story);
        comment.setUser(user);
        comment.setChapter(chapter);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        commentRepository.save(comment);

        CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
        commentResponse.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
        commentResponse.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));
        
        return commentResponse;
    }

    @PreAuthorize("#userId == authentication.name")
    public CommentResponse updateComment(CommentUpdationRequest request, String commentId, String userId){
        Optional<Comment> commentOptional = commentRepository.findByIdAndUserId(commentId, userId);
        if(commentOptional.isEmpty())
            throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);

        commentMapper.updateComment(commentOptional.get(), request);

        return commentMapper.toCommentResponse(commentRepository.save(commentOptional.get()));
    }

    @PreAuthorize("#userId == authentication.name")
    public void deleteComment(String commentId, String userId){
        commentRepository.deleteById(commentId);
    }

    public PageResponse<CommentResponse> getCommentsByStoryId(int storyId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = commentRepository.findByStoryIdAndParentCommentIdIsNull(storyId, pageable);

        List<CommentResponse> commentResponseList = pageData.getContent()
                .stream()
                .map(this::buildCommentResponseWithReplies)
                .toList();

        return buildPageResponse(page, pageData, commentResponseList);
    }


    public PageResponse<CommentResponse> getNewComments(){
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        var pageData = commentRepository.findAll(pageable);

        List<CommentResponse> commentResponses = pageData.getContent().stream().map(comment->{
            CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
            commentResponse.setStory(
                    StoryResponse.builder()
                            .name(comment.getStory().getName())
                            .id(comment.getStory().getId())
                            .newestChapter(comment.getStory().getNewestChapter())
                            .viewCount(comment.getStory().getViewCount())
                            .imgSrc(comment.getStory().getImgSrc())
                            .slug(comment.getStory().getSlug())
                            .build()
            );

            commentResponse.setUser(
                    UserResponse.builder()
                            .id(comment.getUser().getId())
                            .name(comment.getUser().getName())
                            .imgSrc(comment.getUser().getImgSrc())
                            .build()
            );

            commentResponse.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
            commentResponse.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));
            return commentResponse;
        }).toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(0)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(commentResponses)
                .build();

    }
    public PageResponse<CommentResponse> getCommentsByChapterId(String chapterId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // Lấy danh sách comment gốc (parentCommentId = null)
        var pageData = commentRepository.findByChapterIdAndParentCommentIdIsNull(chapterId, pageable);

        // Chuyển đổi dữ liệu sang CommentResponse và xử lý replies
        List<CommentResponse> commentResponseList = pageData.getContent()
                .stream()
                .map(this::buildCommentResponseWithReplies)
                .toList();

        return buildPageResponse(page, pageData, commentResponseList);
    }

    // lấy comment của bản thân
    @PreAuthorize("#id == authentication.name")
    public PageResponse<CommentResponse> getMyComment(String id){
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        var pageData = commentRepository.findAll(pageable);

        return PageResponse.<CommentResponse>builder()
                .currentPage(0)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream().map(comment->{
                    CommentResponse commentResponse = commentMapper.toCommentResponse(comment);


                    return commentResponse;
                }).toList())
                .build();
    }

    private CommentResponse buildCommentResponseWithReplies(Comment comment) {
        CommentResponse response = buildCommentResponse(comment);
        List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());

        // Nếu có replies, thì map sang CommentResponse
        if (!replies.isEmpty()) {
            List<CommentResponse> replyResponses = replies.stream()
                    .map(this::buildCommentResponse)
                    .toList();
            response.setReplies(replyResponses);
        }

        return response;
    }

    private CommentResponse buildCommentResponse(Comment comment) {
        CommentResponse response = commentMapper.toCommentResponse(comment);
        response.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
        response.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));

        // Lấy thông tin frame của người dùng
        String frame = inventoryRepository
                .findFirstByUserIdAndExpirationDateAfter(comment.getUser().getId(), Instant.now())
                .map(inventory -> inventory.getAvatarFrame().getImgSrc())
                .orElse("");

        // Thiết lập thông tin người dùng
        response.setUser(UserResponse.builder()
                .imgSrc(comment.getUser().getImgSrc())
                .name(comment.getUser().getName())
                .id(comment.getUser().getId())
                .frame(frame)
                .level(levelMapper.toLevelResponse(comment.getUser().getLevel()))
                .build());

        return response;
    }

    private PageResponse<CommentResponse> buildPageResponse(int page, Page<Comment> pageData, List<CommentResponse> content) {
        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(content)
                .build();
    }

}
