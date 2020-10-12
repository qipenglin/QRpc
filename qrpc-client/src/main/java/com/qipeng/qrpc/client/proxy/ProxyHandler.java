package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/10/12 11:48 上午
 */
public interface ProxyHandler {
    Object invoke(InvocationContext context);
}
