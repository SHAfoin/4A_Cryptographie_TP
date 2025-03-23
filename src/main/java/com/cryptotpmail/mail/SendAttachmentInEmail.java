package com.cryptotpmail.mail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

import com.cryptotpmail.client.Client;
import com.cryptotpmail.client.ClientIBEParams;

import it.unisa.dia.gas.jpbc.Pairing;

public class SendAttachmentInEmail {
   public static void sendMail(String from, String to, String subject, String body, ArrayList<File> listfile,
         String password, Pairing pairingIBE, ClientIBEParams client)
         throws IOException {

      // Défini l'adresse du serveur
      String host = "smtp.gmail.com";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      // port 25 pour le smtp "classique", 587 pour le smtp Secure
      props.put("mail.smtp.port", "25");

      // Crée une session
      Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(from, password);
               }
            });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: Emetteur
         message.setFrom(new InternetAddress(from));

         // Set To: Destinataire
         message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(to));

         // Set Subject: Sujet
         message.setSubject(subject);

         // Corps du mail
         BodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setText(body);

         // Pour pouvoir ajouter des pièces jointes, on utilise un corps avec plusieurs
         // parties
         Multipart multipart = new MimeMultipart();

         // Ajoute le texte ay mail
         multipart.addBodyPart(messageBodyPart);

         // Parcours les pièces jointes
         for (File file : listfile) {
            String filename = file.getName();
            byte[] fileToSend;

            if (pairingIBE == null || client == null) {
               // Dans le cas où il n'y aurait pas le necessaire pour le chiffrement
               fileToSend = Files.readAllBytes(file.toPath());
               System.out.println("Attention: Message non chiffré !");
            } else {
               fileToSend = Client.encrypt_file_IBE(pairingIBE, client.getP(),
                     client.getP_pub(), Files.readAllBytes(file.toPath()),
                     to);
            }

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
               mimeType = "application/octet-stream";
            }
            DataSource source = new ByteArrayDataSource(fileToSend, mimeType);
            MimeBodyPart piecejointe = new MimeBodyPart();
            piecejointe.setDataHandler(new DataHandler(source));
            piecejointe.setFileName(filename);

            multipart.addBodyPart(piecejointe);
         }

         message.setContent(multipart);

         Transport.send(message);
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
      } catch (MalformedURLException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvalidKeyException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchAlgorithmException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchPaddingException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalBlockSizeException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      } catch (BadPaddingException ex) {
         Logger.getLogger(SendAttachmentInEmail.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}