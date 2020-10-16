package com.qipeng.qrpc.test.client.web;

import com.qipeng.qrpc.client.proxy.RpcProxyFactory;
import com.qipeng.qrpc.test.api.TestRequest;
import com.qipeng.qrpc.test.api.TestResponse;
import com.qipeng.qrpc.test.api.TestService;
import com.qipeng.qrpc.test.client.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author qipenglin
 * @date 2019-09-10 18:08
 **/
@Slf4j
@RestController
public class TestController {

    @Resource
    private HelloService helloService;

    public ExecutorService executorService = Executors.newFixedThreadPool(20);

    @RequestMapping("/test/")
    public void test() {
        helloService.sayHello();
    }

    @RequestMapping("/test/concurrent")
    public void testConcurrent() {
        for (int i = 0; i < 20; i++) {
            executorService.execute(() -> helloService.sayHello());
        }
    }

    @RequestMapping("/test/proxy")
    public void testProxy() {
        TestService testService = RpcProxyFactory.createProxy(TestService.class);
        TestRequest request = new TestRequest();
        int count = ThreadLocalRandom.current().nextInt(1, 20);
        String name = RandomStringUtils.randomAlphabetic(count);
        request.setName(name);
        TestResponse response = testService.test(request);
        log.info("request:{},response:{}", request, response);
    }

}
