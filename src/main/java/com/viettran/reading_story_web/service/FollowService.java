package com.viettran.reading_story_web.service;

import com.viettran.reading_story_web.repository.jpa.ReactionRepository;
import com.viettran.reading_story_web.repository.jpa.ReadingHistoryRepository;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowService {
    ReactionRepository.StoryRepository storyRepository;
    ReadingHistoryRepository.UserRepository userRepository;
}
