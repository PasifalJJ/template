package com.jsc.zk;

import java.text.SimpleDateFormat;
import java.util.Date;

//生成订单号 时间戳
public class OrderNumGenerator {
  //区分不同的订单号
    private static int count = 0;
//单台服务器，多个线程 同事生成订单号
    public String getNumber(){
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            
        }
        SimpleDateFormat simpt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
        return simpt.format(new Date()) + "-" + ++count;  //时间戳后面加了 count

    }
    
}