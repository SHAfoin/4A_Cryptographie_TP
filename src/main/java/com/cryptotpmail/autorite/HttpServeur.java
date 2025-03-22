package com.cryptotpmail.autorite;

import com.cryptotpmail.elgamal.AESCrypto;
import com.cryptotpmail.elgamal.EXschnorsig;
import com.cryptotpmail.elgamal.ElgamalCipher;
import com.cryptotpmail.ibe.IBEBasicIdent;
import com.cryptotpmail.ibe.KeyPair;
import com.cryptotpmail.ibe.SettingParameters;
import com.cryptotpmail.mail.SendAttachmentInEmail;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.util.HashMap; // import the HashMap class
import java.util.Random;

/**
 *
 * @author imino
 */
public class HttpServeur {

    public static void main(String[] args) {

        // Sauvegarde des utilisateurs
        HashMap<String, String> users = new HashMap<String, String>();
        HashMap<String, String> sessions = new HashMap<String, String>();
        HashMap<String, OTP> otp = new HashMap<String, OTP>();

        try {

            Encoder encoder = Base64.getEncoder();
            Decoder decoder = Base64.getDecoder();

            // PAIRING & SETUP DU IBE
            Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");
            SettingParameters param = IBEBasicIdent.setup(pairingIBE);

            // PAIRING DU ELGAMAL
            Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");

            // MISE EN PLACE DU SERVEUR
            System.out.println("my address:" + InetAddress.getLocalHost());
            // InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(),
            // 8080);
            InetSocketAddress s = new InetSocketAddress("localhost", 8080);
            HttpServer server = HttpServer.create(s, 0);
            System.out.println(server.getAddress());
            server.createContext("/sessionkey", new HttpHandler() {

                // REQUÊTE SUR /service SUR LE SERVEUR
                public void handle(HttpExchange he) throws IOException {

                    try {
                        // try {

                        // RECUPERATION DE id, password, generatorElGamal, clientPubKeyElGamal DU CLIENT
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String content = new String(bytes);

                        // String id = content.split(",")[0];
                        // String password = content.split(",")[1];
                        Element generatorElGamal = pairingElGamal.getG1()
                                .newElementFromBytes(decoder.decode(content.split(",")[0]));
                        Element clientPubKey = pairingElGamal.getG1()
                                .newElementFromBytes(decoder.decode(content.split(",")[1]));

                        // Générer un cookie pour l'utilisateur

                        byte[] sessionID = hashSHA256Base64(pairingElGamal.getGT().newRandomElement().toBytes());

                        System.out.println("sessionID:" + new String(sessionID));

                        // Générer une clé AES random

                        byte[] aeskey = pairingElGamal.getGT().newRandomElement().toBytes();
                        byte[] aeskeyBase64 = encoder.encode(aeskey);

                        // Sauvegarder le coookie avec la clé AES

                        sessions.put(new String(sessionID), new String(aeskeyBase64));

                        // Concaténer le tout

                        byte[] message = new byte[aeskeyBase64.length + sessionID.length + 1];
                        System.arraycopy(sessionID, 0, message, 0, sessionID.length);
                        System.arraycopy(",".getBytes(), 0, message, sessionID.length, ",".getBytes().length);
                        System.arraycopy(aeskeyBase64, 0, message, sessionID.length + 1,
                                aeskeyBase64.length);

                        System.out.println("message:" + new String(message));

                        ElgamalCipher cypherElgamal = EXschnorsig.elGamalencr(pairingElGamal,
                                generatorElGamal,
                                message, clientPubKey);

                        // Envoyer cookie, clé AES chiffrée avec la clé publique du client

                        byte[] uBase64 = Base64.getEncoder().encode(cypherElgamal.getU().toBytes());
                        byte[] vBase64 = Base64.getEncoder().encode(cypherElgamal.getV().toBytes());
                        byte[] cipherBase64 = Base64.getEncoder().encode(cypherElgamal.getAESciphertext());

                        he.sendResponseHeaders(200,
                                uBase64.length + vBase64.length
                                        + cipherBase64.length + 2);

                        OutputStream os = he.getResponseBody();
                        os.write(uBase64);
                        os.write(",".getBytes());
                        os.write(vBase64);
                        os.write(",".getBytes());
                        os.write(cipherBase64);
                        os.close();

                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            server.createContext("/authentification", new HttpHandler() {

                // REQUÊTE SUR /service SUR LE SERVEUR
                public void handle(HttpExchange he) throws IOException {

                    // RECUPERATION DE id, password, generatorElGamal, clientPubKeyElGamal DU CLIENT

                    try {
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String session = he.getRequestHeaders().getFirst("Session");
                        // System.out.println("session:" + session);
                        // System.out.println("session:" + new String(session));
                        String secret_decrypted = new String(
                                AESCrypto.decrypt(bytes,
                                        decoder.decode(sessions.get(session))));
                        System.out.println("secret_decrypted:" + secret_decrypted);

                        String id = secret_decrypted.split(",")[0];
                        String password = secret_decrypted.split(",")[1];

                        System.out.println("id:" + id);
                        System.out.println("password:" + password);

                        // VERIFICATION QUE L'UTILISATEUR EST BIEN ENREGISTRE
                        if (users.containsKey(id)) {
                            // VERIFICATION DU MOT DE PASSE
                            if (!users.get(id).equals(password)) { // SI LE MOT DE PASSE EST FAUX
                                he.sendResponseHeaders(401, -1);
                                return;
                            }

                        } else { // SINON L'ENREGISTRER
                            users.put(id, password);

                        }

                        System.out.println("users:" + users.toString());

                        he.sendResponseHeaders(200, -1);

                        he.getResponseBody().close();

                        String sendUsername = "tp.crypto.mail89";
                        String sendPassword = "ztan acej xhei wvtq";
                        Random random = new Random();
                        int deuxFA = random.nextInt();
                        deuxFA = (deuxFA % 899999) + 100000;
                        if (deuxFA < 0) {
                            deuxFA = deuxFA * -1;
                        }
                        String Msg2FA = Integer.toString(deuxFA);
                        System.out.println("Msg2FA:" + Msg2FA);
                        otp.put(session, new OTP(Msg2FA, Instant.now().plusSeconds(60 * 5)));
                        SendAttachmentInEmail.sendMail(sendUsername, id, "OTP Code", Msg2FA,
                                new ArrayList<File>(),
                                sendPassword, null, null);
                        System.out.println("Mail OTP envoyé...");

                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchPaddingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            server.createContext("/checkOTP", new HttpHandler() {

                // REQUÊTE SUR /ceckOTP SUR LE SERVEUR
                public void handle(HttpExchange he) throws IOException {

                    // RECUPERATION DE id, password, generatorElGamal, clientPubKeyElGamal DU CLIENT

                    try {
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String session = he.getRequestHeaders().getFirst("Session");
                        String secret_decrypted = new String(
                                AESCrypto.decrypt(bytes,
                                        decoder.decode(sessions.get(session))));
                        System.out.println("secret_decrypted:" + secret_decrypted);

                        String id = secret_decrypted.split(",")[0];
                        String OTP = secret_decrypted.split(",")[1];

                        System.out.println("OTP:" + OTP);
                        System.out.println("id:" + id);
                        Instant currentInstant = Instant.now();

                        // VERIFICATION QUE L'UTILISATEUR EST BIEN ENREGISTRE
                        if (otp.containsKey(session)) {

                            if (!otp.get(session).getOtp().equals(OTP)
                                    || currentInstant.isAfter(otp.get(session).getTimestamp())) { // SI LE MOT DE PASSE
                                                                                                  // EST FAUX
                                he.sendResponseHeaders(401, -1);
                                return;
                            }

                        } else { // SINON L'ENREGISTRER
                            he.sendResponseHeaders(401, -1);
                            return;

                        }

                        // GENERATION DE LA CLE IBE DU CLIENT
                        KeyPair Kp = IBEBasicIdent.keygen(pairingIBE, param.getMsk(), id);
                        byte[] skBytes = Kp.getSk().toBytes();
                        byte[] skBytesBase64 = encoder.encode(skBytes);

                        // ENVOI DE LA REPONSE AU CLIENT
                        byte[] ibePBase64 = encoder.encode(param.getP().toBytes());
                        byte[] ibePpubBase64 = encoder.encode(param.getP_pub().toBytes());

                        byte[] message = new byte[ibePBase64.length + ibePpubBase64.length + skBytesBase64.length + 2];
                        System.arraycopy(ibePBase64, 0, message, 0, ibePBase64.length);
                        System.arraycopy(",".getBytes(), 0, message, ibePBase64.length, ",".getBytes().length);
                        System.arraycopy(ibePpubBase64, 0, message, ibePBase64.length + 1, ibePpubBase64.length);
                        System.arraycopy(",".getBytes(), 0, message, ibePBase64.length + 1 + ibePpubBase64.length,
                                ",".getBytes().length);
                        System.arraycopy(skBytesBase64, 0, message,
                                ibePBase64.length + 1 + ibePpubBase64.length + 1,
                                skBytesBase64.length);

                        byte[] message_encrypted = com.cryptotpmail.elgamal.AESCrypto.encryptV2(message,
                                decoder.decode(sessions.get(session)));

                        he.sendResponseHeaders(200, message_encrypted.length);
                        OutputStream os = he.getResponseBody();
                        os.write(message_encrypted);
                        os.close();

                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchPaddingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Hashage SHA256
    public static byte[] hashSHA256Base64(byte[] password) {
        try {
            MessageDigest digestSHA256 = MessageDigest.getInstance("SHA256");
            Encoder encoder = Base64.getEncoder();
            digestSHA256.update(password);
            byte[] hash = digestSHA256.digest(); // Calcul du hash
            byte[] hashBase64 = encoder.encode(hash);
            return hashBase64;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
