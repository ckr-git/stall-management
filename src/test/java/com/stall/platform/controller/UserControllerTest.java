package com.stall.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stall.platform.entity.User;
import com.stall.platform.security.JwtUtil;
import com.stall.platform.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理控制器集成测试
 * 测试模块：模块2-用户管理
 * 测试类型：集成测试、权限测试、UX测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private String adminToken;
    private String userToken;
    private Long testUserId;
    private Long testAdminId;
    
    @BeforeEach
    void setUp() {
        // 创建管理员
        User admin = new User();
        admin.setUsername("user_ctrl_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setNickname("测试管理员");
        userService.save(admin);
        testAdminId = admin.getId();
        adminToken = "Bearer " + jwtUtil.generateToken(admin.getUsername(), admin.getId(), "ADMIN");
        
        // 创建普通用户
        User user = new User();
        user.setUsername("user_ctrl_user_" + System.currentTimeMillis());
        user.setPassword("user123");
        userService.register(user);
        User savedUser = userService.findByUsername(user.getUsername());
        testUserId = savedUser.getId();
        userToken = "Bearer " + jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId(), "USER");
    }
    
    // ========== 用户端接口测试 ==========
    
    @Test
    @Order(1)
    @DisplayName("获取个人资料")
    void testGetProfile() throws Exception {
        mockMvc.perform(get("/user/profile")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
    
    @Test
    @Order(2)
    @DisplayName("获取个人资料-未登录")
    void testGetProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @Order(3)
    @DisplayName("更新个人资料")
    void testUpdateProfile() throws Exception {
        User updateUser = new User();
        updateUser.setNickname("更新后的昵称");
        updateUser.setPhone("13900139001");
        
        mockMvc.perform(put("/user/profile")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 验证更新成功
        User updated = userService.getById(testUserId);
        Assertions.assertEquals("更新后的昵称", updated.getNickname());
    }
    
    @Test
    @Order(4)
    @DisplayName("更新个人资料-不能修改角色")
    void testUpdateProfile_CannotChangeRole() throws Exception {
        User updateUser = new User();
        updateUser.setRole("ADMIN"); // 尝试提升权限
        updateUser.setNickname("测试");
        
        mockMvc.perform(put("/user/profile")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());
        
        // 验证角色没有被修改
        User updated = userService.getById(testUserId);
        Assertions.assertEquals("USER", updated.getRole());
    }
    
    // ========== 管理员接口测试 ==========
    
    @Test
    @Order(5)
    @DisplayName("管理员-获取用户列表")
    void testAdminListUsers() throws Exception {
        mockMvc.perform(get("/user/admin/list")
                .header("Authorization", adminToken)
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }
    
    @Test
    @Order(6)
    @DisplayName("管理员-按关键词搜索用户")
    void testAdminListUsers_WithKeyword() throws Exception {
        mockMvc.perform(get("/user/admin/list")
                .header("Authorization", adminToken)
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("keyword", "user_ctrl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(7)
    @DisplayName("管理员-获取用户详情")
    void testAdminGetUser() throws Exception {
        mockMvc.perform(get("/user/admin/" + testUserId)
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testUserId))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
    
    @Test
    @Order(8)
    @DisplayName("管理员-更新用户信息")
    void testAdminUpdateUser() throws Exception {
        User updateUser = new User();
        updateUser.setNickname("管理员修改的昵称");
        
        mockMvc.perform(put("/user/admin/" + testUserId)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(9)
    @DisplayName("管理员-重置用户密码")
    void testAdminResetPassword() throws Exception {
        mockMvc.perform(put("/user/admin/" + testUserId + "/reset-password")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("密码已重置为123456"));
    }
    
    @Test
    @Order(10)
    @DisplayName("管理员-更新用户状态(禁用)")
    void testAdminUpdateStatus() throws Exception {
        mockMvc.perform(put("/user/admin/" + testUserId + "/status")
                .header("Authorization", adminToken)
                .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        User disabled = userService.getById(testUserId);
        Assertions.assertEquals(0, disabled.getStatus());
    }
    
    @Test
    @Order(11)
    @DisplayName("管理员-删除用户")
    void testAdminDeleteUser() throws Exception {
        // 创建要删除的用户
        User toDelete = new User();
        toDelete.setUsername("to_delete_" + System.currentTimeMillis());
        toDelete.setPassword("test123");
        userService.register(toDelete);
        User saved = userService.findByUsername(toDelete.getUsername());
        
        mockMvc.perform(delete("/user/admin/" + saved.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 验证已删除（逻辑删除）
        User deleted = userService.getById(saved.getId());
        Assertions.assertNull(deleted);
    }
    
    // ========== 权限测试 ==========
    
    @Test
    @Order(12)
    @DisplayName("权限测试-普通用户访问管理接口")
    void testAdminEndpoint_AsUser() throws Exception {
        mockMvc.perform(get("/user/admin/list")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @Order(13)
    @DisplayName("权限测试-未登录访问管理接口")
    void testAdminEndpoint_Unauthorized() throws Exception {
        mockMvc.perform(get("/user/admin/list"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @Order(14)
    @DisplayName("权限测试-普通用户不能重置他人密码")
    void testResetPassword_AsUser() throws Exception {
        mockMvc.perform(put("/user/admin/" + testAdminId + "/reset-password")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
