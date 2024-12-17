package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import com.viettran.reading_story_web.dto.request.*;
import com.viettran.reading_story_web.dto.response.FollowResponse;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.entity.mysql.*;
import com.viettran.reading_story_web.entity.redis.UserCache;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.StoryMapper;
import com.viettran.reading_story_web.mapper.UserMapper;
import com.viettran.reading_story_web.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    StoryRepository storyRepository;
    FollowRepository followRepository;
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    OTPRepository otpRepository;
    UserCacheRepository userCacheRepository;
    LevelService levelService;

    UserMapper userMapper;
    StoryMapper storyMapper;

    PasswordEncoder passwordEncoder;
    FileService fileService;

    AuthenticationService authenticationService;
    SimpMessagingTemplate simpMessagingTemplate;

    @NonFinal
    @Value("${app.folder.avatar}")
    protected String AVATAR_FOLDER;

    @NonFinal
    @Value("app.frontend.url")
    String FRONTEND_URL;

    static final String TOP_USER = "topUser";

    public UserResponse createUser(UserCreationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isPresent())
            throw new AppException(ErrorCode.USER_EXISTED);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);

        User user = userMapper.toUser(request);
        user.setCreatedAt(Instant.now());
        user.setCreatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        levelService.createLevel(user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }


    public UserResponse updateUser(String userId, UserUpdationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setUpdatedAt(Instant.now());
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            throw new AppException(ErrorCode.USER_NOT_EXISTED);

        userRepository.deleteByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("#id == authentication.name")
    public UserResponse uploadAvatar(String id, UploadAvatarRequest request) throws IOException {
        String url = fileService.uploadFile(request.getFile(), AVATAR_FOLDER);

        User user = userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setImgSrc(url);
        user.setUpdatedAt(Instant.now());
        return userMapper.toUserResponse(userRepository.save(user));
    }
    public FollowResponse followStory(FollowRequest request) {
        String userId = authenticationService.getCurrentUserId();

       if(followRepository.existsByUserIdAndStoryId(userId, request.getStoryId()))
           throw new AppException(ErrorCode.FOLLOWED_STORY);

       Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

       User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

       Follow follow = Follow.builder()
                .story(story)
                .user(user)
                .followTime(Instant.now())
                .build();
        followRepository.save(follow);

        story.setFollower(story.getFollower() + 1); // Tăng follower cho truyện
        storyRepository.save(story);

        return FollowResponse.builder()
                .story(storyMapper.toStoryResponse(story))
                .user(userMapper.toUserResponse(user))
                .build();
    }

    public void unfollowStory(Integer storyId, String followId){
        followRepository.deleteById(followId);
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        story.setFollower(story.getFollower() - 1);
        storyRepository.save(story);

        simpMessagingTemplate.convertAndSend("/topic/followers/" + storyId, story.getFollower());
    }

    public void forgotPassword(ForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String rawResetPasswordToken = generateResetPasswordToken(user, 60*15);

        //gửi mail chứa link
    }

    public void changePassword(ChangePasswordRequest request) {
        String userId = authenticationService.getCurrentUserId();

        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.INVALID_CONFIRM_PASSWORD);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    public void verifyOTP(VerifyOTPRequest request){

        OTP otp = otpRepository
                .findByEmailAndStatusAndExpirationDateAfter(request.getEmail(), "unused", Instant.now())
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_EXISTED));

        if(passwordEncoder.matches(request.getOtp(), otp.getOtp()))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        otp.setStatus("unused");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsVerified(true);

        otpRepository.save(otp);
        userRepository.save(user);
    }

    public List<UserResponse> getTop10UserByChaptersRead(){
        //Iterable<UserCache> iterable = userCacheRepository.findAll();

        Pageable pageable = PageRequest.of(0, 10);
        List<User> topUsers = userRepository.findTop10UsersByChaptersRead(pageable);
        return topUsers.stream().map(userMapper::toUserResponse).toList();

       /* if(!iterable.iterator().hasNext()){


            for(User user: topUsers){
                userCacheRepository.save(userMapper.toUserCache(user));
            }

        }else{
            List<UserCache> cacheTopUsers = new ArrayList<>();
            iterable.forEach(cacheTopUsers::add);

            return cacheTopUsers.stream().map(userMapper::toUserResponse).toList();
        }*/
    }

    String generateResetPasswordToken(User user, int expirationSeconds) {

        Optional<ResetPasswordToken> lastToken = resetPasswordTokenRepository
                .findFirstByUserIdOrderByCreatedDateDesc(user.getId());

        // sau 15 phút 1 giây mới cho tạo token mới, để tránh việc request nhiều token
        if (lastToken.isPresent() &&
                lastToken.get().getCreatedDate().plus(Duration.ofSeconds(15 * 60 + 1)).isAfter(Instant.now())) {
            throw new AppException(ErrorCode.NOT_ENOUGH_TIME_REQUIRED);
        }

        String rawToken = UUID.randomUUID().toString(); // Tạo token ngẫu nhiên
        String hashedToken = passwordEncoder.encode(rawToken); // Hash token

        ResetPasswordToken resetToken = new ResetPasswordToken(user, expirationSeconds);
        resetToken.setToken(hashedToken);
        resetToken.setStatus("unused");

        resetPasswordTokenRepository.save(resetToken);

        return rawToken;
    }

}
  /*ResetPasswordToken resetToken = resetPasswordTokenRepository
                .findByTokenAndStatusAndExpirationDateAfter(hashToken, "unused", Instant.now())
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_EXISTED));

        User user = resetToken.getUser();// lấy bản ghi user
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Đánh dấu token là đã sử dụng
        resetToken.setStatus("used");*/
