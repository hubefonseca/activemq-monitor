package net.intelie.monitor.engine;

import net.intelie.monitor.events.QueueStoppedConsuming;
import net.intelie.monitor.listeners.Listener;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.log4j.Logger;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Checks number of dequeued messages and alerts when a queue has stopped consumption.
 */
public class QueueMonitor {

    private static Logger logger = Logger.getLogger(QueueMonitor.class);

    private String queueName;
    private String brokerName;
    private String domain;
    private Listener listener;

    private List<Long> lastChecks = new ArrayList<Long>();
    private long lastNotificationTimestamp = 0;
    private long minInterval = 0;

    private static final int MAX_QUEUE_SIZE = 5;
    private static final int EVENTS_BEFORE_NOTIFY = 3;

    public QueueMonitor(String queueName, String brokerName, String domain, Listener listener, long minInterval) {
        logger.info("Starting queue monitor for queue " + queueName);

        this.queueName = queueName;
        this.brokerName = brokerName;
        this.domain = domain;

        this.listener = listener;
        this.minInterval = minInterval;
    }

    public void check(MBeanServerConnection mbConn) throws UndeclaredThrowableException {
        Hashtable<String, String> table = new Hashtable<String, String>();

        table.put("BrokerName", this.brokerName);
        table.put("Type", "Queue");
        table.put("Destination", queueName);

        try {
            QueueViewMBean queueView = JMX.newMBeanProxy(mbConn, new ObjectName(this.domain, table), QueueViewMBean.class);

            while (lastChecks.size() > MAX_QUEUE_SIZE) {
                lastChecks.remove(0);
            }
                                           
            lastChecks.add(queueView.getDequeueCount());

            evaluate();
        } catch (UndeclaredThrowableException e) {
            logger.warn("MBean not found. Does queue " + queueName + " exist?");
            logger.debug(e);
        } catch (MalformedObjectNameException e) {
            logger.warn("MBean not found. Does queue " + queueName + " exist?");
            logger.debug(e);
        }
    }

    public void evaluate() {
        if (EVENTS_BEFORE_NOTIFY > 0 && lastChecks.size() >= EVENTS_BEFORE_NOTIFY) {
            boolean notify = true;

            long lastCheck = lastChecks.get(lastChecks.size() - 1);
            for (int i = lastChecks.size() - 2; lastChecks.size() - i <= EVENTS_BEFORE_NOTIFY; i--) {
                if (lastCheck != lastChecks.get(i)) {
                    notify = false;
                }
                lastCheck = lastChecks.get(i);
            }

            long now = System.currentTimeMillis();
            if (notify) {
                if (now > lastNotificationTimestamp + minInterval) {
                    listener.notify(new QueueStoppedConsuming(this.queueName));
                    lastNotificationTimestamp = now;
                } else {
                    logger.warn("Error notification already sent. Waiting to send another");
                }
            } else {
                logger.debug("Queue " + queueName + " is fine.");
            }
        }
    }

}
