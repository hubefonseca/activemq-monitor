package net.intelie.mointor;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.log4j.Logger;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;

/**
 *
 */
public class QueueMonitor {

    private static Logger logger = Logger.getLogger(QueueMonitor.class);

    private String queueName;
    private String brokerName;

    private LinkedList<Integer> lastSamples = new LinkedList<Integer>();

    public QueueMonitor(String queueName, String brokerName) {
        logger.info("Starting queue monitor for queue " + queueName);

        this.queueName = queueName;
        this.brokerName = brokerName;
    }

    public void check(QueueViewMBean queueView) throws UndeclaredThrowableException {
        logger.info("size : " + queueView.getQueueSize());
    }

}
