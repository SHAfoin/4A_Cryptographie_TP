package com.cryptotpmail;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public class Email {

    private int id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private String attachment;
    private LocalDateTime timestamp;

    // Constructeur
    public Email() {

    }

    // Constructeur sans pièce jointe
    public Email(String sender, String recipient, String subject, String body) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.attachment = "";
        this.timestamp = LocalDateTime.now(); // Date et heure d'envoi
    }

    // Constructeur avec pièce jointe
    public Email(String sender, String recipient, String subject, String body, String attachment) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.attachment = attachment;
        this.body = body;
        this.timestamp = LocalDateTime.now(); // Date et heure d'envoi
    }

    public Email(Message message) throws MessagingException, IOException {
        this.id = message.getMessageNumber();
        this.sender = "";
        for (Address address : message.getFrom()) {
            this.sender += address.toString() + ";";
        }
        this.recipient = "";
        for (Address address : message.getAllRecipients()) {
            this.sender += address.toString() + ";";
        }
        this.subject = message.getSubject();
        this.body = "";
        this.attachment = "";
        if (message.isMimeType("multipart/MIXED")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart body = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(body.getDisposition())) {
                    String filename = body.getFileName();
                    this.attachment += filename + ";";
                } else {
                    this.body += body.getContent().toString();
                }
            }
        }
        this.timestamp = message.getReceivedDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        System.out.println(this.id);
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getAttachment() {
        return attachment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    // Setters
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAttachment(File file) {
        this.attachment = attachment;
    }

    // Méthode pour afficher les détails de l'email
    @Override
    public String toString() {
        if (attachment != null) {
            return sender + "\t\t\t  attachment\t"
                    + timestamp.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDayOfMonth(), 0, 0) + "\n"
                    + body;
        }
        return sender + "\t\t\t\t\t\t"
                + timestamp.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDayOfMonth(), 0, 0) + "\n"
                + body;
        // return "Email{" +
        // "De='" + sender + '\'' +
        // ", À='" + recipient + '\'' +
        // ", Sujet='" + subject + '\'' +
        // ", Contenu='" + body + '\'' +
        // ", Envoyé le=" + timestamp +
        // '}';
    }
    // @Override
    // public String affichageMail(){
    // return "From : "+sender+"\tSend at "+timestamp.toString();
    // }
}
