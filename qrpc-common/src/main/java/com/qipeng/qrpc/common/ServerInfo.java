package com.qipeng.qrpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerInfo {

    private String host;

    private int port;


}
