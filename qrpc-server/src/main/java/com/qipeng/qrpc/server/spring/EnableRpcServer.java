package com.qipeng.qrpc.server.spring;

import com.qipeng.qrpc.common.config.RpcConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/9/27 9:39 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RpcServiceAnnotationPostProcessor.class, RpcServerStarter.class, RpcConfig.class})
public @interface EnableRpcServer {
}
