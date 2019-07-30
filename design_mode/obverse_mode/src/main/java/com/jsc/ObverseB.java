package com.jsc;

public class ObverseB implements Obverse {
    @Override
    public void update(String status) {
        System.out.println("sub状态改变，状态值为："+status);
    }
}
