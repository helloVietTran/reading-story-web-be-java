package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {
    Optional<Level> findByUserId(String userId);

}
