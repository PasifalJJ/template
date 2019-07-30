package jsc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    @GetMapping("/room")
    public String goRoom(){
        System.out.println("访问thymleaf");
        return "chatroom";
    }
}
