package com.viettran.reading_story_web.repository;

import com.viettran.reading_story_web.entity.mysql.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByEmailAndStatusAndExpirationDateAfter(String token, String status, Instant now);
}
