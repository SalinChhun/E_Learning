package com.elearning.common.domain.role;

import com.elearning.common.enums.RoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByNameAndStatus(String name, RoleStatus status);
    Optional<Role> findByIdAndStatus(Long id, RoleStatus status);
    Optional<Role> findRoleByStatus(RoleStatus status);
    List<Role> findByStatus(RoleStatus status);
}