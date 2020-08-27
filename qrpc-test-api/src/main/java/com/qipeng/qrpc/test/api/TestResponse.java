package com.qipeng.qrpc.test.api;/**
 * @author qipenglin
 * @date 2019-09-05 16:36
 **/

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qipenglin
 * @date 2019-09-05 16:36
 **/
@Data
public class TestResponse implements Serializable {

    private String name;

    private Date date;
}
