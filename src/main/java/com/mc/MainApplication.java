package com.mc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 游戏的主Application
 * @author 江辉彬
 * @version 1.0
 */
@SpringBootApplication
//@MapperScan(basePackages = "com.mc.**.mapper")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
