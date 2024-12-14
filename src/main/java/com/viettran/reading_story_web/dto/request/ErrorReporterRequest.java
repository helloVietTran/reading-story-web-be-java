package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorReporterRequest {
    int atChapter;
    String storyName;

    @NotBlank(message = "SOME_FIELDS_REQUIRED")
    String type;

    String description;
}

