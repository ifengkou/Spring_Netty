package cn.st2026.hermes.server;

import cn.st2026.hermes.cfg.SpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by Sloong on 2015/9/2.
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);


    public static final Object waitObject = new Object();
    public static void main(String[] args) throws InterruptedException {
        LOG.info("Start application context");
        @SuppressWarnings("resource")
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        ctx.registerShutdownHook();

        synchronized (waitObject) {
            waitObject.wait();
        }
    }


}
