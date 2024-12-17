package com.viettran.reading_story_web.entity.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if(createdAt == null) createdAt = Instant.now();
        if(updatedAt == null) updatedAt= Instant.now();
    }
}
