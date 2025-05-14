package com.viettran.reading_story_web.entity.mysql;

import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettran.reading_story_web.entity.base.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int atChapter;

    @Column(nullable = false)
    String content;

    String replyTo;

    // comment cấp 2 sẽ chứa parentCommentId của comment cấp 1
    // comment cấp 1 sẽ không chứa parentCommentId
    String parentCommentId;

    @Builder.Default
    int likeCount = 0;

    @Builder.Default
    int dislikeCount = 0;

    // relationship
    @JsonBackReference
    @ManyToOne
    User user;

    @JsonBackReference
    @ManyToOne
    Story story;

    @JsonBackReference
    @ManyToOne
    Chapter chapter;

    @JsonManagedReference
    @OneToMany
    List<Reaction> reactions;
    // dựa vào createdAt để xác định index của comment
}
