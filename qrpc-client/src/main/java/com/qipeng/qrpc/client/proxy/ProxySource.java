package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.registry.RegistryConfig;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/10/12 11:49 上午
 */
public interface ProxySource {
    Object createProxy(Class<?> clazz, RegistryConfig registryConfig);
}
