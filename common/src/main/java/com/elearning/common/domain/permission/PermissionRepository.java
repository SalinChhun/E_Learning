package com.elearning.common.domain.permission;

import com.elearning.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findByName(String name);
    Optional<Permission> findByNameAndStatus(String name, Status status);
    List<Permission> findByResource(String resource);
    List<Permission> findByResourceAndAction(String resource, String action);
}