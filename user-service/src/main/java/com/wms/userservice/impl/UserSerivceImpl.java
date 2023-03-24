package com.wms.userservice.impl;

import com.wms.api.entity.User;
import com.wms.api.service.UserService;
import com.wms.api.util.JWTUtil;
import com.wms.userservice.mapper.UserMapper;
import com.wms.api.util.AuthorizationDecryptor;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.wms.api.util.PasswordEncodeAndVerfiy.encode;
import static com.wms.api.util.PasswordEncodeAndVerfiy.verify;

@Service
public class UserSerivceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserSerivceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public ResponseEntity<Map<String, Object>> loginService(String info) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(AuthorizationDecryptor.decrypt(info));
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            User user = userMapper.findUserByUsername(username);
            if (user == null || !verify(password, user.password())) {
                map.put("code", 1);
                map.put("message", "email or password error");
                return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
            } else if (user.is_active() == 1) {
                map.put("code", 1);
                map.put("message", "user has been blocked");
                return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
            }
            String token = JWTUtil.create(user.username(), user.password());
            map.put("code", 0);
            map.put("message", "login success");
            map.put("token", token);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> registerService(String info) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(AuthorizationDecryptor.decrypt(info));
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            User user = userMapper.findUserByUsername(username);
            if (user != null) {
                map.put("code", 1);
                map.put("message", "user is exist");
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else if (username== null || password == null) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            String encodePassword = encode(password);
            userMapper.addUser(username, encodePassword, 0, 0);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserProfileService(String token) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            User user = userMapper.findUserByUsername(JWTUtil.getUser(token));
            map.put("code", 0);
            Map<String, Object> map1 = new ConcurrentHashMap<>();
            map1.put("id", user.id());
            map1.put("username", user.username());
            map1.put("is_active", user.is_active());
            map1.put("permission", user.permission());
            map.put("data", new TreeMap<>(map1));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> logoutService(String token) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            //把旧的token存入redis的黑名单管理，等待超时后自动过期
            redisTemplate.opsForValue().setIfAbsent(token, 1, 1, TimeUnit.HOURS);
            map.put("code", 0);
            map.put("message", "logout success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> changePasswordService(String newPassword, String oldToken) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            //先通过旧token获取用户信息
            User user = userMapper.findUserByUsername(JWTUtil.getUser(oldToken));
            //新密码加密
            String encodePassword = encode(newPassword);
            //修改密码
            userMapper.changePassword(user.id(), encodePassword);
            //签发新的token
            String newToken = JWTUtil.create(user.username(), encodePassword);
            //把旧的token存入redis的黑名单管理，等待超时后自动过期
            redisTemplate.opsForValue().setIfAbsent(oldToken, 1, 24, TimeUnit.HOURS);
            map.put("code", 0);
            map.put("message", "password has been changed successfully, please re-login");
            map.put("token", newToken);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createSubAccountService(User sub) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            if (sub.permission() == 0) {
                map.put("code", 1);
                map.put("message", "cannot create administer");
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else if (sub.username() == null || sub.password() == null) {
                map.put("code", 1);
                map.put("message", "bad request");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            userMapper.addUser(sub.username(), encode(sub.password()), 0, sub.permission());
            map.put("code", 0);
            map.put("message", "create sub-account success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> changeUserActiveOrPermissionService(Map<String, Integer> userMap) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            userMapper.changeUserActiveOrPermission(userMap.get("is_active"), userMap.get("permission"), userMap.get("id"));
            map.put("code", 0);
            map.put("message", "change sub-account success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteUserService(int id) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        try {
            userMapper.deleteUser(id);
            map.put("code", 0);
            map.put("message", "delete success");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
