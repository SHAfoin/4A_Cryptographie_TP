package com.cryptotpmail.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.cryptotpmail.elgamal.EXschnorsig;

public class FetchMail {

	public static Message fetchMail(int id, String user, String password) throws MessagingException {
		// Serveur de Gmail
		String host = "imap.gmail.com";

		Properties properties = new Properties();
		properties.put("mail.store.protocol", "imap");
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", "993");
		properties.put("mail.imap.ssl.enable", "true");
		properties.put("mail.imap.auth", "true");

		Session emailSession = Session.getDefaultInstance(properties);
		Store store = emailSession.getStore("imaps");

		// Connection au serveur de récéption de la boite mail
		store.connect(host, user, password);

		// Ouvre le menu Boite de récéption
		Folder emailFolder = store.getFolder("INBOX");
		emailFolder.open(Folder.READ_ONLY);

		System.out.println(emailFolder.getMessageCount());
		// retrieve the messages from the folder in an array and print it
		Message message = emailFolder.getMessage(id);
		return message;

	}

	public static ArrayList<Email> fetchAllMails(String user, String password) {
		// Serveur de Gmail
		String host = "imap.gmail.com";
		// ArrayList pour stocker tous les mails récupérés
		ArrayList<Email> list = new ArrayList<Email>();
		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imap");
			properties.put("mail.imap.host", host);
			properties.put("mail.imap.port", "993");
			properties.put("mail.imap.ssl.enable", "true");
			properties.put("mail.imap.auth", "true");

			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore("imaps");

			// Se connecte au serveur de réception de mail
			store.connect(host, user, password);

			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// Récupère les mails et les parcours, en gardant les infos importantes dans des
			// objets de type Email
			Message[] messages = emailFolder.getMessages();
			if (messages != null) {
				for (Message message : messages) {
					Email email = new Email(message);
					list.add(email);
				}
			}

			emailFolder.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
		} catch (MessagingException ex) {
			Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
		}
		return list;
	}

}