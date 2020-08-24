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



    static  {

    }


    static void dealRequest(Channel channel, RpcRequest request, ServiceProvider provider) {

    }
}
