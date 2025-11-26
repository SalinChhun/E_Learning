package com.elearning.api.service;

import com.elearning.api.service.department.DepartmentService;
import com.elearning.api.service.role.RoleService;
import com.elearning.common.domain.dept.Department;
import com.elearning.common.domain.role.Role;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializationService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String ADMIN_FULL_NAME = "Administrator";
    private static final String ADMIN_EMAIL = "admin@elearning.com";
    private static final String ADMIN_DEPARTMENT = "Administration";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting admin user initialization...");
        
        // Check if admin user already exists
        if (userRepository.existsByUsername(ADMIN_USERNAME)) {
            log.info("Admin user already exists. Skipping initialization.");
            return;
        }

        try {
            // Create or get ADMIN role
            Role adminRole = roleService.findOrCreateRole(ADMIN_ROLE_NAME);
            log.info("Admin role found/created: {}", adminRole.getName());

            // Create or get department
            Department department = departmentService.createdDepartment(ADMIN_DEPARTMENT);
            log.info("Department found/created: {}", department.getName());

            // Create admin user
            User adminUser = User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .fullName(ADMIN_FULL_NAME)
                    .email(ADMIN_EMAIL)
                    .role(adminRole)
                    .department(department)
                    .status(UserStatus.ACTIVE)
                    .isSystemGenerate(true)
                    .lastPasswordChange(Instant.now())
                    .build();

            adminUser = userRepository.save(adminUser);
            log.info("Admin user created successfully with ID: {}", adminUser.getId());
            log.info("Admin credentials - Username: {}, Password: {}", ADMIN_USERNAME, ADMIN_PASSWORD);
            
        } catch (Exception e) {
            log.error("Error creating admin user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize admin user", e);
        }
    }
}



