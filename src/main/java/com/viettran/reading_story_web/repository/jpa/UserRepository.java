package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.imgSrc = :imgSrc WHERE u.id = :id")
    void updateImgSrcById(@Param("imgSrc") String imgSrc, @Param("id") String id);

    @EntityGraph(attributePaths = {"level"})
    @Query("SELECT u FROM User u ORDER BY u.level.chaptersRead DESC")
    List<User> findTop10UsersByChaptersRead(Pageable pageable);
}