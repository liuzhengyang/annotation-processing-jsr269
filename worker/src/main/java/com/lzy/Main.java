package com.lzy;

import com.lzy.gen.OrderBuilder;

/**
 * @author liuzhengyang
 */
public class Main {
    public static void main(String[] args) {
        OrderBuilder orderBuilder = new OrderBuilder();
        orderBuilder.buildId(2);
        orderBuilder.buildAddTime(System.currentTimeMillis());
        System.out.println(orderBuilder.build());
    }
}
