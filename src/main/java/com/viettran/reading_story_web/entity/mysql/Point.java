package com.viettran.reading_story_web.entity.mysql;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Integer total;
    Integer consecutiveAttendanceCount = 1;

    Instant lastAttendanceDate;
    //relationship

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "point")
    List<PointHistory> pointHistories;
}
