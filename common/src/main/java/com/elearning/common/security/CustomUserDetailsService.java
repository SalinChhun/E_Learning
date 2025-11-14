package com.elearning.common.security;

import com.elearning.common.domain.rolepermission.RolePermissionRepository;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.Status;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if(user.getStatus().equals(UserStatus.INACTIVE)){
            throw new DisabledException(StatusCode.USER_DISABLED.getMessage());
        } else if(user.getStatus().equals(UserStatus.DELETED)){
            throw new UsernameNotFoundException(StatusCode.USER_NOT_FOUND.getMessage());
        }

        // Get permissions for the user's role
        List<String> permissions = getUserPermissions(user);

        return new UserPrincipal(user, permissions);
    }

    private List<String> getUserPermissions(User user) {
        return rolePermissionRepository.findPermissionsByRoleIdAndStatus(user.getRole().getId(), Status.NORMAL)
                .stream()
                .map(permission -> permission.getResource() + ":" + permission.getAction())
                .collect(Collectors.toList());
    }
}