package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.User;

public interface UserService extends IService<User> {
    
    User findByUsername(String username);
    
    boolean register(User user);
    
    IPage<User> pageList(Integer pageNum, Integer pageSize, String keyword, String role);
    
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    boolean resetPassword(Long userId);
}
