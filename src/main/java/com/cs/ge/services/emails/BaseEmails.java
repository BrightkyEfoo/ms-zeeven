package com.cs.ge.services.emails;

import com.cs.ge.dto.Email;
import com.cs.ge.entites.Event;
import com.cs.ge.entites.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

@Component
public class BaseEmails {
    private static final String LIEN_URL = "activationLink";
    private static final String LIEN_LABEL = "activationLabel";
    private static final String NOM_DESTINATAIRE = "lastName";
    private static final String PRENOM_DESTINATAIRE = "firstName";
    private static final String EMAIL_PAR_DEFAUT = "marlene.zeeven@gmail.com";
    private static final String APPLICATION_LINK = "applicationLink";
    private static final String TITRE = "titre";
    private static final String EMAIL_DESTINATAIRE = "email";

    private final EMailContentBuilder eMailContentBuilder;
    private final String activationUrl;
    private final String applicationHost;
    private final String newPasswordUrl;

    public BaseEmails(
            EMailContentBuilder eMailContentBuilder,
            @Value("${app.host}") String applicationHost,
            @Value("${spring.mail.activation-url}") String activationUrl,
            @Value("${spring.mail.new-password-url}") String newPasswordUrl
    ) {
        this.eMailContentBuilder = eMailContentBuilder;
        this.activationUrl = activationUrl;
        this.applicationHost = applicationHost;
        this.newPasswordUrl = newPasswordUrl;
    }


    public Email newEvent(Event event) {
        Map<String, Object> replacements = new HashMap<>();
        replacements.put(TITRE, "Votre évènement a bien été enregistré");
        replacements.put(PRENOM_DESTINATAIRE, event.getAuthor().getFirstName());
        replacements.put(NOM_DESTINATAIRE, event.getAuthor().getLastName());
        replacements.put(LIEN_URL, this.applicationHost);
        replacements.put(LIEN_LABEL, "Connectez vous à votre compte");
        replacements.put(APPLICATION_LINK, this.applicationHost);
        replacements.put(EMAIL_DESTINATAIRE, event.getAuthor().getEmail());

        return this.getEmail(replacements, "new-event");

    }


    public Email newGuest(Profile guestProfile, Event event, String image) {
        Map<String, Object> replacements = new HashMap<>();
        replacements.put(TITRE, "Votre invitation");
        replacements.put(PRENOM_DESTINATAIRE, guestProfile.getFirstName());
        replacements.put(NOM_DESTINATAIRE, guestProfile.getLastName());
        replacements.put(LIEN_URL, this.applicationHost);
        replacements.put(APPLICATION_LINK, this.applicationHost);
        replacements.put(EMAIL_DESTINATAIRE, guestProfile.getEmail());
        replacements.put("event", String.format("au %s", event.getName().toLowerCase()));
        replacements.put("image", image);

        return this.getEmail(replacements, "new-ticket");
    }

    private Email getEmail(Map<String, Object> replacements, String template) {
        String message = this.eMailContentBuilder.getTemplate(template, replacements);
        Email email = new Email(
                EMAIL_PAR_DEFAUT,
                replacements.get(EMAIL_DESTINATAIRE).toString(),
                replacements.get(TITRE).toString(),
                message
        );
        email.setHtml(TRUE);
        return email;
    }
}
