package com.qipeng.qrpc.common.exception;/**
 * @author qipenglin
 * @date 2019-09-06 16:23
 **/

/**
 * @author qipenglin
 * @date 2019-09-06 16:23
 **/
public class RpcException extends RuntimeException {

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
