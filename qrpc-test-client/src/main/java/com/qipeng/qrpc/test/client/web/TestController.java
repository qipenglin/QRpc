package com.qipeng.qrpc.test.client.web;

import com.qipeng.qrpc.test.client.service.HelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qipenglin
 * @date 2019-09-10 18:08
 **/
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

}
