package com.qipeng.qrpc.test.server.config;/**
 * @Author qipenglin
 * @Date 2019-09-06 10:49
 **/

import com.qipeng.qprc.server.spring.RpcServiceAnnotationPostProcessor;
import com.qipeng.qprc.server.spring.ServerStarter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author qipenglin
 * @Date 2019-09-06 10:49
 **/
@Configuration
public class BeanConfig {

    @Bean
    public RpcServiceAnnotationPostProcessor build() {
        return new RpcServiceAnnotationPostProcessor();
    }

    @Bean
    public ServerStarter buildServerStarter() {
        return new ServerStarter();
    }
}
