package com.viettran.reading_story_web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettran.reading_story_web.entity.mysql.Chapter;
import com.viettran.reading_story_web.entity.mysql.Genre;
import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.enums.StoryStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryResponse {
    int id;
    String name;
    String otherName;
    String authorName;

    String status;

    String description;
    String imgSrc;
    int viewCount;
    double rate;
    int ratingCount;
    int follower;
    int commentCount;
    String slug;
    int newestChapter;
    boolean hot;

    Gender gender;

    String genres;

    String createdAt;
    String updatedAt;

    List<ChapterResponse> chapters;
}
