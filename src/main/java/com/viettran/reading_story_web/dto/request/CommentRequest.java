package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    String parentCommentId;

    @NotNull(message = "SOME_FIELDS_REQUIRED")
    int storyId;

    @NotNull(message = "SOME_FIELDS_REQUIRED")
    String content;

    int atChapter;
    String replyTo;
}
