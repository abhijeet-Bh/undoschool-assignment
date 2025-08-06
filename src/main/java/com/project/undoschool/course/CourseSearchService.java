package com.project.undoschool.course;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<Course> searchCourses(
            String query,
            List<String> categories,
            List<String> types,
            Integer minAge,
            Integer maxAge,
            Double minPrice,
            Double maxPrice,
            ZonedDateTime startDate,
            String sort,
            int page,
            int size) {

        Criteria criteria = new Criteria();

        // Full-text search
        if (query != null && !query.isEmpty()) {
            criteria = criteria.and(new Criteria("title").matches(query))
                    .or(new Criteria("description").matches(query));
        }

        // Add filters
        if (categories != null && !categories.isEmpty()) {
            criteria = criteria.and(new Criteria("category").in(categories));
        }

        if (types != null && !types.isEmpty()) {
            criteria = criteria.and(new Criteria("type").in(types));
        }

        if (minAge != null || maxAge != null) {
            Criteria ageCriteria = new Criteria("minAge");
            if (minAge != null) ageCriteria.greaterThanEqual(minAge);
            if (maxAge != null) ageCriteria.lessThanEqual(maxAge);
            criteria = criteria.and(ageCriteria);
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria("price");
            if (minPrice != null) priceCriteria.greaterThanEqual(minPrice);
            if (maxPrice != null) priceCriteria.lessThanEqual(maxPrice);
            criteria = criteria.and(priceCriteria);
        }

        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        // Sorting
        Sort sortOptions = switch (sort != null ? sort : "") {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.ASC, "nextSessionDate");
        };

        // Pagination
        Pageable pageable = PageRequest.of(page, size, sortOptions);

        // Build and execute query
        Query searchQuery = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<Course> searchHits = elasticsearchOperations.search(searchQuery, Course.class);

        // Convert to Page
        List<Course> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(courses, pageable, searchHits.getTotalHits());
    }
}