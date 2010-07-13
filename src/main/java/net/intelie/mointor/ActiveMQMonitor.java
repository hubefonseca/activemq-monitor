package net.intelie.mointor;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.log4j.Logger;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;
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
    private String[] monitoredQueues;

    private JMXConnector connector = null;

    public ActiveMQMonitor() {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("activemq-monitor.properties"));

            monitoredQueues = properties.getProperty("monitor").split(",");

            server = properties.getProperty("server");
            port = properties.getProperty("port");
            path = properties.getProperty("connectorPath");

            domain = properties.getProperty("domain");
            brokerName = properties.getProperty("brokerName");

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

            for (String queue : monitoredQueues) {
                Hashtable<String, String> table = new Hashtable<String, String>();

                table.put("BrokerName", this.brokerName);
                table.put("Type", "Queue");
                table.put("Destination", queue);

                try {

                    QueueMonitor monitor = new QueueMonitor(queue, this.brokerName);

                    QueueViewMBean queueView = JMX.newMBeanProxy(mbConn, new ObjectName(this.domain, table), QueueViewMBean.class);

                    monitor.check(queueView);

                } catch (UndeclaredThrowableException e) {
                    logger.warn("MBean not found. Does queue " + queue + " exist?");
                    logger.debug(e);
                }
            }

            disconnect();

        } catch (IOException e) {
            logger.error("Could not connect to ActiveMQ. Is instance running and reachable at " + server + ":" + port + "?", e);
        } catch (MalformedObjectNameException e) {
            logger.error("Could not find broker. Are domain and brokerName correct?", e);
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
            logger.info("Connecting to " + server + " on port " + port);

            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + server + ":" + port + connectorPath);
            connector = JMXConnectorFactory.newJMXConnector(url, new HashMap());
            connector.connect();

            logger.info("Connected " + connector.getConnectionId());
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
