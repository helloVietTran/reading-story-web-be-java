package com.viettran.reading_story_web.entity.redis;

import com.viettran.reading_story_web.entity.mysql.Level;
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
public class UserCache implements Serializable {
    String id;
    String name;
    String imgSrc;

    Level level;
}
