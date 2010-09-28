package net.intelie.monitor.engine;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 */
public class CPUMonitor {

    public String snmpGet(String strAddress, String community, String strOID, String SNMPPort){
        String str = "";
        try{
            OctetString com = new OctetString(community);
            strAddress = strAddress + "/" + SNMPPort;
            Address targetAddress = new UdpAddress(strAddress);
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(com);
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(targetAddress);
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            PDU pdu = new PDU();
            ResponseEvent response;
            Snmp snmp;
            pdu.add(new VariableBinding(new OID(strOID)));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            response = snmp.get(pdu,comtarget);
            if (response != null){
                if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")){
                    PDU pduresponse = response.getResponse();
                    str = pduresponse.getVariableBindings().firstElement().toString();
                    if(str.contains("=")){
                        int len = str.indexOf("=");
                        str = str.substring(len+1,str.length());
                    }
                }
            }
            else {
                System.out.println("TIMEOUT");
            }
            snmp.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("RESPONSE = " + str);
        return str;
    }

}
