package com.wms.userservice.controller;

import com.wms.api.entity.User;
import com.wms.api.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserServiceController {
    private static final String USERINFO = "X-Info";

    private static final String AUTHORIZATION = "Authorization";
    @Resource
    private UserService userService;

    @PostMapping(value = "/sign_in", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> loginController(@RequestHeader(name = USERINFO) String info) {
        return userService.loginService(info);
    }

    @PostMapping(value = "/sign_up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> registerController(@RequestHeader(name = USERINFO) String info) {
        return userService.registerService(info);
    }

    @GetMapping(value = "/user/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUserProfileController(@RequestHeader(name = AUTHORIZATION) String token) {
        return userService.getUserProfileService(token);
    }

    @DeleteMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> logoutController(@RequestHeader(name = AUTHORIZATION) String token) {
        return userService.logoutService(token);
    }

    @PutMapping(value = "/change_password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> changePasswordController(@RequestBody Map<String, String> map, @RequestHeader(name = AUTHORIZATION) String oldToken) {
        return userService.changePasswordService(map.get("new_password"), oldToken);
    }

    @PostMapping(value = "/sub_account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createSubAccountController(@RequestBody User sub) {
        return userService.createSubAccountService(sub);
    }

    @PutMapping(value = "/change_user_profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> changeUserActiveOrPermissionController(@RequestBody Map<String, Integer> userMap) {
        return userService.changeUserActiveOrPermissionService(userMap);
    }

    @DeleteMapping(value = "/user")
    public ResponseEntity<Map<String, Object>> deleteUserController(@RequestParam("id") int id) {
        return userService.deleteUserService(id);
    }
}
