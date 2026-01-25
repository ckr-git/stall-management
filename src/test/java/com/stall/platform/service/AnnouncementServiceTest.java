package com.stall.platform.service;

import com.stall.platform.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 公告服务单元测试
 * 测试模块：模块6-公告管理
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnouncementServiceTest {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
    private Long testAdminId;
    
    @BeforeEach
    void setUp() {
        User admin = new User();
        admin.setUsername("ann_admin_" + System.currentTimeMillis());
        admin.setPassword("admin123");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setNickname("公告管理员");
        userService.save(admin);
        testAdminId = admin.getId();
    }
    
    @Test
    @Order(1)
    @DisplayName("发布公告-系统公告")
    void testPublishAnnouncement_System() {
        Announcement announcement = createTestAnnouncement(1, 0); // 系统公告，普通优先级
        
        boolean result = announcementService.publish(announcement);
        assertTrue(result, "发布应该成功");
        assertEquals(1, announcement.getStatus(), "状态应该是已发布(1)");
        assertNotNull(announcement.getPublishTime(), "应该有发布时间");
    }
    
    @Test
    @Order(2)
    @DisplayName("发布公告-政策通知")
    void testPublishAnnouncement_Policy() {
        Announcement announcement = createTestAnnouncement(2, 1); // 政策通知，重要
        
        boolean result = announcementService.publish(announcement);
        assertTrue(result, "发布应该成功");
    }
    
    @Test
    @Order(3)
    @DisplayName("发布公告-活动通知")
    void testPublishAnnouncement_Activity() {
        Announcement announcement = createTestAnnouncement(3, 2); // 活动通知，紧急
        
        boolean result = announcementService.publish(announcement);
        assertTrue(result, "发布应该成功");
    }
    
    @Test
    @Order(4)
    @DisplayName("获取已发布公告列表")
    void testListPublished() {
        // 发布几个公告
        for (int i = 1; i <= 3; i++) {
            Announcement ann = createTestAnnouncement(i, i - 1);
            announcementService.publish(ann);
        }
        
        var list = announcementService.listPublished(null);
        assertNotNull(list, "列表不应为null");
        assertTrue(list.stream().allMatch(a -> a.getStatus() == 1), "所有公告状态应该是已发布");
    }
    
    @Test
    @Order(5)
    @DisplayName("按类型筛选已发布公告")
    void testListPublishedByType() {
        for (int i = 0; i < 3; i++) {
            Announcement ann = createTestAnnouncement(1, 0);
            announcementService.publish(ann);
        }
        
        var list = announcementService.listPublished(1); // 只查系统公告
        assertTrue(list.stream().allMatch(a -> a.getType() == 1), "所有公告类型应该是系统公告");
    }
    
    @Test
    @Order(6)
    @DisplayName("管理员分页查询公告")
    void testPageList() {
        for (int i = 1; i <= 3; i++) {
            Announcement ann = createTestAnnouncement(i, 0);
            announcementService.publish(ann);
        }
        
        var page = announcementService.pageList(1, 10, null, null);
        assertNotNull(page, "分页结果不应为null");
        assertTrue(page.getRecords().size() >= 3);
    }
    
    @Test
    @Order(7)
    @DisplayName("更新公告状态-下架")
    void testUpdateStatus_Unpublish() {
        Announcement announcement = createTestAnnouncement(1, 0);
        announcementService.publish(announcement);
        
        boolean result = announcementService.updateStatus(announcement.getId(), 2); // 下架
        assertTrue(result, "状态更新应该成功");
        
        Announcement updated = announcementService.getById(announcement.getId());
        assertEquals(2, updated.getStatus(), "状态应该是已下架(2)");
    }
    
    @Test
    @Order(8)
    @DisplayName("获取公告详情")
    void testGetDetailById() {
        Announcement announcement = createTestAnnouncement(1, 0);
        announcementService.publish(announcement);
        
        Announcement detail = announcementService.getDetailById(announcement.getId());
        assertNotNull(detail, "应该能获取到详情");
        assertNotNull(detail.getPublisherName(), "应该包含发布人名称");
    }
    
    @Test
    @Order(9)
    @DisplayName("公告按优先级排序")
    void testPublishedOrderByPriority() {
        // 创建不同优先级的公告
        Announcement ann1 = createTestAnnouncement(1, 0); // 普通
        Announcement ann2 = createTestAnnouncement(1, 2); // 紧急
        Announcement ann3 = createTestAnnouncement(1, 1); // 重要
        
        announcementService.publish(ann1);
        announcementService.publish(ann2);
        announcementService.publish(ann3);
        
        var list = announcementService.listPublished(1);
        if (list.size() >= 3) {
            // 验证排序：紧急 > 重要 > 普通
            assertTrue(list.get(0).getPriority() >= list.get(1).getPriority(), 
                    "应该按优先级降序排列");
        }
    }
    
    private Announcement createTestAnnouncement(int type, int priority) {
        Announcement announcement = new Announcement();
        announcement.setTitle("测试公告_" + System.currentTimeMillis());
        announcement.setContent("这是测试公告内容");
        announcement.setType(type);
        announcement.setPriority(priority);
        announcement.setPublisherId(testAdminId);
        return announcement;
    }
}
