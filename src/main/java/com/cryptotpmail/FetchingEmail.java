   package com.cryptotpmail;

   import java.io.BufferedOutputStream;
   import java.io.BufferedReader;
   import java.io.DataOutputStream;
   import java.io.File;
   import java.io.FileOutputStream;
   import java.io.IOException;
   import java.io.InputStream;
   import java.io.InputStreamReader;
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

   public class FetchingEmail {

      public static void fetch(String pop3Host, String storeType, String user,
         String password) {
         try {
            // create properties field
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imap");
            properties.put("mail.imap.host", pop3Host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");

            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("imaps");

            store.connect(pop3Host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0; i < messages.length; i++) {
               Message message = messages[i];
               System.out.println("---------------------------------");
               writePart(message);
               String line = reader.readLine();
               if ("YES".equals(line)) {
                  message.writeTo(System.out);
               } else if ("QUIT".equals(line)) {
                  break;
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
      }
      public static void main(String[] args) {

         String host = "imap.gmail.com";// change accordingly
         String mailStoreType = "imap";
         String username = "tp.crypto.mail89@gmail.com";// change accordingly
         String password = "ztan acej xhei wvtq";// change accordingly

         //Call method fetch
         fetch(host, mailStoreType, username, password);

      }

      /*
      * This method checks for content-type 
      * based on which, it processes and
      * fetches the content of the message
      */
      public static void writePart(Part p) throws Exception {
         if (p instanceof Message) {
            // Appel de la méthode writeEnvelope
            writeEnvelope((Message) p);
         }
      
         System.out.println("----------------------------");
         System.out.println("CONTENT-TYPE: " + p.getContentType());
      
         // Vérifiez si le contenu est du texte brut
         if (p.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------------");
            System.out.println((String) p.getContent());
         }
         // Si le contenu est de type multipart
         else if (p.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
               writePart(mp.getBodyPart(i));
            }
         }
         // Si le contenu est une image
         else if (p.getContentType().contains("IMAGE/")) {
            System.out.println("Image content type: " + p.getContentType());
      
            // Crée un fichier temporaire pour l'image
            File f = new File("image" + new Date().getTime() + ".jpg");
            DataOutputStream output = new DataOutputStream(
                  new BufferedOutputStream(new FileOutputStream(f)));
      
            // Lire les données binaires de l'image
            InputStream inputStream = (InputStream) p.getContent();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
               output.write(buffer, 0, bytesRead);
            }
            output.close();
            System.out.println("Image saved to: " + f.getAbsolutePath());
         } 
         // Si le contenu est une autre pièce jointe
         else {
            Object o = p.getContent();
            if (o instanceof String) {
               System.out.println("This is a string");
               System.out.println("---------------------------");
               System.out.println((String) o);
            } else if (o instanceof InputStream) {
               System.out.println("This is just an input stream");
               System.out.println("---------------------------");
               InputStream is = (InputStream) o;
               int c;
               while ((c = is.read()) != -1) {
                  System.out.write(c);
               }
            } else {
               System.out.println("This is an unknown type");
               System.out.println("---------------------------");
               System.out.println(o.toString());
            }
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