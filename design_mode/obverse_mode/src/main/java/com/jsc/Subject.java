package com.jsc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Subject implements Obverse {

    private static List<Obverse> obverseList = new ArrayList<>();


    //添加观察者
    public  void attach(Obverse obverse) {
        obverseList.add(obverse);
        System.out.println("添加观察者");
    }

    //移除观察者
    public  void remove(Obverse obverse) {
        obverseList.remove(obverse);
        System.out.println("移除观察者");
    }

    /**
     * 状态改变通知其他观察者
     */
    public void update(String status) {
        obverseList.forEach(o -> o.update(status));
    }
}
