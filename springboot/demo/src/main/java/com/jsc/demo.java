package com.jsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class demo {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(demo.class, args);
        String hello  = context.getBean("hello",String.class);
        System.out.println("context = " + hello);
    }
}
