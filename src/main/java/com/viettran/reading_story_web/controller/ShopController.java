package com.viettran.reading_story_web.controller;

import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.request.AvatarFrameRequest;
import com.viettran.reading_story_web.dto.response.AvatarFrameResponse;
import com.viettran.reading_story_web.service.ShopService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ShopController {

    ShopService shopService;

    @GetMapping("/avatar-frame")
    public ApiResponse<List<AvatarFrameResponse>> getAllAvatarFrames() {
        return ApiResponse.<List<AvatarFrameResponse>>builder()
                .result(shopService.getAllAvatarFrames())
                .build();
    }

    @PostMapping("/avatar-frame")
    public ApiResponse<AvatarFrameResponse> createAvatarFrame(@Valid @ModelAttribute AvatarFrameRequest request)
        throws IOException
    {
        return ApiResponse.<AvatarFrameResponse>builder()
                .result(shopService.createAvatarFrame(request))
                .build();
    }

    @PutMapping("/avatar-frame/{id}")
    public ApiResponse<AvatarFrameResponse> updateAvatarFrame(@PathVariable Integer id, @RequestBody AvatarFrameRequest request) {
        return ApiResponse.<AvatarFrameResponse>builder()
                .result(shopService.updateAvatarFrame(id, request))
                .build();
    }

    @DeleteMapping("/avatar-frame/{id}")
    public ApiResponse<String> deleteAvatarFrame(@PathVariable Integer id) {
        shopService.deleteAvatarFrame(id);
        return ApiResponse.<String>builder()
                .result("Avatar frame has been deleted")
                .build();

    }
}
