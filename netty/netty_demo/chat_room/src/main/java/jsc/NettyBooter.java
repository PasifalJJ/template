package jsc;

import jsc.webSocketServer.SimpleChatServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                System.out.println("运行 NettyBooter ~ ~ ~");
                SimpleChatServer.run(8081);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
