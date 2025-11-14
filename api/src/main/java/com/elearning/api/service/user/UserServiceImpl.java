package com.elearning.api.service.user;

import com.elearning.api.common.ProfilesResponse;
import com.elearning.api.event.AuditEventPublisher;
import com.elearning.api.event.UserChangeEvent;
import com.elearning.api.helper.AuthHelper;
import com.elearning.api.payload.common.ReportCountResponse;
import com.elearning.api.payload.user.ResetPassUserRequest;
import com.elearning.api.payload.user.UserMainResponse;
import com.elearning.api.payload.user.UserRequest;
import com.elearning.api.payload.user.UserResponse;
import com.elearning.api.service.department.DepartmentService;
import com.elearning.api.service.role.RoleService;
import com.elearning.common.components.properties.FileInfoConfig;
import com.elearning.common.domain.dept.Department;
import com.elearning.common.domain.role.Role;
import com.elearning.common.domain.user.IGetUsers;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.AuditAction;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.enums.UserStatus;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.payload.CBS.report.IReportCount;
import com.elearning.common.util.ImageUtil;
import com.elearning.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final AuditEventPublisher auditPublisher;
    private final FileInfoConfig fileInfoConfig;

    @Override
    @Transactional
    public void createUser(UserRequest payload) {
        if(existsByUsername(payload.getUsername())){
            throw new BusinessException(StatusCode.USERNAME_ALREADY_EXIST);
        }

        String status = StringUtils.defaultIfBlank(payload.getStatus().getValue(), UserStatus.ACTIVE.getValue());

        Department department = departmentService.createdDepartment(payload.getDepartment());

        User user = User.builder()
                .fullName(payload.getFullName())
                .username(payload.getUsername())
                .password(passwordEncoder.encode(payload.getPassword()))
                .email(payload.getEmail())
                .department(department)
                .role(roleService.getRoleById(payload.getRoleId()))
                .status(UserStatus.fromValue(status))
                .image(payload.getImage())
                .isSystemGenerate(payload.isSystemGenerate())
                .build();

        user = userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserRequest payload) {
        Department department = departmentService.createdDepartment(payload.getDepartment());

        User existingUser = userRepository.findById(userId).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        List<UserChangeEvent> events = new ArrayList<>();
        existingUser.setFullName(payload.getFullName());

        if (!existingUser.getUsername().equals(payload.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(payload.getUsername(), userId)) {
                throw new BusinessException(StatusCode.USERNAME_ALREADY_EXIST);
            }
            events.add(new UserChangeEvent(userId, AuditAction.USERNAME_UPDATE, existingUser.getUsername(), payload.getUsername()));
            existingUser.setUsername(payload.getUsername());
        }

        if(!existingUser.getEmail().equals(payload.getEmail())){
            events.add(new UserChangeEvent(userId, AuditAction.EMAIL_UPDATE, existingUser.getEmail(), payload.getEmail()));
            existingUser.setEmail(payload.getEmail());
        }

        if (!existingUser.getRole().getId().equals(payload.getRoleId())) {
            Role newRole = roleService.getRoleById(payload.getRoleId());
            events.add(new UserChangeEvent(userId, AuditAction.ROLE_UPDATE, existingUser.getRole().getName(), newRole.getName()));
            existingUser.setRole(newRole);
        }

        existingUser.setStatus(UserStatus.fromValue(payload.getStatus().getValue()));
        existingUser.setImage(payload.getImage());
        existingUser.setDepartment(department);

        // Save the user
        User savedUser = userRepository.save(existingUser);

        // Publish all events at once
        events.forEach(event -> auditPublisher.publishAuditActionChangeEvent(event.getUserId(),event.getAction(),event.getOldValue(),event.getNewValue()));
        return savedUser;
    }

    @Override
    @Transactional
    public void deleteUser(List<Long> userIds) {
        List<User> user = userRepository.findAllById(userIds);
        user.forEach(u -> {
            u.setStatus(UserStatus.DELETED);
            userRepository.save(u);
        });
    }

    @Override
    public UserResponse findUserById(Long userId) {
        var user = userRepository.findUserById(userId).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .image(user.getImage())
                .role(user.getRole())
                .department(user.getDepartment())
                .lastLog(user.getLastLog())
                .build();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Object findAllUsers(String searchValue, String startDate, String endDate, String status, Pageable pageable) throws Throwable{

        var userCount = userRepository.getUserCounts(AuthHelper.getCurrentUserId());
        var userCountByFilterDate = userRepository.getUserCountsByFilterDate(startDate, endDate, searchValue, AuthHelper.getCurrentUserId());
        Page<IGetUsers> usersPage = userRepository.findAllUser(searchValue, startDate, endDate, status,AuthHelper.getCurrentUserId() ,pageable);
        List<UserResponse> users = usersPage.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .image(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), user.getImage()))
                        .role(user.getRole())
                        .department(user.getDepartment())
                        .lastLog(user.getLastLog())
                        .status(getStatusLabel(user.getStatus()))
                        .createdAt(user.getCreatedAt())
                        .lastLogin(user.getLastLogin())
                        .build())
                .collect(Collectors.toList());

        return UserMainResponse.builder()
                .userResponses(users)
                .userCount(userCount)
                .userCountByDate(userCountByFilterDate)
                .page(usersPage)
                .build();
    }

    @Override
    public List<User> findSubAccounts(Long parentUserId) {
        return userRepository.findUserByCreatedByAndStatus(parentUserId, UserStatus.ACTIVE);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void resetPasswrod(Long userId, ResetPassUserRequest payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

//        // Verify old password
//        if (!passwordEncoder.matches(payload.getCurrentPassword(), user.getPassword())) {
//            throw new BusinessException(StatusCode.BAD_CREDENTIALS);
//        }

        // Set new password
        user.setPassword(passwordEncoder.encode(payload.getNewPassword()));
        user.setSystemGenerate(payload.getIsGeneratedPassword());
        user.setLastPasswordChange(Instant.now());

        auditPublisher.publishPasswordRestEvent(userId);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        auditPublisher.publishAuditActionChangeEvent(userId, AuditAction.STATUS_CHANGE,user.getStatus().getValue(),status.getValue());
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(List<Long> userId, UserStatus status) {
        List<User> user = userRepository.findAllUserById(userId);
        user.forEach(userEntity -> {
            userEntity.setStatus(status);
            auditPublisher.publishAuditActionChangeEvent(userEntity.getId(), AuditAction.STATUS_CHANGE,userEntity.getStatus().getValue(),status.getValue());
        });
        userRepository.saveAll(user);
    }

    @Override
    @Transactional
    public void updateLastLoginDateTime(Long userId) {
        userRepository.updateLastLogin(userId,Instant.now());
        auditPublisher.publishLoginEvent(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserProfile() {
        var profile = userRepository.findByIdAndStatus(AuthHelper.getCurrentUserId(), UserStatus.ACTIVE).orElseThrow(()-> new BusinessException(StatusCode.USER_NOT_FOUND));

        return ProfilesResponse.builder()
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .role(profile.getRole().getName())
                .role(profile.getRole().getName())
                .image(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), profile.getImage()))
                .build();
    }

    private String getStatusLabel(String status) {
        if (Objects.equals(status, UserStatus.ACTIVE.getValue())) {
            return UserStatus.ACTIVE.getLabel();
        } else if (Objects.equals(status, UserStatus.INACTIVE.getValue())) {
            return UserStatus.INACTIVE.getLabel();
        } else {
            return UserStatus.DELETED.getLabel();
        }
    }

    @Override
    public List<String> generateUsernameSuggestions(String username) {
        List<String> suggestions = new ArrayList<>();

        suggestions.add(username + new Random().nextInt(100));

        suggestions.add(username + "_" + new Random().nextInt(1000));

        suggestions.add(username.toLowerCase() + "_obp");

        suggestions.add("user_" + username);

        suggestions.add(username + "Pro");

        return suggestions.stream()
                .filter(suggestion -> suggestion.length() >= 3)
                .filter(suggestion -> !existsByUsername(suggestion))
                .distinct()
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCountUserProvider() {
        IReportCount report= userRepository.getReportCount(AuthHelper.getCurrentUserId());
        return ReportCountResponse.builder()
                .userCount(report.getUserCount())
                .providerCount(report.getProviderCount())
                .authConfigCount(report.getAuthConfigCount())
                .apiManagementCount(report.getApiManagementCount())
                .build();
    }
}
