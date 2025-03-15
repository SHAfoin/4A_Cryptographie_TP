/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.autorite;

import com.cryptotpmail.elgamal.EXschnorsig;
import com.cryptotpmail.elgamal.ElgamalCipher;
import com.cryptotpmail.elgamal.PairKeys;
import com.cryptotpmail.ibe.IBEBasicIdent;
import com.cryptotpmail.ibe.IBEcipher;
import com.cryptotpmail.ibe.KeyPair;
import com.cryptotpmail.ibe.SettingParameters;
import com.cryptotpmail.ibe.TestIBEAES;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author imino
 */
public class HttpServeur {

    public static void main(String[] args) {

        try {

            // PAIRING DU IBE
            Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");
            SettingParameters param = IBEBasicIdent.setup(pairingIBE);

            // PAIRING DU ELGAMAL
            Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");
            

            System.out.println("my address:" + InetAddress.getLocalHost());
            // InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);

            // HttpServer server = HttpServer.create(s, 0);
            // System.out.println(server.getAddress());
            // server.createContext("/service", new HttpHandler() {
            //     public void handle(HttpExchange he) throws IOException {
                    try {
                        // byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        // he.getRequestBody().read(bytes);
                        // String content = new String(bytes);
                        // System.out.println(content);
                        Encoder encoder = Base64.getEncoder();
                        Decoder decoder = Base64.getDecoder();

                        // Récupérer le générateur par le client
                        Element generatorElGamal=pairingElGamal.getG1().newRandomElement(); //génerateur
                        // Créer mes clés publiques/privées
                        // Même pas ? j'ai juste besoin de la clé publique du client
                        PairKeys pairkeysElGamal=EXschnorsig.keygen(pairingElGamal, generatorElGamal); //keygen

                        // Récupérer l'ID
                        String id = "bcd.saltel@gmail.com";

                        // String[] elems = content.split(",");

                        // String id = new String(elems[0]);
                        
                        // byte[] genBytes = decoder.decode(elems[1]);
                        // byte[] pubBytes = decoder.decode(elems[2]);


                        // Element generator = pairing.getG1().newElementFromBytes(genBytes);
                        // Element pubKeys = pairing.getG1().newElementFromBytes(pubBytes);

                        // GENERATION DES CLES IBE
                        KeyPair Kp = IBEBasicIdent.keygen(pairingIBE, param.getMsk(), id);
                        byte[] skBytes = Kp.getSk().toBytes();
                        byte[] skBytesBase64 = encoder.encode(skBytes);

                        System.out.println("Secret key : " + new String(skBytesBase64));
                        System.out.println("Size : " + skBytesBase64.length);

                        ElgamalCipher cypherElgamal = EXschnorsig.elGamalencr(pairingElGamal, generatorElGamal, skBytesBase64, pairkeysElGamal.getPubkey());


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
                        IBEcipher ibecipher = encrypt_file_IBE(pairingIBE, param.getP(), param.getP_pub(), filepath, Kp.getPk());

                        // DECRYPTION FICHIER IBE
                        decrypt_file_IBE(pairingIBE, param.getP(), param.getP_pub(), filepath.substring(filepath.lastIndexOf("\\")+1), sk_retrieved, ibecipher);

                        // ElgamalCipher c = EXschnorsig.elGamalencr(pairing, generator, Kp.getSk().toBytes(), pubKeys);
                        // System.out.println(Kp.getSk());
                        // byte[] uBase64 = Base64.getEncoder().encode(c.getU().toBytes());
                        // byte[] vBase64 = Base64.getEncoder().encode(c.getV().toBytes());
                        // byte[] cipherBase64 = Base64.getEncoder().encode(c.getAESciphertext());
                        // System.out.println(new String(cipherBase64));
                        // he.sendResponseHeaders(200, uBase64.length + vBase64.length + cipherBase64.length + 2);
                        // OutputStream os = he.getResponseBody();
                        // os.write(uBase64);
                        // os.write(',');
                        // os.write(vBase64);
                        // os.write(',');
                        // os.write(cipherBase64);
                        // os.close();

                    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                        // } catch (NoSuchPaddingException ex) {
                        // Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
                        // } catch (InvalidKeyException ex) {
                        // Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
                        // } catch (IllegalBlockSizeException ex) {
                        // Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
                        // } catch (BadPaddingException ex) {
                        // Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                // }});

            // server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
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
