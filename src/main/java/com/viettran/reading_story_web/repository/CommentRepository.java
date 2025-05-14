package com.viettran.reading_story_web.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Comment;

import feign.Param;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    @Query("SELECT c FROM Comment c WHERE c.story.id = :storyId ORDER BY c.createdAt DESC")
    List<Comment> findByStoryId(Integer storyId);

    Optional<Comment> findByIdAndUserId(String commentId, String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Story s SET s.likeCount = s.likeCount + 1 WHERE s.id = :storyId")
    int incrementLikeCount(@Param("storyId") Integer storyId);

    Page<Comment> findByStoryIdAndParentCommentIdIsNull(int storyId, Pageable pageable);

    List<Comment> findByParentCommentId(String parentCommentId);

    Page<Comment> findByChapterIdAndParentCommentIdIsNull(String chapterId, Pageable pageable);

    Page<Comment> findAllByUserId(Pageable pageale, String userId);
}
