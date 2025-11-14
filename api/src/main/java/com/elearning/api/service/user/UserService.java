package com.elearning.api.service.user;

import com.elearning.api.payload.user.ResetPassUserRequest;
import com.elearning.api.payload.user.UserRequest;
import com.elearning.api.payload.user.UserResponse;
import com.elearning.common.domain.user.User;
import com.elearning.common.enums.UserStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(UserRequest payload);

    User updateUser(Long userId, UserRequest user);

    void deleteUser(List<Long> userId);

    UserResponse findUserById(Long userId);

    Optional<User> findByUsername(String username);

    Object findAllUsers(String searchValue, String startDate, String endDate, String status, Pageable pageable) throws Throwable;

    List<User> findSubAccounts(Long parentUserId);

    boolean existsByUsername(String username);

    void resetPasswrod(Long userId, ResetPassUserRequest payload);

    void updateStatus(Long userId, UserStatus status);

    void updateUserStatus(List<Long> userId, UserStatus status);

    void updateLastLoginDateTime(Long userId);

    Object getUserProfile();

    List<String> generateUsernameSuggestions(String username);

    Object getCountUserProvider();

}
