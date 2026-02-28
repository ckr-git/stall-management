package com.stall.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stall.platform.entity.Stall;
import com.stall.platform.entity.StallType;
import com.stall.platform.entity.User;
import com.stall.platform.security.JwtUtil;
import com.stall.platform.service.StallService;
import com.stall.platform.service.StallTypeService;
import com.stall.platform.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 摊位控制器集成测试
 * 测试：API接口正确性、权限控制
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StallControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private StallTypeService stallTypeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private String adminToken;
    private String userToken;
    private Long testTypeId;
    
    @BeforeEach
    void setUp() {
        // 创建管理员
        User admin = new User();
        admin.setUsername("stall_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userService.save(admin);
        adminToken = "Bearer " + jwtUtil.generateToken(admin.getUsername(), admin.getId(), "ADMIN");
        
        // 创建普通用户
        User user = new User();
        user.setUsername("stall_user_" + System.currentTimeMillis());
        user.setPassword("user123");
        userService.register(user);
        User savedUser = userService.findByUsername(user.getUsername());
        userToken = "Bearer " + jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId(), "USER");
        
        // 创建摊位类型
        StallType type = new StallType();
        type.setName("API测试类型_" + System.currentTimeMillis());
        stallTypeService.save(type);
        testTypeId = type.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("公开接口-获取摊位列表")
    void testGetStallList_Public() throws Exception {
        // 创建测试数据
        Stall stall = createTestStall();
        stallService.save(stall);
        
        mockMvc.perform(get("/stall/list")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }
    
    @Test
    @Order(2)
    @DisplayName("公开接口-获取摊位详情")
    void testGetStallById_Public() throws Exception {
        Stall stall = createTestStall();
        stallService.save(stall);
        
        mockMvc.perform(get("/stall/" + stall.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(stall.getId()));
    }
    
    @Test
    @Order(3)
    @DisplayName("公开接口-获取可用摊位")
    void testGetAvailableStalls_Public() throws Exception {
        Stall stall = createTestStall();
        stall.setStatus(0);
        stallService.save(stall);
        
        mockMvc.perform(get("/stall/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @Order(4)
    @DisplayName("管理员接口-添加摊位")
    void testAddStall_Admin() throws Exception {
        Stall stall = createTestStall();
        
        mockMvc.perform(post("/stall/admin")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stall)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(5)
    @DisplayName("管理员接口-更新摊位")
    void testUpdateStall_Admin() throws Exception {
        Stall stall = createTestStall();
        stallService.save(stall);
        
        stall.setName("更新后的摊位名称");
        
        mockMvc.perform(put("/stall/admin/" + stall.getId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stall)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(6)
    @DisplayName("管理员接口-更新摊位状态")
    void testUpdateStallStatus_Admin() throws Exception {
        Stall stall = createTestStall();
        stallService.save(stall);
        
        mockMvc.perform(put("/stall/admin/" + stall.getId() + "/status")
                .header("Authorization", adminToken)
                .param("status", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(7)
    @DisplayName("管理员接口-删除摊位")
    void testDeleteStall_Admin() throws Exception {
        Stall stall = createTestStall();
        stallService.save(stall);
        
        mockMvc.perform(delete("/stall/admin/" + stall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    @Order(8)
    @DisplayName("权限测试-普通用户访问管理接口")
    void testAdminEndpoint_AsUser() throws Exception {
        Stall stall = createTestStall();
        
        mockMvc.perform(post("/stall/admin")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stall)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @Order(9)
    @DisplayName("权限测试-未登录访问管理接口")
    void testAdminEndpoint_Unauthorized() throws Exception {
        Stall stall = createTestStall();

        mockMvc.perform(post("/stall/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stall)))
                .andExpect(status().isUnauthorized());
    }
    
    private Stall createTestStall() {
        Stall stall = new Stall();
        stall.setStallNo("API_" + System.currentTimeMillis());
        stall.setName("API测试摊位");
        stall.setTypeId(testTypeId);
        stall.setLocation("API测试位置");
        stall.setArea(new BigDecimal("10.5"));
        stall.setRentPrice(new BigDecimal("1200.00"));
        stall.setStatus(0);
        return stall;
    }
}
