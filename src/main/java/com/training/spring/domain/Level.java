package com.training.spring.domain;

public enum Level {
    // 순서 주의
    GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);

    private final int value;
    private final Level next;

    Level(int value, Level next) {
        this.value = value;     // DB에 저장할 int 타입의 값을 가지고 있지만 겉으로는 Level 타입 오브젝트
        this.next = next;
    }

    // Integer value getter
    public int intValue() {
        return value;
    }

    public Level nextLevel() {
        return this.next;
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
