package com.elearning.api.controller;

import com.elearning.api.service.commoncode.CommonCodeService;
import com.elearning.api.service.role.RoleService;
import com.elearning.api.service.user.UserService;
import com.elearning.common.common.RestApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wba/v1")
@RequiredArgsConstructor
public class CommonController extends RestApiResponse {

    private final CommonCodeService commonCodeService;
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfileUser() {
        return ok(userService.getUserProfile());
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ok(roleService.findAllRoles());
    }

    @GetMapping("/common/codes")
    public ResponseEntity<?> getCommonCodeByGroupCode(
            @RequestParam("group_code") String groupCode ,
            @RequestParam(value = "parent_code", required = false) String parentCode ) throws Throwable {
        return ok(commonCodeService.getCommonCodeByGroupCode(groupCode, parentCode));
    }

}
