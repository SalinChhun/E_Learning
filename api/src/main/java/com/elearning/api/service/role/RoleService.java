package com.elearning.api.service.role;

import com.elearning.common.domain.role.Role;
import com.elearning.common.enums.RoleStatus;

import java.util.List;

public interface RoleService {

    Role findOrCreateRole(String roleName);

    Role getRoleById(Long roleId);

    Role getRoleByStatus(RoleStatus roleStatus);

    List<Role> findAllRoles();
}
