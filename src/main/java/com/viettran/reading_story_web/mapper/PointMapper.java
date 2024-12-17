package com.viettran.reading_story_web.mapper;

import com.viettran.reading_story_web.dto.response.PointResponse;
import com.viettran.reading_story_web.entity.mysql.Point;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointMapper {
    PointResponse toPointResponse(Point point);
}
