package com.qipeng.qrpc.test.server.service;

import com.qipeng.qrpc.server.annotation.RpcService;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author qipenglin
 * @date 2019-09-05 17:35
 **/
@Component
@RpcService(serviceInterface = TestService.class)
public class TestServiceImpl implements TestService {

    @Override
    public TestResponse test(TestRequest testRequest) {
        TestResponse response = new TestResponse();
        response.setName(testRequest.getName());
        response.setDate(new Date());
        return response;
    }
}
