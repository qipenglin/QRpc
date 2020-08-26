package com.qipeng.qrpc.common.exception;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/26 9:17 上午
 */
public class PacketFormatException extends RuntimeException {
    public PacketFormatException() {
    }

    public PacketFormatException(String message) {
        super(message);
    }

    public PacketFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketFormatException(Throwable cause) {
        super(cause);
    }
}
