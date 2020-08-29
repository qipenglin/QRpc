package com.qipeng.qrpc.test.client;

import com.qipeng.qrpc.client.proxy.ProxyFactory;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.qipeng.qrpc")
public class RpcClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        TestService testService = (TestService) ProxyFactory.getProxy(TestService.class);
//        TestRequest request = new TestRequest();
//        request.setName("Test");
//        TestResponse test = testService.test(request);
    }
}
