package com.qipeng.qrpc.test.api;/**
 * @author qipenglin
 * @date 2019-09-05 16:36
 **/

import lombok.Data;

import java.io.Serializable;

/**
 * @author qipenglin
 * @date 2019-09-05 16:36
 **/
@Data
public class TestRequest implements Serializable {
    private String name;
}
