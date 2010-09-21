package net.intelie.monitor.notifiers;

import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EmailNotifier {

    private static Logger logger = Logger.getLogger(EmailNotifier.class);

    private Session session;

    public EmailNotifier() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("mail.properties"));
            session = Session.getDefaultInstance(properties, null);
        } catch (IOException e) {
            logger.error("Could not load properties. Is file mail.properties in classpath?", e);
        }
    }

    public void send(List<String> recipients, List<String> smsRecipients, String subject, String smsPasscode, String body) {
        try {
            MimeMessage message = new MimeMessage(session);
            MimeMessage sms = new MimeMessage(session);
            for (String to : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            for (String toSms : smsRecipients) {
                sms.addRecipient(Message.RecipientType.TO, new InternetAddress(toSms));
            }
            message.setSubject(subject);
            message.setText(body);
            sms.setSubject(smsPasscode);
            sms.setText(body);
            Transport.send(message);
            Transport.send(sms);
        }
        catch (MessagingException e) {
            logger.error("Could not send email. Verify correctness of file mail.properties", e);
        }

    }

}
