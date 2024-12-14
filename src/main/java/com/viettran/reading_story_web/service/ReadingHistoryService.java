package com.viettran.reading_story_web.service;

import com.viettran.reading_story_web.dto.request.ReadingHistoryRequest;
import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.entity.mysql.ReadingHistory;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.ReadingHistoryMapper;
import com.viettran.reading_story_web.repository.ReadingHistoryRepository;
import com.viettran.reading_story_web.repository.StoryRepository;
import com.viettran.reading_story_web.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryService {
    ReadingHistoryRepository readingHistoryRepository;
    UserRepository userRepository;
    StoryRepository storyRepository;

    ReadingHistoryMapper readingHistoryMapper;

    @PreAuthorize("#userId == authentication.name")
    public List<ReadingHistoryResponse> getReadingHistory(String userId){
        List<ReadingHistory> readingHistories = readingHistoryRepository.findByUserId(userId);

        return readingHistories.stream().map(readingHistoryMapper::toReadingHistoryResponse).toList();
    }

    public ReadingHistoryResponse createReadingHistory(ReadingHistoryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        // Tìm lịch sử đọc truyện theo userId và storyId
        Optional<ReadingHistory> optionalHistory =
                readingHistoryRepository.findByUserIdAndStoryId(request.getUserId(), request.getStoryId());

        ReadingHistory history;

        if (optionalHistory.isPresent()) {// nếu tìm thấy
            history = optionalHistory.get();

            List<String> chaptersRead = history.getChaptersRead();

            // kiểm tra nếu list không chứa phần tử thì add thêm vào lịch sử
            if(!chaptersRead.contains(request.getChapterRead().toString())){
                chaptersRead.add(request.getChapterRead().toString());
            }

        } else {// không tìm thấy thì tạo mới bản ghi và lưu
            history = createReadingHistoryRecord(user ,story ,request);
        }

        readingHistoryRepository.save(history);

        return readingHistoryMapper.toReadingHistoryResponse(history);
    }

    // hàm tạo reading history
     ReadingHistory createReadingHistoryRecord(User user,Story story, ReadingHistoryRequest request){

        return ReadingHistory.builder()
                .story(story)
                .user(user)
                .chaptersRead(List.of(request.getChapterRead().toString()))
                .build();
    }


}
