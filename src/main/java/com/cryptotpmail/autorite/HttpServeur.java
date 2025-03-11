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
import com.cryptotpmail.ibe.KeyPair;
import com.cryptotpmail.ibe.SettingParameters;
import com.cryptotpmail.ibe.TestIBEAES;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
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
            Pairing pairing = PairingFactory.getPairing("curves/d159.properties");

            SettingParameters param = IBEBasicIdent.setup(pairing);

            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);
            System.out.println("my address:" + InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);

            HttpServer server = HttpServer.create(s, 0);
            System.out.println(server.getAddress());
            server.createContext("/service", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {
                    try {
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String content = new String(bytes);
                        System.out.println(content);

                        Decoder decoder = Base64.getDecoder();
                        String[] elems = content.split(",");
                        byte[] genBytes = decoder.decode(elems[1]);
                        
                        byte[] pubBytes = decoder.decode(elems[2]);
                        Element generator = pairing.getG1().newElementFromBytes(genBytes);
                        Element pubKeys = pairing.getG1().newElementFromBytes(pubBytes);
                        KeyPair Kp = IBEBasicIdent.keygen(pairing, param.getMsk(), new String(elems[0]));

                        ElgamalCipher c = EXschnorsig.elGamalencr(pairing, generator, Kp.getSk().toBytes(), pubKeys);
                        System.out.println(Kp.getSk());
                        byte[] uBase64 = Base64.getEncoder().encode(c.getU().toBytes());
                        byte[] vBase64 = Base64.getEncoder().encode(c.getV().toBytes());
                        byte[] cipherBase64 = Base64.getEncoder().encode(c.getAESciphertext());
                        System.out.println(new String(cipherBase64));
                        he.sendResponseHeaders(200, uBase64.length + vBase64.length + cipherBase64.length + 2);
                        OutputStream os = he.getResponseBody();
                        os.write(uBase64);
                        os.write(',');
                        os.write(vBase64);
                        os.write(',');
                        os.write(cipherBase64);
                        os.close();

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
                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
