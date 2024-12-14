package com.viettran.reading_story_web.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarFrameResponse {
    String id;
    String imgSrc;
    Integer price;
    Duration usageTime;
}
