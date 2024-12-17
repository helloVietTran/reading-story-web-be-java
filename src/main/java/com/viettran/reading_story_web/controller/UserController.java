package com.viettran.reading_story_web.controller;


import java.io.IOException;
import java.util.List;

import com.viettran.reading_story_web.dto.request.*;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.FollowResponse;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.service.UserService;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/my")
    ApiResponse<String> deleteUser() {
        userService.deleteUser();
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        userService.forgotPassword(request);

        return ApiResponse.<String>builder()
                .result("Password reset link has sent into your email")
                .build();
    }

  //?token
    @PatchMapping("/change-password")
    ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request){
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Password has change")
                .build();
    }

    @PatchMapping("/{userId}/upload-avatar")
    ApiResponse<UserResponse> uploadAvatar(@PathVariable String userId,@ModelAttribute UploadAvatarRequest request)
            throws IOException {
        return ApiResponse.<UserResponse>builder()
                .result(userService.uploadAvatar(userId, request))
                .build();
    }

    //verify account with otp
    @PutMapping("/verify-account")
    ApiResponse<String> verifyOTP(@RequestBody VerifyOTPRequest request) {
        userService.verifyOTP(request);

        return ApiResponse.<String>builder()
                .result("Your account have been verified")
                .build();
    }

    // follow truyện
    @PostMapping("/follow")
    ApiResponse<FollowResponse> followStory(@RequestBody FollowRequest request){
        return ApiResponse.<FollowResponse>builder()
                .result(userService.followStory(request))
                .build();
    }

    // unfollow truyện
    @DeleteMapping("/unfollow/{storyId}/{followId}")
    ApiResponse<String> unfollowStory(
            @PathVariable Integer storyId,
            @PathVariable String followId){
        userService.unfollowStory(storyId, followId);

        return ApiResponse.<String>builder()
                .result("This followed story has been unfollow")
                .build();
    }

    @GetMapping("/top-user")
    ApiResponse<List<UserResponse>> getTop10User(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getTop10UserByChaptersRead())
                .build();
    }
}

