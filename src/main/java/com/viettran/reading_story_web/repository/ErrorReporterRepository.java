package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.ErrorReporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorReporterRepository extends JpaRepository<ErrorReporter, Integer> {
}
