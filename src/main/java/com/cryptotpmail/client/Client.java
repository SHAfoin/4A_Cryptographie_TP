/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cryptotpmail.elgamal.PairKeys;
import com.cryptotpmail.ibe.KeyPair;
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
        
        
        try {

            Pairing pairing = PairingFactory.getPairing("curves/d159.properties");


                  URL url = new URL("http://localhost:8080/service");
            // URL url = new URL("https://www.google.com");
           
              URLConnection urlConn = url.openConnection();
           urlConn.setDoInput(true);
           urlConn.setDoOutput(true);
           OutputStream out=urlConn.getOutputStream();
           //out.write(user_name.getBytes());

            Element generator = pairing.getG1().newRandomElement();
            PairKeys pairkeys=EXschnorsig.keygen(pairing, generator); //keygen

           System.out.println("bcd.saltel@gmail.com");
           byte[] generatorBase64 = Base64.getEncoder().encode(generator.toBytes());
           byte[] pubKeyBase64 = Base64.getEncoder().encode(pairkeys.getPubkey().toBytes());
           out.write("bcd.saltel@gmail.com,".getBytes());
           out.write(generatorBase64);
           out.write(",".getBytes());
              out.write(pubKeyBase64);
           
            
           InputStream  dis = urlConn.getInputStream();
           byte[] b=new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
           dis.read(b);

           String msg = new String(b);

           System.out.println("message reçu : " + msg);
           String[] msgParts = msg.split(",");

           Element u = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(msgParts[0]));
           Element v = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(msgParts[1]));
           byte[] AESciphertext = Base64.getDecoder().decode(msgParts[2]);

           ElgamalCipher c = new ElgamalCipher(u,v,AESciphertext);

        //    System.out.println("GENERATOR ORIGINAL = " + generator.toString());
        //    System.out.println("U:" + c.getU().toString() + " V:" + c.getV().toString() + " Cipher: " + new String(AESciphertext));
        //    System.out.println("AESCYPHER BASE64 = " + msgParts[3]);

            // System.out.println("DECRYPTED MESSAGE = " + EXschnorsig.elGamaldec(pairing, generator, c, pairkeys.getSecretkey()));
            
           
           

           KeyPair ibeKeys = new KeyPair("bcd.saltel@gmail.com", pairing.getG1().newElementFromBytes(EXschnorsig.elGamaldec(pairing, generator, c, pairkeys.getSecretkey()).getBytes()) );

            System.out.println("IBEKEYS = " + ibeKeys.getPk() + " " + ibeKeys.getSk());
           


           
          
       
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
