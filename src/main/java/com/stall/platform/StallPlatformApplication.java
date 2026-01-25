package com.stall.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stall.platform.mapper")
public class StallPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(StallPlatformApplication.class, args);
        System.out.println("摊位管理平台启动成功！");
    }
}
