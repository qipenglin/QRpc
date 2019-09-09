package com.qipeng.qrpc.test.client.service;

import com.qipeng.qrpc.client.annotation.RpcReference;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import org.springframework.stereotype.Component;

@Component
public class HelloServiceImpl implements HelloService {

    @RpcReference
    private TestService testService;

    @Override
    public void sayHello() {
        TestRequest request = new TestRequest();
        request.setName("QiPeng");
        TestResponse response = testService.test(request);
        System.out.println(response.getDate());
    }
}
