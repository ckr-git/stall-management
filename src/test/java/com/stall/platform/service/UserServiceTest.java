package com.stall.platform.service;

import com.stall.platform.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务单元测试
 * 测试模块：模块1-账号认证
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String TEST_USERNAME = "testuser_" + System.currentTimeMillis();
    private static final String TEST_PASSWORD = "test123456";
    
    @Test
    @Order(1)
    @DisplayName("用户注册-正常流程")
    void testRegister_Success() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setNickname("测试用户");
        user.setPhone("13800138001");
        
        boolean result = userService.register(user);
        assertTrue(result, "用户注册应该成功");
        
        User savedUser = userService.findByUsername(TEST_USERNAME);
        assertNotNull(savedUser, "用户应该能被查询到");
        assertEquals("USER", savedUser.getRole(), "默认角色应该是USER");
        assertEquals(1, savedUser.getStatus(), "默认状态应该是正常(1)");
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPassword()), "密码应该正确加密");
    }
    
    @Test
    @Order(2)
    @DisplayName("用户注册-用户名重复")
    void testRegister_DuplicateUsername() {
        // 先注册一个用户
        User user1 = new User();
        user1.setUsername("duplicate_user_" + System.currentTimeMillis());
        user1.setPassword(TEST_PASSWORD);
        userService.register(user1);
        
        // 尝试注册相同用户名
        User user2 = new User();
        user2.setUsername(user1.getUsername());
        user2.setPassword("another_password");
        
        boolean result = userService.register(user2);
        assertFalse(result, "重复用户名注册应该失败");
    }
    
    @Test
    @Order(3)
    @DisplayName("根据用户名查询用户")
    void testFindByUsername() {
        String username = "findtest_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword(TEST_PASSWORD);
        userService.register(user);
        
        User found = userService.findByUsername(username);
        assertNotNull(found, "应该能找到用户");
        assertEquals(username, found.getUsername());
        
        User notFound = userService.findByUsername("nonexistent_user");
        assertNull(notFound, "不存在的用户应该返回null");
    }
    
    @Test
    @Order(4)
    @DisplayName("修改密码-正常流程")
    void testUpdatePassword_Success() {
        String username = "pwdtest_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword(TEST_PASSWORD);
        userService.register(user);
        
        User savedUser = userService.findByUsername(username);
        String newPassword = "newpassword123";
        
        boolean result = userService.updatePassword(savedUser.getId(), TEST_PASSWORD, newPassword);
        assertTrue(result, "密码修改应该成功");
        
        User updatedUser = userService.getById(savedUser.getId());
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()), "新密码应该正确");
    }
    
    @Test
    @Order(5)
    @DisplayName("修改密码-旧密码错误")
    void testUpdatePassword_WrongOldPassword() {
        String username = "pwdtest2_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword(TEST_PASSWORD);
        userService.register(user);
        
        User savedUser = userService.findByUsername(username);
        
        boolean result = userService.updatePassword(savedUser.getId(), "wrong_password", "newpassword");
        assertFalse(result, "旧密码错误时修改应该失败");
    }
    
    @Test
    @Order(6)
    @DisplayName("重置密码")
    void testResetPassword() {
        String username = "resettest_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setPassword(TEST_PASSWORD);
        userService.register(user);
        
        User savedUser = userService.findByUsername(username);
        
        boolean result = userService.resetPassword(savedUser.getId());
        assertTrue(result, "重置密码应该成功");
        
        User resetUser = userService.getById(savedUser.getId());
        assertTrue(passwordEncoder.matches("123456", resetUser.getPassword()), "密码应该被重置为123456");
    }
    
    @Test
    @Order(7)
    @DisplayName("分页查询用户列表")
    void testPageList() {
        // 创建测试用户
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("pagelist_" + i + "_" + System.currentTimeMillis());
            user.setPassword(TEST_PASSWORD);
            user.setNickname("分页测试用户" + i);
            userService.register(user);
        }
        
        var page = userService.pageList(1, 10, "分页测试", null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 3, "应该至少有3条记录");
    }
}
