package com.project.undoschool.course;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SearchResponse {
    private long total;
    private List<CourseSearchResult> courses;
}
