package com.stall.platform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stall.platform.entity.User;
import com.stall.platform.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 安全集成测试
 * 测试类型：C1-身份认证、C2-权限控制、C3-输入验证、C4-密码安全
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String testUsername;
    private String testPassword = "testPassword123";
    
    @BeforeEach
    void setUp() {
        testUsername = "security_test_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(testUsername);
        user.setPassword(testPassword);
        userService.register(user);
    }
    
    // ========== C1: 身份认证测试 ==========
    
    @Test
    @Order(1)
    @DisplayName("C1-JWT Token生成验证")
    void testJwtTokenGeneration() {
        String token = jwtUtil.generateToken(testUsername, 1L, "USER");
        
        assertNotNull(token, "Token不应为空");
        assertTrue(jwtUtil.validateToken(token), "Token应该有效");
        assertEquals(testUsername, jwtUtil.getUsernameFromToken(token), "用户名应该正确");
        assertEquals("USER", jwtUtil.getRoleFromToken(token), "角色应该正确");
    }
    
    @Test
    @Order(2)
    @DisplayName("C1-无效Token验证")
    void testInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid_token"), "无效Token应该验证失败");
        assertFalse(jwtUtil.validateToken(""), "空Token应该验证失败");
        assertFalse(jwtUtil.validateToken(null), "null Token应该验证失败");
    }
    
    @Test
    @Order(3)
    @DisplayName("C1-篡改Token验证")
    void testTamperedToken() {
        String token = jwtUtil.generateToken(testUsername, 1L, "USER");
        String tamperedToken = token + "tampered";
        
        assertFalse(jwtUtil.validateToken(tamperedToken), "篡改的Token应该验证失败");
    }
    
    @Test
    @Order(4)
    @DisplayName("C1-无Token访问受保护接口")
    void testAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @Order(5)
    @DisplayName("C1-错误格式Token访问")
    void testAccessWithMalformedToken() throws Exception {
        mockMvc.perform(get("/user/profile")
                .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isForbidden());
    }
    
    // ========== C2: 权限控制测试 ==========
    
    @Test
    @Order(6)
    @DisplayName("C2-用户角色不能访问管理员接口")
    void testUserCannotAccessAdminEndpoint() throws Exception {
        User user = userService.findByUsername(testUsername);
        String userToken = "Bearer " + jwtUtil.generateToken(testUsername, user.getId(), "USER");
        
        mockMvc.perform(get("/user/admin/list")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @Order(7)
    @DisplayName("C2-管理员可以访问管理员接口")
    void testAdminCanAccessAdminEndpoint() throws Exception {
        User admin = new User();
        admin.setUsername("admin_test_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userService.save(admin);
        
        String adminToken = "Bearer " + jwtUtil.generateToken(admin.getUsername(), admin.getId(), "ADMIN");
        
        mockMvc.perform(get("/user/admin/list")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @Order(8)
    @DisplayName("C2-公开接口无需认证")
    void testPublicEndpointAccessible() throws Exception {
        mockMvc.perform(get("/stall/list"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/announcement/list"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/stall-type/list"))
                .andExpect(status().isOk());
    }
    
    @Test
    @Order(9)
    @DisplayName("C2-禁用用户不能登录")
    void testDisabledUserCannotLogin() throws Exception {
        User user = userService.findByUsername(testUsername);
        user.setStatus(0); // 禁用
        userService.updateById(user);
        
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", testUsername);
        loginRequest.put("password", testPassword);
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
    
    // ========== C3: 输入验证测试 ==========
    
    @Test
    @Order(10)
    @DisplayName("C3-SQL注入防护-用户名")
    void testSqlInjectionInUsername() throws Exception {
        Map<String, String> maliciousRequest = new HashMap<>();
        maliciousRequest.put("username", "admin'; DROP TABLE user; --");
        maliciousRequest.put("password", "test123");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500)); // 应该是认证失败，不是SQL错误
        
        // 验证表仍然存在
        assertNotNull(userService.findByUsername(testUsername), "用户表应该正常");
    }
    
    @Test
    @Order(11)
    @DisplayName("C3-XSS防护-注册")
    void testXssInRegistration() throws Exception {
        Map<String, String> xssRequest = new HashMap<>();
        xssRequest.put("username", "xss_test_" + System.currentTimeMillis());
        xssRequest.put("password", "test123");
        xssRequest.put("nickname", "<script>alert('XSS')</script>");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 注：实际XSS防护应该在前端或输出层处理，这里验证不会导致系统错误
    }
    
    @Test
    @Order(12)
    @DisplayName("C3-特殊字符处理")
    void testSpecialCharacters() throws Exception {
        Map<String, String> specialRequest = new HashMap<>();
        specialRequest.put("username", "special_" + System.currentTimeMillis());
        specialRequest.put("password", "test123!@#$%^&*()");
        specialRequest.put("nickname", "测试中文昵称");
        specialRequest.put("phone", "+86-13800138000");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpect(status().isOk());
    }
    
    // ========== C4: 密码安全测试 ==========
    
    @Test
    @Order(13)
    @DisplayName("C4-密码BCrypt加密存储")
    void testPasswordEncryption() {
        User user = userService.findByUsername(testUsername);
        
        assertNotNull(user.getPassword(), "密码不应为空");
        assertNotEquals(testPassword, user.getPassword(), "密码不应明文存储");
        assertTrue(user.getPassword().startsWith("$2a$"), "密码应该是BCrypt格式");
        assertTrue(passwordEncoder.matches(testPassword, user.getPassword()), "密码应该可以验证");
    }
    
    @Test
    @Order(14)
    @DisplayName("C4-密码不在API响应中返回")
    void testPasswordNotInResponse() throws Exception {
        User user = userService.findByUsername(testUsername);
        String token = "Bearer " + jwtUtil.generateToken(testUsername, user.getId(), "USER");
        
        mockMvc.perform(get("/user/profile")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.password").doesNotExist());
        
        mockMvc.perform(get("/auth/info")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
    
    @Test
    @Order(15)
    @DisplayName("C4-密码修改需要验证旧密码")
    void testPasswordChangeRequiresOldPassword() throws Exception {
        User user = userService.findByUsername(testUsername);
        String token = "Bearer " + jwtUtil.generateToken(testUsername, user.getId(), "USER");
        
        Map<String, String> wrongOldPassword = new HashMap<>();
        wrongOldPassword.put("oldPassword", "wrong_password");
        wrongOldPassword.put("newPassword", "newPassword123");
        
        mockMvc.perform(put("/auth/password")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongOldPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("原密码错误"));
    }
    
    @Test
    @Order(16)
    @DisplayName("C4-管理员重置密码为默认值")
    void testAdminResetPasswordToDefault() {
        User user = userService.findByUsername(testUsername);
        
        boolean result = userService.resetPassword(user.getId());
        assertTrue(result, "重置密码应该成功");
        
        User updated = userService.getById(user.getId());
        assertTrue(passwordEncoder.matches("123456", updated.getPassword()), 
                "密码应该重置为123456");
    }
    
    // ========== UX可用性测试 ==========
    
    @Test
    @Order(17)
    @DisplayName("UX-统一响应格式")
    void testUnifiedResponseFormat() throws Exception {
        // 成功响应
        mockMvc.perform(get("/stall/list"))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
        
        // 错误响应
        mockMvc.perform(get("/stall/99999"))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @Order(18)
    @DisplayName("UX-分页参数默认值")
    void testPaginationDefaults() throws Exception {
        mockMvc.perform(get("/stall/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10));
    }
}
