package com.qipeng.qrpc.test.client;

import com.qipeng.qrpc.test.client.service.HelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author qipenglin
 * @date 2019-09-10 18:08
 **/
@RestController
public class TestController {
    @Resource
    private HelloService helloService;

    @RequestMapping("/test")
    public void test() {
        for (int i = 0; i < 10; i++) {
            helloService.sayHello();
        }
    }

}
