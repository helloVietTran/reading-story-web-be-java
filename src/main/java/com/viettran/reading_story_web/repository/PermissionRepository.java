package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
