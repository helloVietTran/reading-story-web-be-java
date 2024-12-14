package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, String> {

}
