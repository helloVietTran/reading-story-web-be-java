package com.viettran.reading_story_web.service;


import com.viettran.reading_story_web.dto.response.LevelResponse;
import com.viettran.reading_story_web.entity.mysql.Level;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.LevelMapper;
import com.viettran.reading_story_web.repository.LevelRepository;
import com.viettran.reading_story_web.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelService {
    LevelRepository levelRepository;
    UserRepository userRepository;

    LevelMapper levelMapper;
    StringRedisTemplate stringRedisTemplate;

    @PreAuthorize("#userId = authentication.name")
    public Long increaseLevel(String userId, String chapterId) {

        // kiểm tra xem user đã đọc chapter chưa
        if(hasUserReadChapter(userId, chapterId))
            return null;
        // đánh dấu user đã đọc chapter nào
        markChapterAsReadByThisUser(userId, chapterId);

        return stringRedisTemplate.opsForValue().increment("user::" + userId);
    }

    public LevelResponse getUserLevel(String userId) {
        String chaptersRead = stringRedisTemplate.opsForValue().get("user::" + userId);

        Optional<Level> levelOptional = levelRepository.findByUserId(userId);

        Level level;
        if(levelOptional.isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
            level = Level.builder()
                    .user(user)
                    .build();
        }else{
            level = levelOptional.get();
        }
        if(chaptersRead != null)
            level.increaseChaptersRead(Integer.parseInt(chaptersRead));

        return levelMapper.toLevelResponse(levelRepository.save(level));
    }

    private boolean hasUserReadChapter(String userId, String chapterId) {
        // key tạm thời để dấu ":"
        String key = "user:" + userId + ":chapter:" + chapterId;

        Boolean exists = stringRedisTemplate.hasKey(key);
        return (exists != null && exists);
    }

    private void markChapterAsReadByThisUser(String userId, String chapterId) {
        String key = "user:" + userId + ":chapter:" + chapterId;
        long timestamp = Instant.now().getEpochSecond();
        stringRedisTemplate.opsForValue().set(key, String.valueOf(timestamp), Duration.ofMinutes(5)); // TTL 5 phút
    }

    @Scheduled(cron = "0 0 * * * *")// 1 giờ 1 lần
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
