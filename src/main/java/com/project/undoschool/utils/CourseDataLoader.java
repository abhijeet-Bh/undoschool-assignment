package com.project.undoschool.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.undoschool.course.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseDataLoader implements CommandLineRunner {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(Course.class);

            // Check if index exists and has documents
            if (!indexOps.exists() || elasticsearchOperations.count(Query.findAll(), Course.class) == 0) {
                log.info("Loading sample courses into Elasticsearch...");

                // Create index if doesn't exist
                if (!indexOps.exists()) {
                    Document mapping = indexOps.createMapping(Course.class);
                    indexOps.create();
                    indexOps.putMapping(mapping);
                    log.info("Created courses index");
                }

                // Load JSON file from resources
                ClassPathResource resource = new ClassPathResource("sample-courses.json");
                try (InputStream inputStream = resource.getInputStream()) {
                    List<Course> courses = objectMapper.readValue(
                            inputStream,
                            new TypeReference<>() {}
                    );

                    // Index all courses
                    elasticsearchOperations.save(courses);
                    log.info("Successfully loaded {} courses", courses.size());
                }
            } else {
                long count = elasticsearchOperations.count(Query.findAll(), Course.class);
                log.info("{} courses already exist in Elasticsearch. Skipping data loading.", count);
            }
        } catch (Exception e) {
            log.error("Failed to load sample courses", e);
            throw e;
        }
    }
}
