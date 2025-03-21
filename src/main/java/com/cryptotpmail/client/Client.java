/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.cryptotpmail.elgamal.PairKeys;
import com.cryptotpmail.ibe.AESCrypto;
import com.cryptotpmail.ibe.IBEBasicIdent;
import com.cryptotpmail.ibe.IBEcipher;
import com.cryptotpmail.SendAttachmentInEmail;
import com.cryptotpmail.elgamal.EXschnorsig;
import com.cryptotpmail.elgamal.ElgamalCipher;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author imino
 */
public class Client {

    public static void main(String[] args) {

        // PAIRING DU IBE
        Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");

        String id = "bcd.saltel@gmail.com";
        String password = "azerty";

        ClientSessionKey sessionKey = sessionParameters(pairingIBE, id, password);

        boolean test = authentification(id, password, sessionKey);

        // Si null, redemander password côté interface

        // String filepath = "D:\\INSA\\4A ICY\\Cryptographie
        // Avancée\\TP\\cryptotpmail\\src\\main\\java\\com\\cryptotpmail\\elgamal\\filetoencrypt.txt";
        // String filename = filepath.substring(filepath.lastIndexOf("\\") + 1);

        // // ENCRYPTION IBE
        // byte[] ibecipher = encrypt_file_IBE(pairingIBE, client.getP(),
        // client.getP_pub(), filepath, id);

        // // DECRYPTION FICHIER IBE
        // decrypt_file_IBE(pairingIBE, client.getP(), client.getP_pub(),
        // filename, client.getSk(), ibecipher);

    }

    // Hashage SHA256
    public static byte[] hashSHA256Base64(String password) {
        try {
            MessageDigest digestSHA256 = MessageDigest.getInstance("SHA256");
            Encoder encoder = Base64.getEncoder();
            digestSHA256.update(password.getBytes());
            byte[] hash = digestSHA256.digest(); // Calcul du hash
            byte[] hashBase64 = encoder.encode(hash);
            return hashBase64;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static boolean authentification(String id, String password, ClientSessionKey session) {
        Encoder encoder = Base64.getEncoder();
        Decoder decoder = Base64.getDecoder();

        try {

            URL url = new URI("http://localhost:8080/authentification").toURL();
            // URL url = new URL("https://www.google.com");

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Session", new String(encoder.encode(session.getSessionID())));
            OutputStream out = urlConn.getOutputStream();

            byte[] passwordHash = hashSHA256Base64(password);

            byte[] message = new byte[id.getBytes().length + passwordHash.length + 1];
            System.arraycopy(id.getBytes(), 0, message, 0, id.getBytes().length);
            System.arraycopy(",".getBytes(), 0, message, id.getBytes().length, 1);
            System.arraycopy(passwordHash, 0, message, id.getBytes().length + 1, passwordHash.length);

            byte[] message_encrypted = com.cryptotpmail.elgamal.AESCrypto.encryptV2(message,
                    session.getAesKey());

            out.write(message_encrypted);
            out.close();

            int code = urlConn.getResponseCode();

            System.out.println(code == 200);
            return code == 200;

        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    // Authentification et récupération des paramètres de chiffrement
    public static ClientSessionKey sessionParameters(Pairing pairingIBE, String id, String password) {
        // PAIRING DU ELGAMAL
        Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");

        Encoder encoder = Base64.getEncoder();
        Decoder decoder = Base64.getDecoder();

        try {

            Element generatorElGamal = pairingElGamal.getG1().newRandomElement();
            PairKeys pairkeysElGamal = EXschnorsig.keygen(pairingElGamal, generatorElGamal); // keygen

            URL url = new URI("http://localhost:8080/sessionkey").toURL();
            // URL url = new URL("https://www.google.com");

            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            OutputStream out = urlConn.getOutputStream();

            // byte[] hashBase64 = hashSHA256Base64(password);
            byte[] generatorElGamalBase64 = encoder.encode(generatorElGamal.toBytes());
            byte[] pubKeyElGamalBase64 = encoder.encode(pairkeysElGamal.getPubkey().toBytes());

            // out.write(id.getBytes());
            // out.write(",".getBytes());
            // out.write(hashBase64);
            // out.write(",".getBytes());
            out.write(generatorElGamalBase64);
            out.write(",".getBytes());
            out.write(pubKeyElGamalBase64);

            InputStream dis = urlConn.getInputStream();
            int contentLength = urlConn.getContentLength();
            byte[] b = new byte[contentLength];
            int bytesRead = 0;
            while (bytesRead < contentLength) {
                int result = dis.read(b, bytesRead, contentLength - bytesRead);
                if (result == -1)
                    break;
                bytesRead += result;
            }
            String content = new String(b);
            System.out.println(content);

            Element u = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[0]));
            Element v = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[1]));
            byte[] AESciphertext = decoder.decode(content.split(",")[2]);

            ElgamalCipher cypherElgamal = new ElgamalCipher(u, v, AESciphertext);

            String message_retrieved = EXschnorsig.elGamaldec(pairingElGamal,
                    generatorElGamal, cypherElgamal,
                    pairkeysElGamal.getSecretkey());

            // byte[] skBytes_retrieved = decoder.decode(message_retrieved);
            // Element sk_retrieved =
            // pairingIBE.getG1().newElementFromBytes(skBytes_retrieved);

            System.out.println("Message retrieved: " + message_retrieved);
            return new ClientSessionKey(decoder.decode(message_retrieved.split(",")[0]),
                    decoder.decode(message_retrieved.split(",")[1]));

            // boolean isAuth = Boolean.parseBoolean(content.split(",")[0]);
            // if (isAuth) {
            // System.out.println("Authentification réussie");

            // Element ibeP =
            // pairingIBE.getG1().newElementFromBytes(decoder.decode(content.split(",")[1]));
            // Element ibePpub =
            // pairingIBE.getG1().newElementFromBytes(decoder.decode(content.split(",")[2]));
            // Element u =
            // pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[3]));
            // Element v =
            // pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[4]));
            // byte[] AESciphertext = decoder.decode(content.split(",")[5]);

            // ElgamalCipher cypherElgamal = new ElgamalCipher(u, v, AESciphertext);

            // String skBytesBase64_retrieved = EXschnorsig.elGamaldec(pairingElGamal,
            // generatorElGamal, cypherElgamal,
            // pairkeysElGamal.getSecretkey());
            // byte[] skBytes_retrieved = decoder.decode(skBytesBase64_retrieved);
            // Element sk_retrieved =
            // pairingIBE.getG1().newElementFromBytes(skBytes_retrieved);

            // return new ClientIBEParams(ibeP, ibePpub, sk_retrieved);

            // } else {
            // System.out.println("Authentification échouée.");
            // return null;
            // }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, byte[] filebytes,
            String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        Element random = pairingIBE.getG1().newRandomElement();
        byte[] file_encrypted = AESCrypto.encrypt(filebytes, random.toBytes());

        IBEcipher c = IBEBasicIdent.IBEencryption(pairingIBE, param_p, param_p_pub, random.toBytes(), pk);
        byte[] U = Base64.getEncoder().encode(c.getU().toBytes());
        byte[] V = Base64.getEncoder().encode(c.getV());
        byte[] Aescipher = Base64.getEncoder().encode(c.getAescipher());
        byte[] file_encrypted_base64 = Base64.getEncoder().encode(file_encrypted);
        byte[] virgule = ",".getBytes();

        byte[] encrypted = new byte[U.length + V.length + Aescipher.length + file_encrypted_base64.length + 3];

        System.arraycopy(U, 0, encrypted, 0, U.length);
        System.arraycopy(virgule, 0, encrypted, U.length, 1);
        System.arraycopy(V, 0, encrypted, U.length + 1, V.length);
        System.arraycopy(virgule, 0, encrypted, U.length + 1 + V.length, 1);
        System.arraycopy(Aescipher, 0, encrypted, U.length + 1 + V.length + 1, Aescipher.length);
        System.arraycopy(virgule, 0, encrypted, U.length + 1 + V.length + 1 + Aescipher.length, 1);
        System.arraycopy(file_encrypted_base64, 0, encrypted, U.length + 1 + V.length + 1 + Aescipher.length + 1,
                file_encrypted_base64.length);

        return encrypted;

    }

    public static byte[] encrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, String filepath,
            String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        FileInputStream in = new FileInputStream(filepath); // ouverture d'un stream de lecture sur le fichier

        byte[] filebytes = new byte[in.available()]; // réservation d'un tableau de byte en fontion du nombre de bytes
                                                     // contenus dans le fichier

        // System.out.println("taille de fichier en byte:" + filebytes.length);

        in.read(filebytes); // lecture du fichier

        in.close();

        return encrypt_file_IBE(pairingIBE, param_p, param_p_pub, filebytes, pk); // chiffrement
                                                                                  // BasicID-IBE/AES
    }

