package com.qipeng.qprc.server.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcService {
    Class<?> serviceInterface();
}
