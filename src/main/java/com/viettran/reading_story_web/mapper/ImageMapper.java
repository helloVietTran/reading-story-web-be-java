package com.viettran.reading_story_web.mapper;

import com.viettran.reading_story_web.dto.response.ImageResponse;
import com.viettran.reading_story_web.entity.mysql.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse toImageResponse(Image image);
}
