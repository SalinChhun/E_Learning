package com.elearning.common.security;

import com.elearning.common.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String role;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Set<String> permissions;

    public UserPrincipal(User user, List<String> userPermissions) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isActive();
        this.role = user.getRole().getName();

        // Convert permissions to a set for faster lookup
        this.permissions = Set.copyOf(userPermissions);

        // Create authorities from role and permissions
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        if (!userPermissions.isEmpty()) {
            List<GrantedAuthority> permissionAuthorities = userPermissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            auths.addAll(permissionAuthorities);
        }

        this.authorities = auths;
    }


    public UserPrincipal(Long userId, String username, String roleName, List<String> userPermissions) {
        this.userId = userId;
        this.username = username;
        this.password = null;
        this.enabled = true;
        this.role = roleName;

        // Convert permissions to a set for faster lookup
        this.permissions = Set.copyOf(userPermissions);

        // Create authorities from role and permissions
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        if (!userPermissions.isEmpty()) {
            List<GrantedAuthority> permissionAuthorities = userPermissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            auths.addAll(permissionAuthorities);
        }

        this.authorities = auths;
    }

    /**
     * Checks if the user has the specified permission for the resource and action
     * @param resource The resource to check permission for
     * @param action The action to check permission for
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(String resource, String action) {
        // Check for specific permission (resource:action)
        String specificPermission = resource + ":" + action;
        if (permissions.contains(specificPermission)) {
            return true;
        }

        // Check for wildcard permissions (resource:*)
        String wildcardPermission = resource + ":*";
        if (permissions.contains(wildcardPermission)) {
            return true;
        }

        // Check for global permissions (*:*)
        return permissions.contains("*:*");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}