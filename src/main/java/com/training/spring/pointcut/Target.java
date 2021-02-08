package com.training.spring.pointcut;

public class Target implements TargetInterface{
    public void hello() {}
    public void hello(String a) {}
    public int minus(int a, int b) throws RuntimeException {return 0;}
    public int plus(int a, int b){return 0;}
    public void method() {}

//    public static void main(String[] args) throws NoSuchMethodException {
//        System.out.println(Target.class.getMethod("minus", int.class, int.class));
//        // print :: public int com.training.spring.pointcut.Target.minus(int,int) throws java.lang.RuntimeException
//    }
}
