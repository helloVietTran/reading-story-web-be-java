package com.viettran.reading_story_web.controller;

import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.LevelResponse;
import com.viettran.reading_story_web.service.LevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelController {
    LevelService levelService;

    @PutMapping("/users/{userId}/chapters/{chapterId}")
    public ApiResponse<Long> increaseLevel(
            @PathVariable String userId,
            @PathVariable String chapterId) {

       return ApiResponse.<Long>builder()
               .result(levelService.increaseLevel(userId, chapterId))
               .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<LevelResponse> getUserLevel(@PathVariable String userId) {

        return  ApiResponse.<LevelResponse>builder()
                .result(levelService.getUserLevel(userId))
                .build();
    }
}
