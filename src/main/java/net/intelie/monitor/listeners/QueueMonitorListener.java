package net.intelie.monitor.listeners;

import net.intelie.monitor.events.Event;
import net.intelie.monitor.events.QueueStoppedConsuming;
import net.intelie.monitor.notifiers.EmailNotifier;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class QueueMonitorListener implements Listener {

    private static Logger logger = Logger.getLogger(QueueMonitorListener.class);

    private EmailNotifier emailNotifier;
    private List<String> recipients = new ArrayList<String>();
    private String companyName;
    private List<String> smsRecipients = new ArrayList<String>();
    private String smsPasscode;

    public QueueMonitorListener(String[] rec, String companyName, String[] smsRec, String smsPass) {
        for (String to : rec) {
            recipients.add(to.trim());
        }
        for (String to : smsRec){
            smsRecipients.add(to.trim());
        }
        this.companyName = companyName;
        this.emailNotifier = new EmailNotifier();
        this.smsPasscode = smsPass;
    }

    public void notify(Event event) {
        if (event instanceof QueueStoppedConsuming) {
            QueueStoppedConsuming queueStoppedConsuming = (QueueStoppedConsuming) event;
            logger.warn("Queue " + queueStoppedConsuming.getQueueName() + " stopped consuming events.");
            logger.warn("Notifying recipients: ");
            for (String to : recipients) {
                logger.warn(to);
            }

            emailNotifier.send(recipients, smsRecipients, "[ERRO] Problema no ambiente " + companyName, smsPasscode, "A fila " + queueStoppedConsuming.getQueueName() + " parou de ser consumida no ambiente: " + companyName);
        }
    }

}
