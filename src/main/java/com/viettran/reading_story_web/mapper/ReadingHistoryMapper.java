package com.viettran.reading_story_web.mapper;

import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.entity.mysql.ReadingHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingHistoryMapper {
    ReadingHistoryResponse toReadingHistoryResponse(ReadingHistory readingHistory);
}
