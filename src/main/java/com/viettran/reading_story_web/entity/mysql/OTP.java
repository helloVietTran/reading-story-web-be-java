package com.viettran.reading_story_web.entity.mysql;

import com.viettran.reading_story_web.entity.base.ExpirationBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "OTP")
public class OTP extends ExpirationBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;

    String email;

    String otp;

    String status;

    public OTP(int expirationSeconds) {
        super(expirationSeconds);
    }

}

