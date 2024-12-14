package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.redis.StoryCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryCacheRepository extends CrudRepository<StoryCache, String> {
}
