package com.qipeng.qrpc.test.client;

import com.qipeng.qrpc.test.client.service.HelloService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication(scanBasePackages = "com.qipeng.qrpc")
public class RpcClientApplication implements CommandLineRunner {

    @Resource
    private HelloService helloService;

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        helloService.sayHello();
    }
}
