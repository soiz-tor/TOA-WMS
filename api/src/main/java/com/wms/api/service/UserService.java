package com.wms.api.service;

import com.wms.api.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
    ResponseEntity<Map<String, Object>> loginService(String info);

    ResponseEntity<Map<String, Object>> registerService(String info);

    ResponseEntity<Map<String, Object>> getUserProfileService(String token);

    ResponseEntity<Map<String, Object>> logoutService(String token);

    ResponseEntity<Map<String, Object>> changePasswordService(String newPassword, String oldToken);

    ResponseEntity<Map<String, Object>> createSubAccountService(User sub);

    ResponseEntity<Map<String, Object>> changeUserActiveOrPermissionService(Map<String, Integer> userMap);

    ResponseEntity<Map<String, Object>> deleteUserService(int id);

}
