package com.jsc.configuration;


import org.springframework.context.annotation.Bean;

public class HelloWorldAutoConfiguration {

    @Bean("hello")
    public String HelloWorld(){
        return "Hello World sxs";
    }
}
