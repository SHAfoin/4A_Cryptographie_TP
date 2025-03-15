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
import com.cryptotpmail.ibe.KeyPair;
import com.cryptotpmail.ibe.SettingParameters;
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

        // PAIRING DU ELGAMAL
        Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");
        Element generatorElGamal = pairingElGamal.getG1().newRandomElement();
        PairKeys pairkeysElGamal=EXschnorsig.keygen(pairingElGamal, generatorElGamal); //keygen

        

        Encoder encoder = Base64.getEncoder();
        Decoder decoder = Base64.getDecoder();
        
        
        try {

            MessageDigest digestSHA256=MessageDigest.getInstance("SHA256");


            URL url = new URL("http://localhost:8080/service");
            // URL url = new URL("https://www.google.com");
           
            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            OutputStream out=urlConn.getOutputStream();
            
            
            String id = "bcd.saltel@gmail.com";
            
            String password="azerty";
            digestSHA256.update(password.getBytes());
            byte[] hash = digestSHA256.digest(); // Calcul du hash
            byte[] hashBase64 = Base64.getEncoder().encode(hash);
            
            byte[] generatorElGamalBase64 = Base64.getEncoder().encode(generatorElGamal.toBytes());
            byte[] pubKeyElGamalBase64 = Base64.getEncoder().encode(pairkeysElGamal.getPubkey().toBytes());

            out.write(id.getBytes());
            out.write(",".getBytes());
            out.write(hashBase64);
            out.write(",".getBytes());
            out.write(generatorElGamalBase64);
            out.write(",".getBytes());
            out.write(pubKeyElGamalBase64);
           
            
            InputStream  dis = urlConn.getInputStream();
            byte[] b=new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
            dis.read(b);
            String msg = new String(b);

        //    System.out.println("message reçu : " + msg);
        //    String[] msgParts = msg.split(",");

        boolean isAuth = Boolean.parseBoolean(msg.split(",")[0]);
        if (isAuth) {
            System.out.println("Authentification réussie");


            Element ibeP = pairingIBE.getG1().newElementFromBytes(decoder.decode(msg.split(",")[1]));
            Element ibePpub = pairingIBE.getG1().newElementFromBytes(decoder.decode(msg.split(",")[2]));
            Element u = pairingElGamal.getG1().newElementFromBytes(decoder.decode(msg.split(",")[3]));
            Element v = pairingElGamal.getG1().newElementFromBytes(decoder.decode(msg.split(",")[4]));
            byte[] AESciphertext = decoder.decode(msg.split(",")[5]);

            ElgamalCipher cypherElgamal = new ElgamalCipher(u, v, AESciphertext);

            
            // PROBLEME DE CONVERSION ATTENTION : remplacer la sortie de elgamaldec en byte[] ?? 
            String skBytesBase64_retrieved = EXschnorsig.elGamaldec(pairingElGamal, generatorElGamal, cypherElgamal, pairkeysElGamal.getSecretkey());
            byte[] skBytes_retrieved = decoder.decode(skBytesBase64_retrieved);

            System.out.println("Secret key retrieved : " + skBytesBase64_retrieved);
            System.out.println("Size : " + skBytesBase64_retrieved.getBytes().length);

            Element sk_retrieved = pairingIBE.getG1().newElementFromBytes(skBytes_retrieved);

            System.out.println("Secret key retrieved : " + new String(sk_retrieved.toBytes()));
            System.out.println("Size : " + sk_retrieved.toBytes().length);

            String filepath = "D:\\INSA\\4A ICY\\Cryptographie Avancée\\TP\\cryptotpmail\\src\\main\\java\\com\\cryptotpmail\\elgamal\\filetoencrypt.txt"; // chemin du fichier à chiffrer
            
            // ENCRYPTION IBE
            IBEcipher ibecipher = encrypt_file_IBE(pairingIBE, ibeP, ibePpub, filepath, id);

            // DECRYPTION FICHIER IBE
            decrypt_file_IBE(pairingIBE, ibeP, ibePpub, filepath.substring(filepath.lastIndexOf("\\")+1), sk_retrieved, ibecipher);

        }        
          
       
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

    public static IBEcipher encrypt_file_IBE(Pairing pairingIBE, Element param_p,Element param_p_pub, byte[] filebytes, String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return IBEBasicIdent.IBEencryption(pairingIBE, param_p, param_p_pub, filebytes, pk); // chiffrement BasicID-IBE/AES
    }

    public static IBEcipher encrypt_file_IBE(Pairing pairingIBE, Element param_p,Element param_p_pub, String filepath, String pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        
        FileInputStream in = new FileInputStream(filepath); // ouverture d'un stream de lecture sur le fichier

        byte[] filebytes = new byte[in.available()]; // réservation d'un tableau de byte en fontion du nombre de bytes contenus dans  le fichier

        // System.out.println("taille de fichier en byte:" + filebytes.length);

        in.read(filebytes); // lecture du fichier

        in.close();      
        
        return IBEBasicIdent.IBEencryption(pairingIBE, param_p, param_p_pub, filebytes, pk); // chiffrement BasicID-IBE/AES
    }

    public static void decrypt_file_IBE(Pairing pairingIBE, Element param_p,Element param_p_pub, String filename, Element sk, IBEcipher encrypted) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        
        byte[] messageBytes_retrieved = IBEBasicIdent.IBEdecryption(pairingIBE, param_p, param_p_pub, sk, encrypted); //déchiffrment Basic-ID IBE/AES
                        

        System.out.println(filename);
        File f = new File("decrypted_" + filename); // création d'un fichier pour l'enregistrement du résultat du déchiffrement

        f.createNewFile();

        FileOutputStream fout = new FileOutputStream(f);

        fout.write(messageBytes_retrieved); // ecriture du résultat de déchiffrement dans le fichier 

        fout.close();
    }
}
