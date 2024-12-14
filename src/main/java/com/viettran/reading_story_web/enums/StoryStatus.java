package com.viettran.reading_story_web.enums;

import lombok.Getter;

@Getter
public enum StoryStatus {
    IN_PROGRESS("Đang tiến hành"),
    COMPLETED("Đã hoàn thành" ),
    UPCOMING("Sắp phát hành");

    private  final  String fullName;

    StoryStatus( String fullName) {
            this.fullName = fullName;
    }

    public static String getFullNameFromStatus(StoryStatus storyStatus) {
        return storyStatus.getFullName();
    }
}
