package com.jsc.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EnableHelloWorldBootstrap {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EnableHelloWorldBootstrap.class, args);

/*        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(EnableHelloWorldBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        //helloWorldBean是否存在*/
        String hello = context.getBean("hello", String.class);
        System.out.println("hello = " + hello);
        context.close();
    }

}
