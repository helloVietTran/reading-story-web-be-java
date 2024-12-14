package com.viettran.reading_story_web.service;


import com.viettran.reading_story_web.repository.StoryRepository;
import com.viettran.reading_story_web.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowService {
    StoryRepository storyRepository;
    UserRepository userRepository;


}
