package com.jsc;

import com.jsc.netty.WSServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class nettyStart implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                System.out.println("运行 NettyBooter ~ ~ ~");
                WSServer.getInstance().start(8080);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
