package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository  extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByTokenAndStatusAndExpirationDateAfter(String token, String status, Instant now);
    Optional<ResetPasswordToken> findFirstByUserIdOrderByCreatedDateDesc(String userId);
}
