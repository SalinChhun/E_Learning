package com.elearning.common.domain.rolepermission;

import com.elearning.common.domain.permission.Permission;
import com.elearning.common.domain.role.Role;
import com.elearning.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

   List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByRoleAndStatus(Role role, Status status);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.status = :status")
    List<Permission> findPermissionsByRoleIdAndStatus(Long roleId, Status status);

    boolean existsByRoleAndPermission(Role role, Permission permission);


}