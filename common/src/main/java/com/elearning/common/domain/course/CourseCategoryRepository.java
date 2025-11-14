package com.elearning.common.domain.course;

import com.elearning.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    Optional<CourseCategory> findByNameAndStatus(String name, Status status);
    List<CourseCategory> findByStatus(Status status);
}

