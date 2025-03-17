package com.cryptotpmail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public class Email {

    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private File attachment;
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
        this.timestamp = LocalDateTime.now(); // Date et heure d'envoi
    }

    // Constructeur avec pièce jointe
    public Email(String sender, String recipient, String subject, String body, File attachment) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.attachment = attachment;
        this.body = body;
        this.timestamp = LocalDateTime.now(); // Date et heure d'envoi
    }

    public Email(Message message) throws MessagingException, IOException {
        if (message.getFrom().length > 0) {
            this.sender = message.getFrom()[0].toString();
        }
        if (message.getAllRecipients().length > 0) {
            this.recipient = message.getAllRecipients()[0].toString();
        }
        this.subject = message.getSubject();
        this.body = message.getContent().toString();
        if (message.isMimeType("multipart/MIXED")) {
            Multipart multipart = (Multipart) message.getContent();
            if (multipart.getCount() > 0) {
                BodyPart body = multipart.getBodyPart(0);
                if (Part.ATTACHMENT.equalsIgnoreCase(body.getDisposition())) {
                    InputStream input = body.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int byteLu;
                    while ((byteLu = input.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, byteLu);
                    }
                    byte[] fichierEnByte = buffer.toByteArray();
                    File fichier = new File(body.getFileName());
                    FileOutputStream outStream = new FileOutputStream(fichier);
                    outStream.write(fichierEnByte);
                    outStream.close();
                    this.attachment = fichier;

                }
            }
        }
        this.timestamp = message.getReceivedDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
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

    public File getAttachment() {
        return attachment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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
