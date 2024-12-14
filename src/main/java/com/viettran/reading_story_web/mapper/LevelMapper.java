package com.viettran.reading_story_web.mapper;

import com.viettran.reading_story_web.dto.response.LevelResponse;
import com.viettran.reading_story_web.entity.mysql.Level;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LevelMapper {
    LevelResponse toLevelResponse(Level level);
}
