package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {

    List<ReadingHistory> findByUserId(String userId);

    Optional<ReadingHistory> findByUserIdAndStoryId(String userId, Integer StoryId);
}
