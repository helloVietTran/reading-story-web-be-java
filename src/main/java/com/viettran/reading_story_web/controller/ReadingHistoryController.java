package com.viettran.reading_story_web.controller;

import com.viettran.reading_story_web.dto.request.ReadingHistoryRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.service.ReadingHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-history")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryController {
    ReadingHistoryService readingHistoryService;

    @GetMapping("/users/{userId}")
    ApiResponse<List<ReadingHistoryResponse>> getReadingHistory(
            @PathVariable String userId
    ){
        return  ApiResponse.<List<ReadingHistoryResponse>>builder()
                .result(readingHistoryService.getReadingHistory(userId))
                .build();
    }

    @PostMapping
    ApiResponse<ReadingHistoryResponse> createReadingHistory(@RequestBody ReadingHistoryRequest request){
        return  ApiResponse.<ReadingHistoryResponse>builder()
                .result(readingHistoryService.createReadingHistory(request))
                .build();

    }

}
