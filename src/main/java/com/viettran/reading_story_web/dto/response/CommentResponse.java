package com.viettran.reading_story_web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettran.reading_story_web.entity.mysql.Comment;
import com.viettran.reading_story_web.entity.mysql.ErrorReporter;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    String id;
    int atChapter;
    String content;
    String replyTo;
    int likeCount;
    int dislikeCount;

    String createdAt;
    String updatedAt;

    UserResponse user;
    StoryResponse story;

    // chứa các comment con
    List<CommentResponse> replies;

    String parentCommentId;
}
