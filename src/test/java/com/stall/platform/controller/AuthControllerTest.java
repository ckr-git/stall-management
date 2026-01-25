package com.stall.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stall.platform.entity.User;
import com.stall.platform.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 * 测试：API接口正确性、安全性
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserService userService;
    
    private static String testToken;
    
    @Test
    @Order(1)
    @DisplayName("用户注册接口测试")
    void testRegister() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "api_test_user_" + System.currentTimeMillis());
        request.put("password", "test123456");
        request.put("nickname", "API测试用户");
        request.put("phone", "13800138000");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }
    
    @Test
    @Order(2)
    @DisplayName("用户注册-用户名重复")
    void testRegister_DuplicateUsername() throws Exception {
        String username = "duplicate_api_" + System.currentTimeMillis();
        
        // 先注册一次
        User user = new User();
        user.setUsername(username);
        user.setPassword("test123");
        userService.register(user);
        
        // 再次注册相同用户名
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", "test456");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }
    
    @Test
    @Order(3)
    @DisplayName("用户登录接口测试")
    void testLogin() throws Exception {
        String username = "login_test_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword("test123456");
        userService.register(user);
        
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", "test123456");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value(username));
    }
    
    @Test
    @Order(4)
    @DisplayName("用户登录-密码错误")
    void testLogin_WrongPassword() throws Exception {
        String username = "wrong_pwd_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword("correct_password");
        userService.register(user);
        
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", "wrong_password");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }
    
    @Test
    @Order(5)
    @DisplayName("未登录访问受保护接口")
    void testAccessProtectedEndpoint_Unauthorized() throws Exception {
        mockMvc.perform(get("/auth/info"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @Order(6)
    @DisplayName("登出接口测试")
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
