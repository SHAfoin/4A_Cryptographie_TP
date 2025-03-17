package com.cryptotpmail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class SendAttachmentInEmail {
   public static void main(String[] args) throws IOException {
      // Recipient's email ID needs to be mentioned.
      String to = "tp.crypto.mail89@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "tp.crypto.mail89@gmail.com";
      final String username = "tp.crypto.mail89";// change accordingly
      final String password = "ztan acej xhei wvtq";// change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "25");

      // Get the Session object.
      Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(username, password);
               }
            });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("SIUUUUUUUUUUUUUUUUUUUUUUUUUUU Subject");

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText("This is message body TEST ENVOIE BYTE");

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment

         String filename = "D:\\Images\\Medaka.png";
         /*
          * File fichier_to_byte = new File(filename);
          * FileInputStream fluxBinaire = new FileInputStream(fichier_to_byte);
          * ByteArrayOutputStream ByteOutput = new ByteArrayOutputStream(); //Va stocker
          * la donnée en mémoire
          * byte[] buffer = new byte[1024];
          * int byteLu;
          * while((byteLu = fluxBinaire.read(buffer)) != -1){
          * ByteOutput.write(buffer,0,byteLu);
          * }
          * 
          * fluxBinaire.close()
          * byte [] fichier_byte = ByteOutput.toByteArray();
          */
         String[] parts = filename.split("\\\\");
         String nomDuFichier = parts[parts.length - 1];
         System.out.println(nomDuFichier);
         byte[] fichierEnByte = Files.readAllBytes(new File(filename).toPath());

         String mimeType = Files.probeContentType(new File(filename).toPath());
         if (mimeType == null) {
            mimeType = "application/octet-stream";
         }
         DataSource source = new ByteArrayDataSource(fichierEnByte, mimeType);

         MimeBodyPart piecejointe = new MimeBodyPart();
         piecejointe.setDataHandler(new DataHandler(source));
         piecejointe.setFileName(nomDuFichier);

         multipart.addBodyPart(piecejointe);
         message.setContent(multipart);

         Transport.send(message);
         System.out.println("Message envoyé avec piece jointe : " + filename);
         /*
          * messageBodyPart = new MimeBodyPart();
          * 
          * DataSource source = new FileDataSource(filename);
          * messageBodyPart.setDataHandler(new DataHandler(source));
          * messageBodyPart.setFileName(filename);
          * multipart.addBodyPart(messageBodyPart);
          * 
          * // Send the complete message parts
          * message.setContent(multipart);
          * 
          * // Send message
          * Transport.send(message);
          */
         System.out.println("Sent message successfully....");

      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}