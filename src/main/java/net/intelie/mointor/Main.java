package net.intelie.mointor;

import org.apache.log4j.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static ActiveMQMonitor activeMQMonitor;

    public static void main(String[] args) {
        activeMQMonitor = new ActiveMQMonitor();

        Collector collector = new Collector(activeMQMonitor);
    }
    
}
