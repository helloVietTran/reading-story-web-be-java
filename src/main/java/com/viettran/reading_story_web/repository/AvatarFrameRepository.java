package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.AvatarFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarFrameRepository extends JpaRepository<AvatarFrame, Integer> {
}
