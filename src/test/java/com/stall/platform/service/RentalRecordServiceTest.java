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
 * 租赁记录服务单元测试
 * 测试模块：模块9-租赁记录
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RentalRecordServiceTest {
    
    @Autowired
    private RentalRecordService rentalRecordService;
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StallApplicationService applicationService;
    
    private Long testUserId;
    private Long testStallId;
    private Long testRecordId;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("rental_user_" + System.currentTimeMillis());
        user.setPassword("test123");
        userService.register(user);
        testUserId = userService.findByUsername(user.getUsername()).getId();
        
        // 创建测试摊位
        Stall stall = new Stall();
        stall.setStallNo("RENTAL_" + System.currentTimeMillis());
        stall.setName("租赁测试摊位");
        stall.setRentPrice(new BigDecimal("1000.00"));
        stall.setStatus(1); // 已租用
        stallService.save(stall);
        testStallId = stall.getId();
        
        // 创建测试租赁记录
        RentalRecord record = new RentalRecord();
        record.setApplicationId(1L);
        record.setUserId(testUserId);
        record.setStallId(testStallId);
        record.setStartDate(LocalDate.now());
        record.setEndDate(LocalDate.now().plusMonths(3));
        record.setRentAmount(new BigDecimal("3000.00"));
        record.setDeposit(new BigDecimal("1000.00"));
        record.setPaymentStatus(0); // 未支付
        record.setStatus(1); // 租赁中
        rentalRecordService.save(record);
        testRecordId = record.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("创建租赁记录")
    void testCreateRentalRecord() {
        Stall newStall = new Stall();
        newStall.setStallNo("NEW_RENTAL_" + System.currentTimeMillis());
        newStall.setName("新租赁摊位");
        newStall.setRentPrice(new BigDecimal("2000.00"));
        newStall.setStatus(1);
        stallService.save(newStall);
        
        RentalRecord record = new RentalRecord();
        record.setApplicationId(2L);
        record.setUserId(testUserId);
        record.setStallId(newStall.getId());
        record.setStartDate(LocalDate.now());
        record.setEndDate(LocalDate.now().plusMonths(6));
        record.setRentAmount(new BigDecimal("12000.00"));
        record.setDeposit(new BigDecimal("2000.00"));
        record.setPaymentStatus(0);
        record.setStatus(1);
        
        boolean result = rentalRecordService.save(record);
        assertTrue(result, "创建租赁记录应该成功");
        assertNotNull(record.getId(), "应该生成ID");
    }
    
    @Test
    @Order(2)
    @DisplayName("查询我的租赁列表")
    void testPageList() {
        var page = rentalRecordService.pageList(1, 10, testUserId, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 1, "应该至少有1条记录");
        assertTrue(page.getRecords().stream().allMatch(r -> r.getUserId().equals(testUserId)),
                "所有记录应该属于测试用户");
    }
    
    @Test
    @Order(3)
    @DisplayName("管理员查询租赁列表-按摊位筛选")
    void testPageListForAdmin_ByStall() {
        var page = rentalRecordService.pageListForAdmin(1, 10, testStallId, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().stream().allMatch(r -> r.getStallId().equals(testStallId)),
                "所有记录应该属于指定摊位");
    }
    
    @Test
    @Order(4)
    @DisplayName("管理员查询租赁列表-按状态筛选")
    void testPageListForAdmin_ByStatus() {
        var page = rentalRecordService.pageListForAdmin(1, 10, null, 1);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().stream().allMatch(r -> r.getStatus() == 1),
                "所有记录状态应该是租赁中");
    }
    
    @Test
    @Order(5)
    @DisplayName("更新支付状态")
    void testUpdatePaymentStatus() {
        boolean result = rentalRecordService.updatePaymentStatus(testRecordId, 1);
        assertTrue(result, "更新支付状态应该成功");
        
        RentalRecord updated = rentalRecordService.getById(testRecordId);
        assertEquals(1, updated.getPaymentStatus(), "支付状态应该是已支付(1)");
    }
    
    @Test
    @Order(6)
    @DisplayName("终止租赁")
    void testTerminate() {
        boolean result = rentalRecordService.terminate(testRecordId);
        assertTrue(result, "终止租赁应该成功");
        
        RentalRecord terminated = rentalRecordService.getById(testRecordId);
        assertEquals(3, terminated.getStatus(), "状态应该是提前终止(3)");
        
        // 验证摊位状态已释放
        Stall stall = stallService.getById(testStallId);
        assertEquals(0, stall.getStatus(), "摊位状态应该是空闲(0)");
    }
    
    @Test
    @Order(7)
    @DisplayName("终止已终止的租赁-应该失败")
    void testTerminate_AlreadyTerminated() {
        // 先终止一次
        rentalRecordService.terminate(testRecordId);
        
        // 再次终止应该失败
        boolean result = rentalRecordService.terminate(testRecordId);
        assertFalse(result, "不能终止已终止的租赁");
    }
    
    @Test
    @Order(8)
    @DisplayName("获取租赁详情")
    void testGetDetailById() {
        RentalRecord detail = rentalRecordService.getDetailById(testRecordId);
        assertNotNull(detail, "应该能获取到详情");
        assertNotNull(detail.getUsername(), "应该包含用户名");
        assertNotNull(detail.getStallNo(), "应该包含摊位编号");
    }
    
    @Test
    @Order(9)
    @DisplayName("租赁记录关联数据完整性")
    void testDataIntegrity() {
        RentalRecord record = rentalRecordService.getDetailById(testRecordId);
        
        assertNotNull(record.getUserId(), "用户ID不应为空");
        assertNotNull(record.getStallId(), "摊位ID不应为空");
        assertNotNull(record.getStartDate(), "开始日期不应为空");
        assertNotNull(record.getEndDate(), "结束日期不应为空");
        assertTrue(record.getEndDate().isAfter(record.getStartDate()), 
                "结束日期应该在开始日期之后");
    }
}
