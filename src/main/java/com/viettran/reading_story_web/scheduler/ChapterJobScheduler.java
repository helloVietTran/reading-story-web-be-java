package com.viettran.reading_story_web.scheduler;

import com.viettran.reading_story_web.entity.mysql.Chapter;
import com.viettran.reading_story_web.repository.ChapterRepository;
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
public class ChapterJobScheduler {
    ChapterRepository chapterRepository;
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 */5 * * * ?")// đồng bộ 5 phút / lần
    public void cacheChapterViewCountInRedis() {
        // set key cho toàn bộ chapter vào redis
        List<Chapter> chapters = chapterRepository.findAll();
        // SD pipelined để tối ưu
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            if (connection instanceof StringRedisConnection) {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (Chapter chapter : chapters) {
                    String key = "chapter::" + chapter.getId();
                    stringRedisConn.set(key, "0");
                }
            } else {
                throw new IllegalStateException("Connection is not a StringRedisConnection");
            }
            return null;
        });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncDataFromRedisToMySQL() {
        Set<String> keys = stringRedisTemplate.keys("chapter::*"); // lấy all key

        if (keys != null) {
            for (String key : keys) {
                String viewCountStr = (String) stringRedisTemplate.opsForValue().get(key);

                if (viewCountStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        String chapterId = keyParts[1];

                        updateChapterViewCountInMySQL(chapterId, viewCountStr);
                    }
                }
            }
        }
    }

    private void updateChapterViewCountInMySQL(String chapterId, String viewCountStr) {
        int viewCount = Integer.parseInt(viewCountStr);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);

        if (chapter != null) {
            chapter.setViewCount(viewCount);
            chapterRepository.save(chapter);
        }
    }
}
