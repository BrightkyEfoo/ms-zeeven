package com.cs.ge.services;

import com.cs.ge.entites.Utilisateur;
import com.cs.ge.entites.Verification;
import com.cs.ge.exception.ApplicationException;
import com.cs.ge.repositories.VerificationRepository;
import com.cs.ge.utils.Data;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final String accountLink;

    public VerificationService(
            @Value("${spring.mail.accountLink: ''}") final String accountLink,
            final SpringTemplateEngine templateEngine,
            final VerificationRepository verificationRepository,
            final JavaMailSender javaMailSender
    ) {
        this.accountLink = accountLink;
        this.templateEngine = templateEngine;
        this.verificationRepository = verificationRepository;
        this.javaMailSender = javaMailSender;
    }

    public Verification getByCode(final String code) {
        return this.verificationRepository.findByCode(code).orElseThrow(
                () -> new ApplicationException("le code d'activation n'est pas disponible"));
    }

    public Verification createCode(final Utilisateur utilisateur) {
        final Verification verification = new Verification();
        verification.setUsername(utilisateur.getUsername());
        final String randomCode = RandomString.make(20);
        verification.setCode(randomCode);
        verification.setDateCreation(LocalDateTime.now());
        verification.setDateExpiration(LocalDateTime.now().plusMinutes(15));
        verification.setUtilisateur(utilisateur);
        return this.verificationRepository.save(verification);
    }

    public void sendEmail(final Utilisateur utilisateur, final String code) throws MessagingException, IOException {

        final Map<String, Object> props = new HashMap<String, Object>();
        props.put("firstName", utilisateur.getFirstName());
        props.put("lastName", utilisateur.getLastName());
        props.put("activationLink", String.format("%s%s", this.accountLink, code));
        props.put("code", code);
        props.put("activationLabel", "Activez votre compte");

        final MimeMessage message = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        final Context context = new Context();


        context.setVariables(props);
        final String html = this.templateEngine.process("account", context);
        helper.setTo(utilisateur.getEmail());
        helper.setText(html, true);
        helper.setSubject("Activez votre compte");
        helper.setFrom(Data.EMAIL_FROM);
        this.javaMailSender.send(message);
    }
}
