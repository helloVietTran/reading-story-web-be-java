package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository  extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findFirstByUserIdAndStatusAndExpirationDateAfterOrderByCreatedDateAsc(
            String userId,
            String status,
            Instant now);
}
