package com.viettran.reading_story_web.service;

import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.repository.StoryRepository;
import com.viettran.reading_story_web.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowService {
    StoryRepository storyRepository;
    UserRepository userRepository;
}
