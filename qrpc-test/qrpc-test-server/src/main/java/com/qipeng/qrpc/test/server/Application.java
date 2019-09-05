package com.qipeng.qrpc.test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author qipenglin
 * @Date 2019-09-05 17:06
 **/
@SpringBootApplication(scanBasePackages="com.qipeng.qrpc")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
