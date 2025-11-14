package com.elearning.api.controller;

import com.elearning.api.payload.MultiSortBuilder;
import com.elearning.api.payload.user.ResetPassUserRequest;
import com.elearning.api.payload.user.UserRequest;
import com.elearning.api.payload.user.UsernameRequest;
import com.elearning.api.service.user.UserService;
import com.elearning.common.common.RestApiResponse;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wba/v1/users")
@RequiredArgsConstructor
public class UserController extends RestApiResponse {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "search_value", required = false) String searchValue,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate,
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) throws Throwable {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pages = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));

        return ok(userService.findAllUsers(searchValue, startDate, endDate, status, pages));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return ok(userService.findUserById(userId));
    }

    @PostMapping("/username-check")
    public ResponseEntity<?> checkUsername(@RequestBody @Valid UsernameRequest request) {
        if(userService.existsByUsername(request.username())) {
            List<String> suggestions = userService.generateUsernameSuggestions(request.username());

            Map<String, Object> response = new HashMap<>();
            response.put("code", StatusCode.USERNAME_ALREADY_EXIST.getCode());
            response.put("message", StatusCode.USERNAME_ALREADY_EXIST.getMessage());
            response.put("data", suggestions);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ok();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest payload) {
        userService.createUser(payload);
        return ok();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserRequest user) {
        userService.updateUser(userId, user);
        return ok();
    }

    //  ? MARK AS DELETE
    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@RequestBody List<Long> userIds) {
        userService.deleteUser(userIds);
        return ok();
    }

    @PatchMapping("/inactive")
    public ResponseEntity<?> updateStatusUserInactive(@RequestBody List<Long> userIds) {
        userService.updateUserStatus(userIds, UserStatus.INACTIVE);
        return ok();
    }

    @PatchMapping("/active")
    public ResponseEntity<?> updateStatusUserActive(@RequestBody List<Long> userIds) {
        userService.updateUserStatus(userIds, UserStatus.ACTIVE);
        return ok();
    }

    @PutMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPasswordUser(@PathVariable Long userId, @Valid @RequestBody ResetPassUserRequest payload) {
        userService.resetPasswrod(userId,payload);
        return ok();
    }
}
