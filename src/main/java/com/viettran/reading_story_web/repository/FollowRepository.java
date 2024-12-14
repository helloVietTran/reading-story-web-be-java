package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    @Query("SELECT f FROM Follow f JOIN FETCH f.story WHERE f.user.id = :userId")
    List<Follow> findFollowedStories(String userId);

    //JPQL là ngôn ngữ truy vấn của JPA

    boolean existsByUserIdAndStoryId(String userId, int storyId);

    Optional<Follow> findByUserIdAndStoryId(String userId, int storyId);
}

