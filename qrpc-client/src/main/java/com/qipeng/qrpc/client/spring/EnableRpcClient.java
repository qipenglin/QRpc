package com.qipeng.qrpc.client.spring;

import com.qipeng.qrpc.common.config.RpcConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RpcConfig.class, RpcReferenceAnnotationPostProcessor.class})
public @interface EnableRpcClient {
}
