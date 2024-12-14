package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, String> {
    Optional<Reaction> findByCommentIdAndUserId(String commentId, String userId);
}
