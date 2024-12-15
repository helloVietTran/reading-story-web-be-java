package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findByIdIn(List<Integer> genreIds);

}
