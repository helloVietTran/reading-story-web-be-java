package com.viettran.reading_story_web.entity.redis;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "StoryCache", timeToLive = 3600)
public class StoryCache implements Serializable {
    int id;
    String name;
    String authorName;
    String imgSrc;
    int viewCount;
    double rate;
    int ratingCount;
    int follower;
    int commentCount;
    String slug;
    Long likeCount;
    boolean hot;
}
