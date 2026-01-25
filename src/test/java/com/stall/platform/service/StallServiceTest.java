package com.stall.platform.service;

import com.stall.platform.entity.Stall;
import com.stall.platform.entity.StallType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 摊位服务单元测试
 * 测试模块：模块2-摊位管理
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StallServiceTest {
    
    @Autowired
    private StallService stallService;
    
    @Autowired
    private StallTypeService stallTypeService;
    
    private Long testTypeId;
    
    @BeforeEach
    void setUp() {
        // 创建测试摊位类型
        StallType type = new StallType();
        type.setName("测试类型_" + System.currentTimeMillis());
        type.setDescription("测试用摊位类型");
        type.setSortOrder(99);
        stallTypeService.save(type);
        testTypeId = type.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("添加摊位-正常流程")
    void testAddStall() {
        Stall stall = createTestStall("TEST001");
        boolean result = stallService.save(stall);
        
        assertTrue(result, "摊位添加应该成功");
        assertNotNull(stall.getId(), "应该生成ID");
        
        Stall saved = stallService.getById(stall.getId());
        assertEquals("TEST001", saved.getStallNo());
        assertEquals(0, saved.getStatus(), "默认状态应该是空闲(0)");
    }
    
    @Test
    @Order(2)
    @DisplayName("获取摊位详情-带类型名称")
    void testGetDetailById() {
        Stall stall = createTestStall("TEST002");
        stallService.save(stall);
        
        Stall detail = stallService.getDetailById(stall.getId());
        assertNotNull(detail, "应该能获取到摊位详情");
        assertNotNull(detail.getTypeName(), "应该包含类型名称");
    }
    
    @Test
    @Order(3)
    @DisplayName("分页查询摊位列表")
    void testPageList() {
        // 创建多个测试摊位
        for (int i = 0; i < 5; i++) {
            Stall stall = createTestStall("PAGE" + i + "_" + System.currentTimeMillis());
            stall.setName("分页测试摊位" + i);
            stallService.save(stall);
        }
        
        var page = stallService.pageList(1, 10, testTypeId, null, "分页测试");
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 5, "应该至少有5条记录");
    }
    
    @Test
    @Order(4)
    @DisplayName("按状态筛选摊位")
    void testPageListByStatus() {
        Stall stall1 = createTestStall("STATUS1_" + System.currentTimeMillis());
        stall1.setStatus(0); // 空闲
        stallService.save(stall1);
        
        Stall stall2 = createTestStall("STATUS2_" + System.currentTimeMillis());
        stall2.setStatus(1); // 已租用
        stallService.save(stall2);
        
        var availablePage = stallService.pageList(1, 10, null, 0, null);
        assertTrue(availablePage.getRecords().stream().allMatch(s -> s.getStatus() == 0), 
                "所有结果状态应该是空闲");
    }
    
    @Test
    @Order(5)
    @DisplayName("获取可用摊位列表")
    void testListAvailable() {
        Stall stall = createTestStall("AVAIL_" + System.currentTimeMillis());
        stall.setStatus(0);
        stallService.save(stall);
        
        var list = stallService.listAvailable();
        assertNotNull(list, "列表不应为null");
        assertTrue(list.stream().allMatch(s -> s.getStatus() == 0), "所有结果应该是可用状态");
    }
    
    @Test
    @Order(6)
    @DisplayName("更新摊位状态")
    void testUpdateStatus() {
        Stall stall = createTestStall("UPSTAT_" + System.currentTimeMillis());
        stall.setStatus(0);
        stallService.save(stall);
        
        boolean result = stallService.updateStatus(stall.getId(), 1);
        assertTrue(result, "状态更新应该成功");
        
        Stall updated = stallService.getById(stall.getId());
        assertEquals(1, updated.getStatus(), "状态应该更新为已租用(1)");
    }
    
    @Test
    @Order(7)
    @DisplayName("更新摊位信息")
    void testUpdateStall() {
        Stall stall = createTestStall("UPDATE_" + System.currentTimeMillis());
        stallService.save(stall);
        
        stall.setName("更新后的名称");
        stall.setRentPrice(new BigDecimal("2000.00"));
        boolean result = stallService.updateById(stall);
        
        assertTrue(result, "更新应该成功");
        Stall updated = stallService.getById(stall.getId());
        assertEquals("更新后的名称", updated.getName());
        assertEquals(0, new BigDecimal("2000.00").compareTo(updated.getRentPrice()));
    }
    
    @Test
    @Order(8)
    @DisplayName("删除摊位")
    void testDeleteStall() {
        Stall stall = createTestStall("DELETE_" + System.currentTimeMillis());
        stallService.save(stall);
        
        boolean result = stallService.removeById(stall.getId());
        assertTrue(result, "删除应该成功");
        
        Stall deleted = stallService.getById(stall.getId());
        assertNull(deleted, "删除后不应能查到(逻辑删除)");
    }
    
    private Stall createTestStall(String stallNo) {
        Stall stall = new Stall();
        stall.setStallNo(stallNo);
        stall.setName("测试摊位-" + stallNo);
        stall.setTypeId(testTypeId);
        stall.setLocation("测试位置");
        stall.setArea(new BigDecimal("10.5"));
        stall.setRentPrice(new BigDecimal("1200.00"));
        stall.setStatus(0);
        stall.setDescription("测试摊位描述");
        return stall;
    }
}
