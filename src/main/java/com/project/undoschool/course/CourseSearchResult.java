package com.project.undoschool.course;

import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class CourseSearchResult {
    private String id;
    private String title;
    private String category;
    private Double price;
    private ZonedDateTime nextSessionDate;

    public static CourseSearchResult from(Course course) {
        return CourseSearchResult.builder()
                .id(course.getId())
                .title(course.getTitle())
                .category(course.getCategory())
                .price(course.getPrice())
                .nextSessionDate(course.getNextSessionDate())
                .build();
    }
}
