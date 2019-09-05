package com.qipeng.qrpc.test.server.service;/**
 * @Author qipenglin
 * @Date 2019-09-05 17:35
 **/

import com.qipeng.qprc.server.annotation.RpcService;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author qipenglin
 * @Date 2019-09-05 17:35
 **/
@Component
@RpcService(serviceInterface = TestService.class)
public class TestServiceImpl implements TestService, InitializingBean {

    @Override
    public TestResponse test(TestRequest testRequest) {
        TestResponse response = new TestResponse();
        response.setName(testRequest.getName());
        response.setDate(new Date());
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("");
    }
}
