package com.qipeng.qrpc.test.server;

import com.qipeng.qrpc.server.spring.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author qipenglin
 * @date 2019-09-05 17:06
 **/
@EnableRpcServer
@SpringBootApplication
public class RpcServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }
}
