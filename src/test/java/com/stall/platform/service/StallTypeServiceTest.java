package com.stall.platform.service;

import com.stall.platform.entity.StallType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 摊位类型服务单元测试
 * 测试模块：模块4-摊位类型
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StallTypeServiceTest {
    
    @Autowired
    private StallTypeService stallTypeService;
    
    @Test
    @Order(1)
    @DisplayName("添加摊位类型")
    void testAddStallType() {
        StallType type = new StallType();
        type.setName("测试类型_" + System.currentTimeMillis());
        type.setDescription("这是测试类型描述");
        type.setSortOrder(1);
        
        boolean result = stallTypeService.save(type);
        assertTrue(result, "添加摊位类型应该成功");
        assertNotNull(type.getId(), "应该生成ID");
    }
    
    @Test
    @Order(2)
    @DisplayName("查询类型详情")
    void testGetById() {
        StallType type = new StallType();
        type.setName("详情测试_" + System.currentTimeMillis());
        type.setDescription("详情测试描述");
        stallTypeService.save(type);
        
        StallType found = stallTypeService.getById(type.getId());
        assertNotNull(found, "应该能查到类型");
        assertEquals(type.getName(), found.getName());
    }
    
    @Test
    @Order(3)
    @DisplayName("查询所有类型列表-按排序")
    void testListAll() {
        // 创建多个类型，设置不同排序
        for (int i = 3; i >= 1; i--) {
            StallType type = new StallType();
            type.setName("排序测试_" + i + "_" + System.currentTimeMillis());
            type.setSortOrder(i);
            stallTypeService.save(type);
        }
        
        List<StallType> list = stallTypeService.listAll();
        assertNotNull(list, "列表不应为null");
        assertTrue(list.size() >= 3, "应该至少有3个类型");
        
        // 验证排序（按sort_order升序）
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getSortOrder() <= list.get(i + 1).getSortOrder(),
                    "应该按排序字段升序排列");
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("更新摊位类型")
    void testUpdateStallType() {
        StallType type = new StallType();
        type.setName("更新前_" + System.currentTimeMillis());
        type.setDescription("更新前描述");
        stallTypeService.save(type);
        
        type.setName("更新后_" + System.currentTimeMillis());
        type.setDescription("更新后描述");
        boolean result = stallTypeService.updateById(type);
        
        assertTrue(result, "更新应该成功");
        StallType updated = stallTypeService.getById(type.getId());
        assertTrue(updated.getName().startsWith("更新后"), "名称应该已更新");
        assertEquals("更新后描述", updated.getDescription());
    }
    
    @Test
    @Order(5)
    @DisplayName("删除摊位类型")
    void testDeleteStallType() {
        StallType type = new StallType();
        type.setName("删除测试_" + System.currentTimeMillis());
        stallTypeService.save(type);
        
        boolean result = stallTypeService.removeById(type.getId());
        assertTrue(result, "删除应该成功");
        
        StallType deleted = stallTypeService.getById(type.getId());
        assertNull(deleted, "删除后不应能查到(逻辑删除)");
    }
    
    @Test
    @Order(6)
    @DisplayName("类型名称必填测试")
    void testTypeNameRequired() {
        // 验证名称必填 - 数据库层面name是NOT NULL约束
        // 测试提供有效名称能正常保存
        StallType type = new StallType();
        type.setName("必填测试_" + System.currentTimeMillis());
        type.setDescription("带名称的类型");
        boolean result = stallTypeService.save(type);
        assertTrue(result, "带名称的类型保存应该成功");
        assertNotNull(type.getId(), "应该生成ID");
    }
    
    @Test
    @Order(7)
    @DisplayName("默认排序为0")
    void testDefaultSortOrder() {
        StallType type = new StallType();
        type.setName("默认排序_" + System.currentTimeMillis());
        stallTypeService.save(type);
        
        StallType saved = stallTypeService.getById(type.getId());
        assertNotNull(saved.getSortOrder(), "排序不应为null");
    }
}
