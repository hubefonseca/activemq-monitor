package net.intelie.mointor.engine;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Collector {

    private static Logger logger = Logger.getLogger(Collector.class);

    private ActiveMQMonitor activeMQMonitor;
    private static Timer timer;

    private static final Integer INTERVAL_IN_SECS = 3;


    public Collector(ActiveMQMonitor activeMQMonitor) {
        this.activeMQMonitor = activeMQMonitor;

        timer = new Timer();
        timer.schedule(new MonitorTask(), 0, INTERVAL_IN_SECS * 1000);
    }

    class MonitorTask extends TimerTask {
        public void run() {
            logger.debug("Retrieving information");
            activeMQMonitor.fetch();
        }
    }

}
