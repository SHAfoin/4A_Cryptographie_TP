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

            Encoder encoder = Base64.getEncoder();
            Decoder decoder = Base64.getDecoder();

            // PAIRING DU IBE
            Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");
            SettingParameters param = IBEBasicIdent.setup(pairingIBE);

            // PAIRING DU ELGAMAL
            Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");
            

            System.out.println("my address:" + InetAddress.getLocalHost());
            // InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            InetSocketAddress s = new InetSocketAddress("localhost", 8080);

            HttpServer server = HttpServer.create(s, 0);
            System.out.println(server.getAddress());
            server.createContext("/service", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {
                    try {
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String content = new String(bytes);
                        // System.out.println(content);

                        // Récupérer l'ID
                        String id = content.split(",")[0];
                        // Récupérer le password
                        String password = content.split(",")[1];

                        // TODO : traiter le password
                        
                        // Récupérer le générateur par le client
                        Element generatorElGamal = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[2]));
                        // Récupérer la clé publique du client
                        Element clientPubKey = pairingElGamal.getG1().newElementFromBytes(decoder.decode(content.split(",")[3]));

                        // GENERATION DES CLES IBE
                        KeyPair Kp = IBEBasicIdent.keygen(pairingIBE, param.getMsk(), id);
                        byte[] skBytes = Kp.getSk().toBytes();
                        byte[] skBytesBase64 = encoder.encode(skBytes);

                        System.out.println("Secret key : " + new String(skBytesBase64));
                        System.out.println("Size : " + skBytesBase64.length);

                        ElgamalCipher cypherElgamal = EXschnorsig.elGamalencr(pairingElGamal, generatorElGamal, skBytesBase64, clientPubKey);

                        byte[] authentificationOK = "true".getBytes();
                        byte[] ibePBase64 = encoder.encode(param.getP().toBytes());
                        byte[] ibePpubBase64 = encoder.encode(param.getP_pub().toBytes());
                        byte[] uBase64 = Base64.getEncoder().encode(cypherElgamal.getU().toBytes());
                        byte[] vBase64 = Base64.getEncoder().encode(cypherElgamal.getV().toBytes());
                        byte[] cipherBase64 = Base64.getEncoder().encode(cypherElgamal.getAESciphertext());

                        he.sendResponseHeaders(200,authentificationOK.length + ibePBase64.length + ibePpubBase64.length +  uBase64.length + vBase64.length + cipherBase64.length + 5);
                        OutputStream os = he.getResponseBody();
                        os.write(authentificationOK);
                        os.write(',');
                        os.write(ibePBase64);
                        os.write(',');
                        os.write(ibePpubBase64);
                        os.write(',');
                        os.write(uBase64);
                        os.write(',');
                        os.write(vBase64);
                        os.write(',');
                        os.write(cipherBase64);
                        os.close();



                        // System.out.println(new String(cipherBase64));
                        

                    } catch (NoSuchAlgorithmException ex) {
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
                
                }});

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

}
