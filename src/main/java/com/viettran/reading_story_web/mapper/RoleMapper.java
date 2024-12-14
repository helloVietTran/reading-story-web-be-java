package com.viettran.reading_story_web.mapper;


import com.viettran.reading_story_web.dto.request.RoleRequest;
import com.viettran.reading_story_web.dto.response.RoleResponse;
import com.viettran.reading_story_web.entity.mysql.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}

