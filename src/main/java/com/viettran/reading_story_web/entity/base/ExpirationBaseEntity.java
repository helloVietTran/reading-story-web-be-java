package com.viettran.reading_story_web.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Setter
@Getter
@MappedSuperclass
public abstract class ExpirationBaseEntity {

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;


    public ExpirationBaseEntity() {}

    public ExpirationBaseEntity(long seconds) {
        this.createdDate = Instant.now();
        this.expirationDate = createdDate.plus(Duration.ofSeconds(seconds));
    }
}

