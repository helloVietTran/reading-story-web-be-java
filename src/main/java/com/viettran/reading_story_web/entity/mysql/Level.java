package com.viettran.reading_story_web.entity.mysql;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viettran.reading_story_web.enums.UserRank;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    @Builder.Default
    String rankName = UserRank.RANK_1.getRankName();// luyện khí kì

    @Column(nullable = false)
    @Builder.Default
    int level = UserRank.RANK_1.getLevel();

    @Column(nullable = false)
    @Builder.Default
    float process = 0.0f;

    @Builder.Default
    int chaptersRead = 0;

    int nextLevelChaptersRequired;

    // relationship
    @JsonBackReference
    @OneToOne
    User user;

    // Phương thức tăng chaptersRead và update rank/level
    public void increaseChaptersRead(int additionalChapters) {
        this.chaptersRead += additionalChapters;

        // Tính số chương cần để lên rank tiếp theo
        UserRank currentRank = UserRank.getRankByChaptersRead(this.chaptersRead);
        this.nextLevelChaptersRequired = calculateNextLevelChapters(currentRank);

        // Tăng level nếu đủ điều kiện
        this.process =  (float) (this.chaptersRead + additionalChapters)/this.nextLevelChaptersRequired;
        while (this.process >= 1) {
            this.level++;
            this.process = 0.0f; // Reset tiến độ
            this.rankName =  UserRank.getRankByChaptersRead(this.chaptersRead).getRankName();
        }
    }

    // Tính số chương cần để lên rank tiếp theo dựa vào chaptersRequired của rank tiếp theo
    private int calculateNextLevelChapters(UserRank currentRank) {
        UserRank[] ranks = UserRank.values();
        for (int i = 0; i < ranks.length - 1; i++) {
            if (ranks[i] == currentRank) {
                return ranks[i + 1].getChaptersRequired() - this.chaptersRead;
            }
        }
        // Nếu đã đạt rank cuối, không cần chương thêm
        return Integer.MAX_VALUE;
    }


}
