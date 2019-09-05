package com.qipeng.qrpc.common.serializer;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class RpcPacketSplitter extends LengthFieldBasedFrameDecoder {

    private static final int LENGTH_FIELD_OFFSET = 6;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public RpcPacketSplitter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }
}
