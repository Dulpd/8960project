package com.itheima;

import org.junit.jupiter.api.Test;

public class ThreadLocalTest {

    @Test
    public void testThreadLocalSetAndGet() {
        //提供一个threadlocal对象
        ThreadLocal tl=new ThreadLocal();

        //开启两个线程
        new Thread(()->{
            tl.set("lsq");
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
        },"blue").start();

        new Thread(()->{
            tl.set("ysy");
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
            System.out.println(Thread.currentThread().getName()+" : "+tl.get());
        },"green").start();
    }
}
