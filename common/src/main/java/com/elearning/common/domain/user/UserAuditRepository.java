package com.elearning.common.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit, Long> {

    List<UserAudit> findByUserIdOrderByCreatedAtDesc(Long userId);
}