package com.viettran.reading_story_web.dto.response;

import com.viettran.reading_story_web.entity.mysql.Story;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryResponse {
    String id;

    StoryResponse story;
    List<String> chaptersRead;
}
