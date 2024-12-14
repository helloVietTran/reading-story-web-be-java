package com.viettran.reading_story_web.mapper;


import com.viettran.reading_story_web.dto.request.PermissionRequest;
import com.viettran.reading_story_web.dto.response.PermissionResponse;
import com.viettran.reading_story_web.entity.mysql.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}