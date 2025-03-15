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
import com.cryptotpmail.ibe.IBEBasicIdent;
import com.cryptotpmail.ibe.IBEcipher;
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

        try {

            ClientIBEParams client = mailEncryptionParameters(pairingIBE, id, password);
            // Si null, redemander password côté interface

            String filepath = "D:\\INSA\\4A ICY\\Cryptographie Avancée\\TP\\cryptotpmail\\src\\main\\java\\com\\cryptotpmail\\elgamal\\filetoencrypt.txt";
            String filename = filepath.substring(filepath.lastIndexOf("\\") + 1);

            // ENCRYPTION IBE
            IBEcipher ibecipher = encrypt_file_IBE(pairingIBE, client.getP(), client.getP_pub(), filepath, id);

            // DECRYPTION FICHIER IBE
            decrypt_file_IBE(pairingIBE, client.getP(), client.getP_pub(),
                    filename, client.getSk(), ibecipher);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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

    // Authentification et récupération des paramètres de chiffrement
    public static ClientIBEParams mailEncryptionParameters(Pairing pairingIBE, String id, String password) {
        // PAIRING DU ELGAMAL
        Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");

        Encoder encoder = Base64.getEncoder();
        Decoder decoder = Base64.getDecoder();

        try {

            Element generatorElGamal = pairingElGamal.getG1().newRandomElement();
            PairKeys pairkeysElGamal = EXschnorsig.keygen(pairingElGamal, generatorElGamal); // keygen

            URL url = new URI("http://localhost:8080/service").toURL();
            // URL url = new URL("https://www.google.com");

            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            OutputStream out = urlConn.getOutputStream();

            byte[] hashBase64 = hashSHA256Base64(password);
            byte[] generatorElGamalBase64 = encoder.encode(generatorElGamal.toBytes());
            byte[] pubKeyElGamalBase64 = encoder.encode(pairkeysElGamal.getPubkey().toBytes());

            out.write(id.getBytes());
            out.write(",".getBytes());
            out.write(hashBase64);
            out.write(",".getBytes());
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

            boolean isAuth = Boolean.parseBoolean(content.split(",")[0]);
            if (isAuth) {
                System.out.println("Authentification réussie");

                Element ibeP = pairingIBE.getG1().newElementFromBytes(decoder.decode(content.split(",")[1]));
                Element ibePpub = pairingIBE.getG1().newElementFromBytes(decoder.decode(content.split(",")[2]));
                Element u = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[3]));
                Element v = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[4]));
                byte[] AESciphertext = decoder.decode(content.split(",")[5]);

                ElgamalCipher cypherElgamal = new ElgamalCipher(u, v, AESciphertext);

                String skBytesBase64_retrieved = EXschnorsig.elGamaldec(pairingElGamal, generatorElGamal, cypherElgamal,
                        pairkeysElGamal.getSecretkey());
                byte[] skBytes_retrieved = decoder.decode(skBytesBase64_retrieved);
                Element sk_retrieved = pairingIBE.getG1().newElementFromBytes(skBytes_retrieved);

                return new ClientIBEParams(ibeP, ibePpub, sk_retrieved);

            } else {
                System.out.println("Authentification échouée.");
                return null;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IBEcipher encrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, byte[] filebytes,
            String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return IBEBasicIdent.IBEencryption(pairingIBE, param_p, param_p_pub, filebytes, pk); // chiffrement
                                                                                             // BasicID-IBE/AES
    }

    public static IBEcipher encrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, String filepath,
            String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        FileInputStream in = new FileInputStream(filepath); // ouverture d'un stream de lecture sur le fichier

        byte[] filebytes = new byte[in.available()]; // réservation d'un tableau de byte en fontion du nombre de bytes
                                                     // contenus dans le fichier

        // System.out.println("taille de fichier en byte:" + filebytes.length);

        in.read(filebytes); // lecture du fichier

        in.close();

        return IBEBasicIdent.IBEencryption(pairingIBE, param_p, param_p_pub, filebytes, pk); // chiffrement
                                                                                             // BasicID-IBE/AES
    }

    public static void decrypt_file_IBE(Pairing pairingIBE, Element param_p, Element param_p_pub, String filename,
            Element sk, IBEcipher encrypted) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] messageBytes_retrieved = IBEBasicIdent.IBEdecryption(pairingIBE, param_p, param_p_pub, sk, encrypted); // déchiffrment
                                                                                                                      // Basic-ID
                                                                                                                      // IBE/AES

        System.out.println(filename);
        File f = new File("decrypted_" + filename); // création d'un fichier pour l'enregistrement du résultat du
                                                    // déchiffrement

        f.createNewFile();

        FileOutputStream fout = new FileOutputStream(f);

        fout.write(messageBytes_retrieved); // ecriture du résultat de déchiffrement dans le fichier

        fout.close();
    }
}
