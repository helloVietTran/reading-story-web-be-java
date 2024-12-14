package com.viettran.reading_story_web.mapper;

import com.viettran.reading_story_web.dto.response.PointResponse;
import com.viettran.reading_story_web.entity.mysql.Point;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointMapper {
    PointResponse toPointResponse(Point point);
}
