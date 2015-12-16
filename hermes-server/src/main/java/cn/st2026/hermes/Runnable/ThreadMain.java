package cn.st2026.hermes.Runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by Sloong on 2015/9/8.
 */
@Component
public class ThreadMain {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadMain.class);
    @Autowired
    public TcpRunnable tcpRunnable;

    @Autowired
    public HttpRunnable httpRunnable;

    @PostConstruct
    public void start(){
        Thread tcpThread = new Thread(tcpRunnable);
        tcpThread.setDaemon(true);
        tcpThread.start();

        Thread httpThread = new Thread(httpRunnable);
        httpThread.setDaemon(true);
        httpThread.start();
    }

    @PreDestroy
    public void stop(){
        LOG.info("Stopping server...");
        tcpRunnable.stop();
        httpRunnable.stop();
    }


    public HttpRunnable getHttpRunnable() {
        return httpRunnable;
    }

    public void setHttpRunnable(HttpRunnable httpRunnable) {
        this.httpRunnable = httpRunnable;
    }

    public TcpRunnable getTcpRunnable() {
        return tcpRunnable;
    }

    public void setTcpRunnable(TcpRunnable tcpRunnable) {
        this.tcpRunnable = tcpRunnable;
    }
}
