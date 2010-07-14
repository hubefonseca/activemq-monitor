package net.intelie.monitor.engine;

import net.intelie.monitor.listeners.QueueMonitorListener;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Gets information from ActiveMQ through JMX frequently and uses rules to determine if
 * is necessary to alert about some behaviour.
 */
public class ActiveMQMonitor {

    private static Logger logger = Logger.getLogger(ActiveMQMonitor.class);

    private String server;
    private String port;
    private String path;

    private String domain;
    private String brokerName;

    private LinkedList<QueueMonitor> monitors = new LinkedList<QueueMonitor>();

    private JMXConnector connector = null;

    public ActiveMQMonitor() {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("activemq-monitor.properties"));

            server = properties.getProperty("server");
            port = properties.getProperty("port");
            path = properties.getProperty("connectorPath");

            domain = properties.getProperty("domain");
            brokerName = properties.getProperty("brokerName");

            String[] recipients = properties.getProperty("recipients").split(",");
            long interval = Long.parseLong(properties.getProperty("minInterval")) * 60 * 1000;

            String company = properties.getProperty("company");
            QueueMonitorListener queueMonitorListener = new QueueMonitorListener(recipients, company);

            String[] monitoredQueues = properties.getProperty("monitor").split(",");
            for (String queueName : monitoredQueues) {
                QueueMonitor queueMonitor = new QueueMonitor(queueName.trim(), brokerName, domain, queueMonitorListener, interval);
                monitors.add(queueMonitor);
            }

        } catch (IOException e) {
            logger.warn("Could not load properties. Is file activemq-monitor.properties in classpath?", e);
        }
    }

    /**
     * Use JMX to find activemq statistics and send them to engine
     */
    public void fetch() {
        try {
            connect(server, port, path);

            MBeanServerConnection mbConn = connector.getMBeanServerConnection();

            for (QueueMonitor monitor : monitors) {
                monitor.check(mbConn);
            }

            disconnect();
        } catch (IOException e) {
            logger.error("Could not connect to ActiveMQ. Is instance running and reachable at " + server + ":" + port + "?", e);
        }
    }

    /**
     * Create an RMI connector and start it
     *
     * @param server
     * @param port
     */
    private void connect(String server, String port, String connectorPath) {
        JMXServiceURL url;
        try {
            logger.debug("Connecting to " + server + " on port " + port);

            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + server + ":" + port + connectorPath);
            connector = JMXConnectorFactory.newJMXConnector(url, new HashMap());
            connector.connect();

            logger.debug("Connected " + connector.getConnectionId());
        } catch (MalformedURLException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void disconnect() {
        try {
            connector.close();
        } catch (IOException e) {
            logger.error("Error closing connection", e);
        }
    }

}
