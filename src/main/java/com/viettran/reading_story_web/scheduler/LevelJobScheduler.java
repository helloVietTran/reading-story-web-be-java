package com.viettran.reading_story_web.scheduler;


import com.viettran.reading_story_web.entity.mysql.Level;
import com.viettran.reading_story_web.repository.LevelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelJobScheduler {
    StringRedisTemplate stringRedisTemplate;
    LevelRepository levelRepository;

    @Scheduled(cron = "0 0/5 * * * ?")
    protected void syncChaptersReadDataInRedis(){
        Set<String> keys = stringRedisTemplate.keys("user::*");

        if (keys != null) {
            for (String key : keys) {
                String chaptersReadStr = (String) stringRedisTemplate.opsForValue().get(key);

                if (chaptersReadStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        String chapterId = keyParts[1];

                        updateLevels(chapterId, chaptersReadStr);
                        stringRedisTemplate.opsForValue().set(key, "0");
                    }
                }
            }
        }

    }

    void updateLevels(String levelId, String chaptersRead) {
        int chaptersReadNumber = Integer.parseInt(chaptersRead);
        Level level = levelRepository.findById(levelId).orElse(null);

        if (level != null)
            level.increaseChaptersRead(chaptersReadNumber);
    }
}
