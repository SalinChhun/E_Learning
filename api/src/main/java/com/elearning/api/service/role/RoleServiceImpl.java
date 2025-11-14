package com.elearning.api.service.role;

import com.elearning.common.domain.role.Role;
import com.elearning.common.domain.role.RoleRepository;
import com.elearning.common.enums.RoleStatus;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findOrCreateRole(String roleName) {
        return roleRepository.findByNameAndStatus(roleName, RoleStatus.NORMAL)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(roleName)
                            .status(RoleStatus.NORMAL)
                            .build();
                    return roleRepository.save(newRole);
                });
    }

    @Override
    public Role getRoleById(Long roleId) {
        return roleRepository.findByIdAndStatus(roleId, RoleStatus.NORMAL   )
                .orElseThrow(() -> new BusinessException(StatusCode.ROLE_NOT_FOUND));
    }

    @Override
    public Role getRoleByStatus(RoleStatus roleStatus) {
        return roleRepository.findRoleByStatus(roleStatus)
                .orElseThrow(() -> new BusinessException(StatusCode.ROLE_NOT_FOUND));
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findByStatus(RoleStatus.NORMAL);
    }

}
