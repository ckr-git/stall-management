package com.stall.platform.integration;

import com.stall.platform.entity.*;
import com.stall.platform.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 端到端业务流程集成测试
 * 测试类型：回归测试、功能测试（需求覆盖）
 * 覆盖完整业务流程的正确性和数据一致性
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusinessFlowIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private StallTypeService stallTypeService;
    
    @Autowired
    private StallApplicationService applicationService;
    
    @Autowired
    private RentalRecordService rentalRecordService;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private HygieneInspectionService hygieneInspectionService;
    
    private Long userId;
    private Long adminId;
    private Long stallId;
    private Long typeId;
    
    @BeforeEach
    void setUp() {
        // 创建用户
        User user = new User();
        user.setUsername("flow_user_" + System.currentTimeMillis());
        user.setPassword("test123");
        user.setNickname("流程测试用户");
        user.setPhone("13800138001");
        userService.register(user);
        userId = userService.findByUsername(user.getUsername()).getId();
        
        // 创建管理员
        User admin = new User();
        admin.setUsername("flow_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setNickname("流程测试管理员");
        userService.save(admin);
        adminId = admin.getId();
        
        // 创建摊位类型
        StallType type = new StallType();
        type.setName("流程测试类型_" + System.currentTimeMillis());
        type.setDescription("用于流程测试");
        stallTypeService.save(type);
        typeId = type.getId();
        
        // 创建摊位
        Stall stall = new Stall();
        stall.setStallNo("FLOW_" + System.currentTimeMillis());
        stall.setName("流程测试摊位");
        stall.setTypeId(typeId);
        stall.setLocation("测试位置");
        stall.setArea(new BigDecimal("20.0"));
        stall.setRentPrice(new BigDecimal("1500.00"));
        stall.setStatus(0); // 空闲
        stallService.save(stall);
        stallId = stall.getId();
    }
    
    // ========== 完整申请-审核-租赁流程测试 ==========
    
    @Test
    @Order(1)
    @DisplayName("E2E: 完整摊位申请流程 - 申请->审核通过->创建租赁记录")
    void testCompleteApplicationFlow_Approved() {
        // 1. 用户提交申请
        StallApplication application = new StallApplication();
        application.setUserId(userId);
        application.setStallId(stallId);
        application.setStartDate(LocalDate.now().plusDays(7));
        application.setEndDate(LocalDate.now().plusMonths(6));
        application.setBusinessType("餐饮");
        application.setReason("申请经营小吃摊位");
        boolean submitResult = applicationService.submit(application);
        assertTrue(submitResult, "申请提交应该成功");
        assertEquals(0, application.getStatus(), "初始状态应该是待审核");
        
        // 2. 管理员审核通过
        boolean reviewResult = applicationService.review(
            application.getId(), 1, "符合条件，审核通过", adminId);
        assertTrue(reviewResult, "审核应该成功");
        
        // 3. 验证申请状态更新
        StallApplication reviewed = applicationService.getById(application.getId());
        assertEquals(1, reviewed.getStatus(), "状态应该是审核通过");
        assertEquals(adminId, reviewed.getReviewerId(), "审核人ID应该正确");
        assertNotNull(reviewed.getReviewTime(), "应该有审核时间");
        
        // 4. 验证摊位状态更新为已租用
        Stall stall = stallService.getById(stallId);
        assertEquals(1, stall.getStatus(), "摊位状态应该更新为已租用");
        
        // 5. 验证租赁记录已创建
        var rentalPage = rentalRecordService.pageList(1, 10, userId, null);
        assertTrue(rentalPage.getRecords().size() > 0, "应该创建了租赁记录");
        RentalRecord record = rentalPage.getRecords().get(0);
        assertEquals(application.getId(), record.getApplicationId(), "应该关联申请ID");
        assertEquals(stallId, record.getStallId(), "应该关联摊位ID");
        assertEquals(0, record.getPaymentStatus(), "支付状态应该是未支付");
        assertEquals(1, record.getStatus(), "租赁状态应该是租赁中");
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E: 申请流程 - 申请->审核拒绝")
    void testCompleteApplicationFlow_Rejected() {
        // 创建新摊位
        Stall newStall = new Stall();
        newStall.setStallNo("REJECT_" + System.currentTimeMillis());
        newStall.setName("拒绝测试摊位");
        newStall.setRentPrice(new BigDecimal("1000.00"));
        newStall.setStatus(0);
        stallService.save(newStall);
        
        // 1. 提交申请
        StallApplication application = new StallApplication();
        application.setUserId(userId);
        application.setStallId(newStall.getId());
        application.setStartDate(LocalDate.now());
        application.setEndDate(LocalDate.now().plusMonths(1));
        application.setBusinessType("零售");
        application.setReason("测试拒绝流程");
        applicationService.submit(application);
        
        // 2. 审核拒绝
        boolean reviewResult = applicationService.review(
            application.getId(), 2, "材料不完整", adminId);
        assertTrue(reviewResult, "审核应该成功");
        
        // 3. 验证申请状态
        StallApplication rejected = applicationService.getById(application.getId());
        assertEquals(2, rejected.getStatus(), "状态应该是审核拒绝");
        
        // 4. 验证摊位状态未改变
        Stall stall = stallService.getById(newStall.getId());
        assertEquals(0, stall.getStatus(), "摊位状态应该仍然是空闲");
    }
    
    @Test
    @Order(3)
    @DisplayName("E2E: 申请流程 - 用户取消申请")
    void testApplicationFlow_UserCancel() {
        StallApplication application = new StallApplication();
        application.setUserId(userId);
        application.setStallId(stallId);
        application.setStartDate(LocalDate.now().plusDays(1));
        application.setEndDate(LocalDate.now().plusMonths(2));
        application.setReason("测试取消");
        applicationService.submit(application);
        
        // 用户取消
        boolean cancelResult = applicationService.cancel(application.getId(), userId);
        assertTrue(cancelResult, "取消应该成功");
        
        StallApplication cancelled = applicationService.getById(application.getId());
        assertEquals(3, cancelled.getStatus(), "状态应该是已取消");
    }
    
    // ========== 反馈处理流程测试 ==========
    
    @Test
    @Order(4)
    @DisplayName("E2E: 完整反馈处理流程 - 提交->处理中->已处理")
    void testCompleteFeedbackFlow() {
        // 1. 用户提交反馈
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType(1); // 投诉
        feedback.setTitle("摊位环境问题");
        feedback.setContent("存在卫生问题，希望处理");
        feedback.setContactPhone("13800138001");
        boolean submitResult = feedbackService.submit(feedback);
        assertTrue(submitResult, "反馈提交应该成功");
        assertEquals(0, feedback.getStatus(), "初始状态应该是待处理");
        
        // 2. 管理员更新为处理中
        boolean updateResult = feedbackService.updateStatus(feedback.getId(), 1);
        assertTrue(updateResult, "状态更新应该成功");
        
        Feedback processing = feedbackService.getById(feedback.getId());
        assertEquals(1, processing.getStatus(), "状态应该是处理中");
        
        // 3. 管理员回复并处理
        boolean replyResult = feedbackService.reply(
            feedback.getId(), "已安排检查人员处理，感谢您的反馈", adminId);
        assertTrue(replyResult, "回复应该成功");
        
        // 4. 验证最终状态
        Feedback handled = feedbackService.getById(feedback.getId());
        assertEquals(2, handled.getStatus(), "状态应该是已处理");
        assertEquals(adminId, handled.getHandlerId(), "处理人ID应该正确");
        assertNotNull(handled.getHandleTime(), "应该有处理时间");
        assertNotNull(handled.getReply(), "应该有回复内容");
    }
    
    // ========== 租赁终止流程测试 ==========
    
    @Test
    @Order(5)
    @DisplayName("E2E: 租赁终止流程 - 终止租赁->释放摊位")
    void testRentalTerminationFlow() {
        // 创建一个已租用的摊位和租赁记录
        Stall rentedStall = new Stall();
        rentedStall.setStallNo("TERM_" + System.currentTimeMillis());
        rentedStall.setName("终止测试摊位");
        rentedStall.setRentPrice(new BigDecimal("2000.00"));
        rentedStall.setStatus(1); // 已租用
        stallService.save(rentedStall);
        
        RentalRecord record = new RentalRecord();
        record.setApplicationId(999L);
        record.setUserId(userId);
        record.setStallId(rentedStall.getId());
        record.setStartDate(LocalDate.now().minusMonths(1));
        record.setEndDate(LocalDate.now().plusMonths(5));
        record.setRentAmount(new BigDecimal("12000.00"));
        record.setDeposit(new BigDecimal("2000.00"));
        record.setPaymentStatus(1);
        record.setStatus(1); // 租赁中
        rentalRecordService.save(record);
        
        // 终止租赁
        boolean terminateResult = rentalRecordService.terminate(record.getId());
        assertTrue(terminateResult, "终止应该成功");
        
        // 验证租赁状态
        RentalRecord terminated = rentalRecordService.getById(record.getId());
        assertEquals(3, terminated.getStatus(), "状态应该是提前终止");
        
        // 验证摊位状态已释放
        Stall stall = stallService.getById(rentedStall.getId());
        assertEquals(0, stall.getStatus(), "摊位状态应该恢复为空闲");
    }
    
    // ========== 卫生检查流程测试 ==========
    
    @Test
    @Order(6)
    @DisplayName("E2E: 卫生检查流程 - 检查->整改->确认")
    void testHygieneInspectionFlow() {
        // 1. 添加检查记录（不合格需整改）
        HygieneInspection inspection = new HygieneInspection();
        inspection.setStallId(stallId);
        inspection.setInspectorId(adminId);
        inspection.setInspectionDate(LocalDate.now());
        inspection.setScore(55);
        inspection.setProblems("卫生条件差，垃圾未清理");
        inspection.setSuggestions("立即清理，加强日常卫生管理");
        boolean addResult = hygieneInspectionService.addInspection(inspection);
        assertTrue(addResult, "添加检查记录应该成功");
        assertEquals("不合格", inspection.getResult(), "55分应该评为不合格");
        assertEquals(0, inspection.getStatus(), "不合格应该待整改");
        
        // 2. 确认整改完成
        boolean rectifyResult = hygieneInspectionService.updateRectification(
            inspection.getId(), 1);
        assertTrue(rectifyResult, "整改确认应该成功");
        
        HygieneInspection rectified = hygieneInspectionService.getById(inspection.getId());
        assertEquals(1, rectified.getStatus(), "状态应该是已整改");
    }
    
    // ========== 公告发布流程测试 ==========
    
    @Test
    @Order(7)
    @DisplayName("E2E: 公告发布流程 - 发布->下架->重新发布")
    void testAnnouncementPublishFlow() {
        // 1. 发布公告
        Announcement announcement = new Announcement();
        announcement.setTitle("重要通知：摊位管理规定更新");
        announcement.setContent("根据最新政策，摊位管理规定有以下更新...");
        announcement.setType(2); // 政策通知
        announcement.setPriority(1); // 重要
        announcement.setPublisherId(adminId);
        boolean publishResult = announcementService.publish(announcement);
        assertTrue(publishResult, "发布应该成功");
        assertEquals(1, announcement.getStatus(), "状态应该是已发布");
        assertNotNull(announcement.getPublishTime(), "应该有发布时间");
        
        // 2. 验证在公开列表中可见
        var publishedList = announcementService.listPublished(null);
        assertTrue(publishedList.stream().anyMatch(a -> a.getId().equals(announcement.getId())),
            "公告应该在已发布列表中");
        
        // 3. 下架公告
        boolean unpublishResult = announcementService.updateStatus(announcement.getId(), 2);
        assertTrue(unpublishResult, "下架应该成功");
        
        // 4. 验证不在公开列表中
        var publishedListAfter = announcementService.listPublished(null);
        assertTrue(publishedListAfter.stream().noneMatch(a -> a.getId().equals(announcement.getId())),
            "下架后不应该在已发布列表中");
        
        // 5. 重新发布
        boolean republishResult = announcementService.updateStatus(announcement.getId(), 1);
        assertTrue(republishResult, "重新发布应该成功");
    }
    
    // ========== 数据一致性测试 ==========
    
    @Test
    @Order(8)
    @DisplayName("数据一致性: 用户信息关联正确性")
    void testDataConsistency_UserRelations() {
        // 创建用户的各种数据
        StallApplication app = new StallApplication();
        app.setUserId(userId);
        app.setStallId(stallId);
        app.setStartDate(LocalDate.now());
        app.setEndDate(LocalDate.now().plusMonths(1));
        applicationService.submit(app);
        
        Feedback fb = new Feedback();
        fb.setUserId(userId);
        fb.setType(2);
        fb.setTitle("数据一致性测试");
        fb.setContent("测试内容");
        feedbackService.submit(fb);
        
        // 验证关联数据正确
        StallApplication appDetail = applicationService.getDetailById(app.getId());
        assertNotNull(appDetail.getUsername(), "申请应该包含用户名");
        
        Feedback fbDetail = feedbackService.getDetailById(fb.getId());
        assertNotNull(fbDetail.getUsername(), "反馈应该包含用户名");
    }
    
    @Test
    @Order(9)
    @DisplayName("数据一致性: 摊位信息关联正确性")
    void testDataConsistency_StallRelations() {
        Stall stallDetail = stallService.getDetailById(stallId);
        assertNotNull(stallDetail.getTypeName(), "摊位详情应该包含类型名称");
    }
}
