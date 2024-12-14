package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.DisabledToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisabledTokenRepository extends JpaRepository<DisabledToken, String> {
}
