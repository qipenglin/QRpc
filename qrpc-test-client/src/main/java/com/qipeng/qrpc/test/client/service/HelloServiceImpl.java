package com.qipeng.qrpc.test.client.service;

import com.qipeng.qrpc.client.annotation.RpcReference;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class HelloServiceImpl implements HelloService {

    @RpcReference
    private TestService testService;

    @Override
    public TestResponse sayHello() {
        TestRequest request = new TestRequest();
        int count = ThreadLocalRandom.current().nextInt(1, 20);
        String name = RandomStringUtils.randomAlphabetic(count);
        request.setName(name);
        TestResponse response = testService.test(request);
        log.info("request:{},response:{}", request, response);
        return response;
    }
}
