package ch.bastiangardel.LittleCafet.tool.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by bastiangardel on 14.07.17.
 */
@Service
public class EmailService {

    @Autowired
    private MailContentBuilder mailContentBuilder;

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void prepareAndSend(String sender, String receiver, String name, String object, BigDecimal solde) {

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            String content = mailContentBuilder.build(name,solde.toString());


            messageHelper.setText(content, true);

            messageHelper.setFrom(sender);
            messageHelper.setTo(receiver);
            messageHelper.setSubject(object);
        };

        mailSender.send(messagePreparator);
    }
}
