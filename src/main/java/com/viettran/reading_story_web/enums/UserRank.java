package com.viettran.reading_story_web.enums;

import lombok.Getter;

@Getter
public enum UserRank {
    RANK_1("Luyện khí", 10, 1),
    RANK_2("Trúc cơ", 100, 2),
    RANK_3("Kết đan", 1000, 3),
    RANK_4("Nguyên anh", 5000, 4),
    RANK_5("Hoá thần", 10000, 5),
    RANK_6("Luyện hư", 50000, 6),
    RANK_7("Hợp thể kì", 100000, 7),
    RANK_8("Độ kiếp kì", 500000,8),
    RANK_9("Đại thừa kì", 1000000, 9);

    private final String rankName;
    private final int chaptersRequired;
    private final int level;

    UserRank(String rankName, int chaptersRequired, int level){
        this.rankName = rankName;
        this.chaptersRequired = chaptersRequired;
        this.level = level;
    }

    public static UserRank getRankByChaptersRead(int chaptersRead) {
        for (UserRank rank : UserRank.values()) {
            if (chaptersRead >= rank.getChaptersRequired()) {
                return rank;
            }
        }
        return RANK_1;
    }
}