    public static void decrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, String filename,
            Element sk, byte[] encrypted) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {

        String ibecypher_bytes_string = new String(encrypted);
        System.out.println(ibecypher_bytes_string);
        String u = ibecypher_bytes_string.split(",")[0];
        String v = ibecypher_bytes_string.split(",")[1];
        String Aescipher = ibecypher_bytes_string.split(",")[2];
        String file_encrypted = ibecypher_bytes_string.split(",")[3];

        Element U = pairingIBE.getG1().newElementFromBytes(Base64.getDecoder().decode(u));
        byte[] V = Base64.getDecoder().decode(v);
        byte[] Aescipher_bytes = Base64.getDecoder().decode(Aescipher);
        byte[] file_encrypted_bytes = Base64.getDecoder().decode(file_encrypted);

        IBEcipher c = new IBEcipher(U, V, Aescipher_bytes);

        byte[] aes_key = IBEBasicIdent.IBEdecryption(pairingIBE, param_p, param_p_pub, sk, c); // déchiffrment
                                                                                               // Basic-ID
                                                                                               // IBE/AES

        byte[] messageBytes_retrieved = AESCrypto.decrypt(file_encrypted_bytes, aes_key); // déchiffrement AES
        File f = new File("decrypted_" + filename); // création d'un fichier pour l'enregistrement du résultat du
                                                    // déchiffrement

        f.createNewFile();

        FileOutputStream fout = new FileOutputStream(f);

        fout.write(messageBytes_retrieved); // ecriture du résultat de déchiffrement dans le fichier

        fout.close();
    }
}
