package com.training.spring.domain;

public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    Level(int value) {
        this.value = value;     // DB에 저장할 int 타입의 값을 가지고 있지만 겉으로는 Level 타입 오브젝트
    }

    // Integer value getter
    public int intValue() {
        return value;
    }

    public static Level valueOf(int value){
        switch (value){
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default:throw new AssertionError("unknown value: "+value);
        }
    }
}
