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
 * 卫生检查服务单元测试
 * 测试模块：模块4-卫生管理
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HygieneInspectionServiceTest {
    
    @Autowired
    private HygieneInspectionService hygieneInspectionService;
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private UserService userService;
    
    private Long testStallId;
    private Long testInspectorId;
    
    @BeforeEach
    void setUp() {
        // 创建测试摊位
        Stall stall = new Stall();
        stall.setStallNo("HYGIENE_" + System.currentTimeMillis());
        stall.setName("卫生检查测试摊位");
        stall.setRentPrice(new BigDecimal("1000.00"));
        stall.setStatus(1);
        stallService.save(stall);
        testStallId = stall.getId();
        
        // 创建检查员
        User inspector = new User();
        inspector.setUsername("inspector_" + System.currentTimeMillis());
        inspector.setPassword("test123");
        inspector.setRole("ADMIN");
        inspector.setStatus(1);
        inspector.setNickname("卫生检查员");
        userService.save(inspector);
        testInspectorId = inspector.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("添加卫生检查-优秀")
    void testAddInspection_Excellent() {
        HygieneInspection inspection = createTestInspection(95);
        
        boolean result = hygieneInspectionService.addInspection(inspection);
        assertTrue(result, "添加检查记录应该成功");
        assertEquals("优秀", inspection.getResult(), "90分以上应评为优秀");
        assertEquals(2, inspection.getStatus(), "优秀应无需整改(2)");
    }
    
    @Test
    @Order(2)
    @DisplayName("添加卫生检查-良好")
    void testAddInspection_Good() {
        HygieneInspection inspection = createTestInspection(85);
        
        boolean result = hygieneInspectionService.addInspection(inspection);
        assertTrue(result, "添加检查记录应该成功");
        assertEquals("良好", inspection.getResult(), "80-89分应评为良好");
        assertEquals(2, inspection.getStatus(), "良好应无需整改(2)");
    }
    
    @Test
    @Order(3)
    @DisplayName("添加卫生检查-合格")
    void testAddInspection_Qualified() {
        HygieneInspection inspection = createTestInspection(70);
        
        boolean result = hygieneInspectionService.addInspection(inspection);
        assertTrue(result, "添加检查记录应该成功");
        assertEquals("合格", inspection.getResult(), "60-79分应评为合格");
    }
    
    @Test
    @Order(4)
    @DisplayName("添加卫生检查-不合格")
    void testAddInspection_Unqualified() {
        HygieneInspection inspection = createTestInspection(50);
        inspection.setProblems("卫生条件差，存在安全隐患");
        
        boolean result = hygieneInspectionService.addInspection(inspection);
        assertTrue(result, "添加检查记录应该成功");
        assertEquals("不合格", inspection.getResult(), "60分以下应评为不合格");
        assertEquals(0, inspection.getStatus(), "不合格应待整改(0)");
    }
    
    @Test
    @Order(5)
    @DisplayName("分页查询检查记录")
    void testPageList() {
        for (int i = 0; i < 3; i++) {
            HygieneInspection inspection = createTestInspection(80 + i * 5);
            hygieneInspectionService.addInspection(inspection);
        }
        
        var page = hygieneInspectionService.pageList(1, 10, testStallId, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 3);
    }
    
    @Test
    @Order(6)
    @DisplayName("按评级筛选检查记录")
    void testPageListByResult() {
        for (int i = 0; i < 3; i++) {
            HygieneInspection inspection = createTestInspection(95);
            hygieneInspectionService.addInspection(inspection);
        }
        
        var page = hygieneInspectionService.pageList(1, 10, null, "优秀");
        assertTrue(page.getRecords().stream().allMatch(i -> "优秀".equals(i.getResult())), 
                "所有结果应该是优秀");
    }
    
    @Test
    @Order(7)
    @DisplayName("更新整改状态")
    void testUpdateRectification() {
        HygieneInspection inspection = createTestInspection(50);
        inspection.setProblems("需要整改");
        hygieneInspectionService.addInspection(inspection);
        
        boolean result = hygieneInspectionService.updateRectification(inspection.getId(), 1); // 已整改
        assertTrue(result, "状态更新应该成功");
        
        HygieneInspection updated = hygieneInspectionService.getById(inspection.getId());
        assertEquals(1, updated.getStatus(), "状态应该是已整改(1)");
    }
    
    @Test
    @Order(8)
    @DisplayName("获取检查详情")
    void testGetDetailById() {
        HygieneInspection inspection = createTestInspection(85);
        hygieneInspectionService.addInspection(inspection);
        
        HygieneInspection detail = hygieneInspectionService.getDetailById(inspection.getId());
        assertNotNull(detail, "应该能获取到详情");
        assertNotNull(detail.getStallNo(), "应该包含摊位编号");
        assertNotNull(detail.getInspectorName(), "应该包含检查员名称");
    }
    
    private HygieneInspection createTestInspection(int score) {
        HygieneInspection inspection = new HygieneInspection();
        inspection.setStallId(testStallId);
        inspection.setInspectorId(testInspectorId);
        inspection.setInspectionDate(LocalDate.now());
        inspection.setScore(score);
        inspection.setSuggestions("测试建议");
        return inspection;
    }
}
