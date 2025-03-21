package com.cryptotpmail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.BodyPart;

public class FetchEmailByte {

   public static ArrayList<Email> fetch(String user, String password) {
      String host = "imap.gmail.com";
      ArrayList<Email> list = new ArrayList<Email>();
      try {
         // create properties field
         Properties properties = new Properties();
         properties.put("mail.store.protocol", "imap");
         properties.put("mail.imap.host", host);
         properties.put("mail.imap.port", "993");
         properties.put("mail.imap.ssl.enable", "true");
         properties.put("mail.imap.auth", "true");

         Session emailSession = Session.getDefaultInstance(properties);
         // emailSession.setDebug(true);

         // create the POP3 store object and connect with the pop server
         Store store = emailSession.getStore("imaps");

         store.connect(host, user, password);

         // create the folder object and open it
         Folder emailFolder = store.getFolder("INBOX");
         emailFolder.open(Folder.READ_ONLY);

         // retrieve the messages from the folder in an array and print it
         Message[] messages = emailFolder.getMessages();
         if (messages != null) {
            for (Message message : messages) {
               Email email = new Email(message);
               System.out.println(email);
               list.add(email);
            }
         }

         // close the store and folder objects
         emailFolder.close(false);
         store.close();
      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return list;
   }

   /*
    * This method checks for content-type
    * based on which, it processes and
    * fetches the content of the message
    */

   public static void writePart(Part p) throws Exception {
      System.out.println(p.getContentType());
      if (p.isMimeType("multipart/MIXED")) {
         Multipart multipart = (Multipart) p.getContent();
         for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart BodyPart = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(BodyPart.getDisposition())) {
               String nomFichier = BodyPart.getFileName();
               String[] parts = nomFichier.split("\\\\");
               String nomDuFichier = parts[parts.length - 1];

               InputStream input = BodyPart.getInputStream();
               ByteArrayOutputStream buffer = new ByteArrayOutputStream();
               byte[] data = new byte[1024];
               int byteLu;
               while ((byteLu = input.read(data, 0, data.length)) != -1) {
                  buffer.write(data, 0, byteLu);
               }
               byte[] fichierEnByte = buffer.toByteArray();
               FileOutputStream fichier = new FileOutputStream(new File(nomDuFichier));
               fichier.write(fichierEnByte);
               fichier.close();

            }
         }
      } else {
         System.out.println("Mail sans pièce jointe");
         System.out.println("---------------------------------");
         System.out.println("Subject: " + ((Message) p).getSubject());
         System.out.println("From: " + ((Message) p).getFrom()[0]);
         System.out.println("Text: " + ((Message) p).getContent().toString());

      }
   }

   /*
    * This method would print FROM,TO and SUBJECT of the message
    */
   public static void writeEnvelope(Message m) throws Exception {
      System.out.println("This is the message envelope");
      System.out.println("---------------------------");
      Address[] a;

      // FROM
      if ((a = m.getFrom()) != null) {
         for (int j = 0; j < a.length; j++)
            System.out.println("FROM: " + a[j].toString());
      }

      // TO
      if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
         for (int j = 0; j < a.length; j++)
            System.out.println("TO: " + a[j].toString());
      }

      // SUBJECT
      if (m.getSubject() != null)
         System.out.println("SUBJECT: " + m.getSubject());

   }

}