package com.cryptotpmail;
import java.io.File;
import java.time.LocalDateTime;


public class Email {
    
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private File attachment;
    private LocalDateTime timestamp;

    //Constructeur
    public Email(){

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
    public File getAttachment(){
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
    public void setAttachment(File file){
        this.attachment = attachment;
    }

    // Méthode pour afficher les détails de l'email
    @Override
    public String toString() {
        if(attachment!=null){
            return sender+"\t\t\t  attachment\t"+timestamp.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDayOfMonth(), 0, 0)+"\n"+body;
        }
        return sender+"\t\t\t\t\t\t"+timestamp.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDayOfMonth(), 0, 0)+"\n"+body;
        // return "Email{" +
        //         "De='" + sender + '\'' +
        //         ", À='" + recipient + '\'' +
        //         ", Sujet='" + subject + '\'' +
        //         ", Contenu='" + body + '\'' +
        //         ", Envoyé le=" + timestamp +
        //         '}';
    }
    // @Override
    // public String affichageMail(){
    //     return "From : "+sender+"\tSend at "+timestamp.toString();
    // }
}
