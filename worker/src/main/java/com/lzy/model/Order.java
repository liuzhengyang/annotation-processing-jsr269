package com.lzy.model;

import com.lzy.blog.Builder;

/**
 * @author liuzhengyang
 */
@Builder
public class Order {
    private long id;
    private long addTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", addTime=" + addTime +
                '}';
    }
}
