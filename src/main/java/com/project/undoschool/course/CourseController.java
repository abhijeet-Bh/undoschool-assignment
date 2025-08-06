package com.project.undoschool.course;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseSearchService courseSearchService;

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Course> results = courseSearchService.searchCourses(
                q, category, type,
                minAge, maxAge,
                minPrice, maxPrice,
                startDate,
                sort,
                page, size);

        List<CourseSearchResult> courseResults = results.getContent().stream()
                .map(CourseSearchResult::from)
                .collect(Collectors.toList());

        SearchResponse response = SearchResponse.builder()
                .total(results.getTotalElements())
                .courses(courseResults)
                .build();

        return ResponseEntity.ok(response);
    }
}
