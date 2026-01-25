package com.stall.platform.controller;

import com.stall.platform.common.Result;
import com.stall.platform.entity.User;
import com.stall.platform.security.JwtUtil;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.UserService;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, 
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            
            User user = userService.findByUsername(loginDTO.getUsername());
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("role", user.getRole());
            
            return Result.success(data);
        } catch (AuthenticationException e) {
            return Result.error("用户名或密码错误");
        }
    }
    
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }
    
    @PostMapping("/logout")
    public Result<?> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("登出成功");
    }
    
    @GetMapping("/info")
    public Result<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            User user = userService.getById(loginUser.getUserId());
            if (user != null) {
                user.setPassword(null); // 不返回密码
                return Result.success(user);
            }
        }
        return Result.error(401, "未登录");
    }
    
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            boolean success = userService.updatePassword(loginUser.getUserId(), 
                    passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
            if (success) {
                return Result.success("密码修改成功");
            }
            return Result.error("原密码错误");
        }
        return Result.error(401, "未登录");
    }
    
    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }
    
    @Data
    public static class RegisterDTO {
        private String username;
        private String password;
        private String nickname;
        private String phone;
    }
    
    @Data
    public static class PasswordDTO {
        private String oldPassword;
        private String newPassword;
    }
}
