package com.qipeng.qrpc.client.spring;

import com.qipeng.qrpc.common.config.RpcConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RpcConfig.class, RpcReferenceAnnotationPostProcessor.class, RpcReferenceAnnotationProcessor.class})
public @interface EnableRpcClient {
}
