package com.elearning.common.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {

    @Modifying
    @Transactional
    @Query("""
        update ApiLog set
            updatedAt = current_timestamp,
            responseAt = :#{#apiLog.responseAt},
            responseData = :#{#apiLog.responseData},
            duration = :#{#apiLog.duration},
            httpStatus = :#{#apiLog.httpStatus}
        where id = :#{#apiLog.id}
        """)
    void updateSuccess(@Param("apiLog") ApiLog apiLog);

    @Modifying
    @Transactional
    @Query("""
        update ApiLog set
            updatedAt = current_timestamp,
            responseAt = :#{#apiLog.responseAt},
            responseData = :#{#apiLog.responseData},
            responseHeaders = :#{#apiLog.responseHeaders},
            httpStatus = :#{#apiLog.httpStatus},
            duration = :#{#apiLog.duration},
            errorCode = :#{#apiLog.errorCode},
            errorMessage = :#{#apiLog.errorMessage},
            errorCategory = :#{#apiLog.errorCategory}
        where id = :#{#apiLog.id}
        """)
    void updateError(@Param("apiLog") ApiLog apiLog);

}