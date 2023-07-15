package com.cs.ge.services.emails;

import com.cs.ge.dto.Email;
import com.google.common.base.Strings;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class MailsSender {

    private final JavaMailSender mailSender;

    public MailsSender(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(final Email eParams) {
        if (eParams.isHtml()) {
            try {
                this.sendHtmlMail(eParams);
            } catch (final MessagingException e) {
                log.error("Could not send email to : {} Error = {}", eParams.getToAsList(), e.getMessage());
            }
        } else {
            this.sendPlainTextMail(eParams);
        }
    }

    private void sendHtmlMail(final Email eParams) throws MessagingException {
        try {
            final boolean isHtml = true;
            final MimeMessage message = this.mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8.name());


            //helper.addAttachment("facebook-icon", new ClassPathResource("static/images/facebook-icon.gif"));

            helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
            helper.setReplyTo(eParams.getFrom());
            helper.setFrom(eParams.getFrom());
            helper.setSubject(eParams.getSubject());
            helper.setText(eParams.getMessage(), true);
            if (!Strings.isNullOrEmpty(eParams.getImage())) {
                final byte[] imageBytes = Base64.decodeBase64(String.valueOf(eParams.getImage().getBytes("UTF-8")));
                final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
                helper.addInline("imageResource", imageSource, "image/png");
            }
            if (eParams.getCc().size() > 0) {
                helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
            }
            this.mailSender.send(message);
        } catch (final UnsupportedEncodingException | jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }

    }

    private void sendPlainTextMail(final Email eParams) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        eParams.getTo().toArray(new String[eParams.getTo().size()]);
        mailMessage.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
        mailMessage.setReplyTo(eParams.getFrom());
        mailMessage.setFrom(eParams.getFrom());
        mailMessage.setSubject(eParams.getSubject());
        mailMessage.setText(eParams.getMessage());
        if (eParams.getCc().size() > 0) {
            mailMessage.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
        }
        this.mailSender.send(mailMessage);
    }
}
