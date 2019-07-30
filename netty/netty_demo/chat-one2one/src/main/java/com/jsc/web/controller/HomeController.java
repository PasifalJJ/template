package com.jsc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/one")
    public String chat_one(){
        return "chat_one";
    }

    @RequestMapping("/two")
    public String chat_two(){
        return "chat_two";
    }
}
