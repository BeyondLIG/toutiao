package com.nowcoder.async;

public enum EventType {// 事件类型的枚举类型
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;

    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
