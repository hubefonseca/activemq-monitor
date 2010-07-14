package net.intelie.mointor.notifiers;

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

    public void send(List<String> recipients, String subject, String body) {
        try {
            MimeMessage message = new MimeMessage(session);

            for (String to : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        }
        catch (MessagingException e) {
            logger.error("Could not send email. Verify correctness of file mail.properties", e);
        }

    }

}
