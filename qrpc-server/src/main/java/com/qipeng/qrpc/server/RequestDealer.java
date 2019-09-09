package com.qipeng.qrpc.server;/**
 * @Author qipenglin
 * @Date 2019-09-06 15:28
 **/

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import io.netty.channel.Channel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author qipenglin
 * @Date 2019-09-06 15:28
 **/
public class RequestDealer {

    private static ThreadPoolExecutor executor;

    static  {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("requestDealer-{}").build();
        executor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000), threadFactory);
    }


    static void dealRequest(Channel channel, RpcRequest request, ServiceProvider provider) {
        executor.submit(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            try {
                Object instance = provider.getInstance();
                String methodName = request.getMethodName();
                Class<?>[] paramTypes = request.getParamTypes();
                Method method = instance.getClass().getMethod(methodName, paramTypes);
                Object[] params = request.getParameters();
                Object result = method.invoke(instance, params);
                response.setResult(result);
                response.setHasException(false);
            } catch (Exception e) {
                response.setHasException(true);
                response.setResult(e);
            }
            channel.writeAndFlush(response);
        });
    }
}
