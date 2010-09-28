package net.intelie.monitor.engine;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class SNMPMonitor {

    private String community;
    private String snmpPort;
    private String OID;
    private String snmpVersion;


    public SNMPMonitor(){

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("snmp.properties"));
            community = properties.getProperty("community");
            snmpPort = properties.getProperty("SNMP_port");
            snmpVersion = properties.getProperty("mSNMPVersion");
            OID = properties.getProperty("OID_CPU_load_1_minute");
           //QueueMonitor queueMonitor = new QueueMonitor(queueName.trim(), brokerName, domain, queueMonitorListener, interval);
            CPUMonitor cpuMonitor = new CPUMonitor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
