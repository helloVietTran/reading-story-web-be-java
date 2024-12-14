package com.viettran.reading_story_web.scheduler;

import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.repository.StoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryJobScheduler {
    StoryRepository storyRepository;
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cacheStoryViewCountInRedis() {
        // set key cho toàn bộ chapter vào redis
        List<Story> stories = storyRepository.findAll();
        // SD pipelined để tối ưu
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            if (connection instanceof StringRedisConnection) {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (Story story : stories) {
                    String key = "chapter::" + story.getId();
                    stringRedisConn.set(key, String.valueOf(story.getViewCount()));
                }
            } else {
                throw new IllegalStateException("Connection is not a StringRedisConnection");
            }
            return null;
        });
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncDataFromRedisToMySQL() {
        // đồng bộ mỗi 10 phút
        Set<String> keys = stringRedisTemplate.keys("story::*"); // lấy all key

        if (keys != null) {
            for (String key : keys) {
                String viewCountStr = (String) stringRedisTemplate.opsForValue().get(key);

                if (viewCountStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        Integer storyId = Integer.parseInt(keyParts[1]);

                        updateStoryViewCountInMySQL(storyId, viewCountStr);
                    }
                }
            }
        }
    }

    private void updateStoryViewCountInMySQL(Integer storyId, String viewCountStr) {
        int viewCount = Integer.parseInt(viewCountStr);
        Story story = storyRepository.findById(storyId).orElse(null);

        if (story != null) {
            story.setViewCount(viewCount);
            storyRepository.save(story);
        }
    }
}
