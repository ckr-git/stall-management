package com.stall.platform.service;

import com.stall.platform.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 摊位申请服务单元测试
 * 测试模块：模块3-申请审核
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StallApplicationServiceTest {
    
    @Autowired
    private StallApplicationService applicationService;
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private StallTypeService stallTypeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RentalRecordService rentalRecordService;
    
    private Long testUserId;
    private Long testStallId;
    private Long testAdminId;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("apptest_user_" + System.currentTimeMillis());
        user.setPassword("test123");
        userService.register(user);
        testUserId = userService.findByUsername(user.getUsername()).getId();
        
        // 创建管理员
        User admin = new User();
        admin.setUsername("apptest_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userService.save(admin);
        testAdminId = admin.getId();
        
        // 创建测试摊位类型
        StallType type = new StallType();
        type.setName("申请测试类型_" + System.currentTimeMillis());
        stallTypeService.save(type);
        
        // 创建测试摊位
        Stall stall = new Stall();
        stall.setStallNo("APPTEST_" + System.currentTimeMillis());
        stall.setName("申请测试摊位");
        stall.setTypeId(type.getId());
        stall.setRentPrice(new BigDecimal("1000.00"));
        stall.setStatus(0);
        stallService.save(stall);
        testStallId = stall.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("提交摊位申请-正常流程")
    void testSubmitApplication() {
        StallApplication application = createTestApplication();
        
        boolean result = applicationService.submit(application);
        assertTrue(result, "申请提交应该成功");
        assertNotNull(application.getId(), "应该生成ID");
        assertNotNull(application.getApplicationNo(), "应该生成申请编号");
        assertTrue(application.getApplicationNo().startsWith("APP"), "申请编号应以APP开头");
        assertEquals(0, application.getStatus(), "初始状态应该是待审核(0)");
    }
    
    @Test
    @Order(2)
    @DisplayName("查询我的申请列表")
    void testPageList() {
        // 提交几个申请
        for (int i = 0; i < 3; i++) {
            StallApplication app = createTestApplication();
            applicationService.submit(app);
        }
        
        var page = applicationService.pageList(1, 10, testUserId, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 3, "应该至少有3条记录");
        assertTrue(page.getRecords().stream().allMatch(a -> a.getUserId().equals(testUserId)), 
                "所有记录应该属于测试用户");
    }
    
    @Test
    @Order(3)
    @DisplayName("获取申请详情")
    void testGetDetailById() {
        StallApplication application = createTestApplication();
        applicationService.submit(application);
        
        StallApplication detail = applicationService.getDetailById(application.getId());
        assertNotNull(detail, "应该能获取到详情");
        assertNotNull(detail.getUsername(), "应该包含用户名");
        assertNotNull(detail.getStallName(), "应该包含摊位名称");
    }
    
    @Test
    @Order(4)
    @DisplayName("取消申请-正常流程")
    void testCancelApplication() {
        StallApplication application = createTestApplication();
        applicationService.submit(application);
        
        boolean result = applicationService.cancel(application.getId(), testUserId);
        assertTrue(result, "取消申请应该成功");
        
        StallApplication cancelled = applicationService.getById(application.getId());
        assertEquals(3, cancelled.getStatus(), "状态应该是已取消(3)");
    }
    
    @Test
    @Order(5)
    @DisplayName("取消申请-非本人申请")
    void testCancelApplication_NotOwner() {
        StallApplication application = createTestApplication();
        applicationService.submit(application);
        
        // 尝试用其他用户取消
        boolean result = applicationService.cancel(application.getId(), testAdminId);
        assertFalse(result, "非本人不能取消申请");
    }
    
    @Test
    @Order(6)
    @DisplayName("审核申请-通过")
    void testReviewApplication_Approve() {
        StallApplication application = createTestApplication();
        applicationService.submit(application);
        
        boolean result = applicationService.review(application.getId(), 1, "审核通过", testAdminId);
        assertTrue(result, "审核应该成功");
        
        StallApplication reviewed = applicationService.getById(application.getId());
        assertEquals(1, reviewed.getStatus(), "状态应该是审核通过(1)");
        assertEquals("审核通过", reviewed.getReviewOpinion());
        assertEquals(testAdminId, reviewed.getReviewerId());
        assertNotNull(reviewed.getReviewTime(), "应该有审核时间");
        
        // 检查摊位状态是否更新
        Stall stall = stallService.getById(testStallId);
        assertEquals(1, stall.getStatus(), "摊位状态应该更新为已租用(1)");
    }
    
    @Test
    @Order(7)
    @DisplayName("审核申请-拒绝")
    void testReviewApplication_Reject() {
        // 创建新摊位避免冲突
        Stall newStall = new Stall();
        newStall.setStallNo("REJECT_" + System.currentTimeMillis());
        newStall.setName("拒绝测试摊位");
        newStall.setRentPrice(new BigDecimal("1000.00"));
        newStall.setStatus(0);
        stallService.save(newStall);
        
        StallApplication application = new StallApplication();
        application.setUserId(testUserId);
        application.setStallId(newStall.getId());
        application.setStartDate(LocalDate.now().plusDays(1));
        application.setEndDate(LocalDate.now().plusMonths(3));
        application.setBusinessType("零售");
        application.setReason("测试申请");
        applicationService.submit(application);
        
        boolean result = applicationService.review(application.getId(), 2, "不符合条件", testAdminId);
        assertTrue(result, "审核应该成功");
        
        StallApplication reviewed = applicationService.getById(application.getId());
        assertEquals(2, reviewed.getStatus(), "状态应该是审核拒绝(2)");
        
        // 摊位状态不应改变
        Stall stall = stallService.getById(newStall.getId());
        assertEquals(0, stall.getStatus(), "摊位状态应保持空闲(0)");
    }
    
    @Test
    @Order(8)
    @DisplayName("审核已处理的申请-应该失败")
    void testReviewApplication_AlreadyProcessed() {
        StallApplication application = createTestApplication();
        applicationService.submit(application);
        
        // 先取消
        applicationService.cancel(application.getId(), testUserId);
        
        // 尝试审核已取消的申请
        boolean result = applicationService.review(application.getId(), 1, "尝试审核", testAdminId);
        assertFalse(result, "不能审核已处理的申请");
    }
    
    private StallApplication createTestApplication() {
        StallApplication application = new StallApplication();
        application.setUserId(testUserId);
        application.setStallId(testStallId);
        application.setStartDate(LocalDate.now().plusDays(1));
        application.setEndDate(LocalDate.now().plusMonths(3));
        application.setBusinessType("餐饮");
        application.setReason("测试申请理由");
        return application;
    }
}
