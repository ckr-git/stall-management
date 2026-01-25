package com.stall.platform.service;

import com.stall.platform.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 反馈投诉服务单元测试
 * 测试模块：模块5-反馈投诉
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeedbackServiceTest {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private UserService userService;
    
    private Long testUserId;
    private Long testAdminId;
    
    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("feedback_user_" + System.currentTimeMillis());
        user.setPassword("test123");
        userService.register(user);
        testUserId = userService.findByUsername(user.getUsername()).getId();
        
        User admin = new User();
        admin.setUsername("feedback_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setNickname("管理员");
        userService.save(admin);
        testAdminId = admin.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("提交反馈-投诉类型")
    void testSubmitFeedback_Complaint() {
        Feedback feedback = createTestFeedback(1); // 投诉
        
        boolean result = feedbackService.submit(feedback);
        assertTrue(result, "反馈提交应该成功");
        assertEquals(0, feedback.getStatus(), "初始状态应该是待处理(0)");
    }
    
    @Test
    @Order(2)
    @DisplayName("提交反馈-建议类型")
    void testSubmitFeedback_Suggestion() {
        Feedback feedback = createTestFeedback(2); // 建议
        
        boolean result = feedbackService.submit(feedback);
        assertTrue(result, "反馈提交应该成功");
    }
    
    @Test
    @Order(3)
    @DisplayName("提交反馈-咨询类型")
    void testSubmitFeedback_Inquiry() {
        Feedback feedback = createTestFeedback(3); // 咨询
        
        boolean result = feedbackService.submit(feedback);
        assertTrue(result, "反馈提交应该成功");
    }
    
    @Test
    @Order(4)
    @DisplayName("查询我的反馈列表")
    void testPageList() {
        for (int i = 1; i <= 3; i++) {
            Feedback feedback = createTestFeedback(i);
            feedbackService.submit(feedback);
        }
        
        var page = feedbackService.pageList(1, 10, testUserId, null, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 3, "应该至少有3条记录");
    }
    
    @Test
    @Order(5)
    @DisplayName("按类型筛选反馈")
    void testPageListByType() {
        for (int i = 0; i < 3; i++) {
            Feedback feedback = createTestFeedback(1); // 都是投诉
            feedbackService.submit(feedback);
        }
        
        var page = feedbackService.pageList(1, 10, testUserId, 1, null);
        assertTrue(page.getRecords().stream().allMatch(f -> f.getType() == 1), 
                "所有结果类型应该是投诉");
    }
    
    @Test
    @Order(6)
    @DisplayName("回复反馈")
    void testReplyFeedback() {
        Feedback feedback = createTestFeedback(1);
        feedbackService.submit(feedback);
        
        boolean result = feedbackService.reply(feedback.getId(), "已收到您的反馈，正在处理中", testAdminId);
        assertTrue(result, "回复应该成功");
        
        Feedback replied = feedbackService.getById(feedback.getId());
        assertEquals("已收到您的反馈，正在处理中", replied.getReply());
        assertEquals(2, replied.getStatus(), "状态应该是已处理(2)");
        assertEquals(testAdminId, replied.getHandlerId());
        assertNotNull(replied.getHandleTime());
    }
    
    @Test
    @Order(7)
    @DisplayName("更新反馈状态")
    void testUpdateStatus() {
        Feedback feedback = createTestFeedback(1);
        feedbackService.submit(feedback);
        
        boolean result = feedbackService.updateStatus(feedback.getId(), 1); // 处理中
        assertTrue(result, "状态更新应该成功");
        
        Feedback updated = feedbackService.getById(feedback.getId());
        assertEquals(1, updated.getStatus());
    }
    
    @Test
    @Order(8)
    @DisplayName("获取反馈详情")
    void testGetDetailById() {
        Feedback feedback = createTestFeedback(1);
        feedbackService.submit(feedback);
        
        Feedback detail = feedbackService.getDetailById(feedback.getId());
        assertNotNull(detail, "应该能获取到详情");
        assertNotNull(detail.getUsername(), "应该包含用户名");
    }
    
    private Feedback createTestFeedback(int type) {
        Feedback feedback = new Feedback();
        feedback.setUserId(testUserId);
        feedback.setType(type);
        feedback.setTitle("测试反馈标题_" + System.currentTimeMillis());
        feedback.setContent("这是测试反馈内容");
        feedback.setContactPhone("13800138000");
        return feedback;
    }
}
