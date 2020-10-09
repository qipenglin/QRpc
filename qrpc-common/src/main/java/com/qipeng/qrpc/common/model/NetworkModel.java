package com.qipeng.qrpc.common.model;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/26 8:04 下午
 */
public enum NetworkModel {

    NETTY("netty"),
    BIO("bio"),
    NIO("nio"),
    SIMPLE_NIO("simpleNio"),
    ;

    private final String name;

    NetworkModel(String name) {
        this.name = name;
    }

    public static NetworkModel getByName(String name) {
        for (NetworkModel networkModel : values()) {
            if (networkModel.name.equals(name)) {
                return networkModel;
            }
        }
        return NETTY;
    }

}
