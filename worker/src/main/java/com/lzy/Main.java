package com.lzy;

import com.lzy.gen.OrderBuilder;
import com.lzy.model.Order;

/**
 * @author liuzhengyang
 */
public class Main {
    public static void main(String[] args) {
        Order build = new OrderBuilder().buildId(2).buildAddTime(System.currentTimeMillis()).build();
        System.out.println(build);
    }
}
