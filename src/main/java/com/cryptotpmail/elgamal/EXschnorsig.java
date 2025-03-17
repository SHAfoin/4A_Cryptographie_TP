/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.elgamal;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author imino
 */
public class EXschnorsig {

    public static PairKeys keygen(Pairing p, Element generator) {
        Element sk = p.getZr().newRandomElement();

        Element pk = generator.duplicate().mulZn(sk);

        return new PairKeys(pk, sk);
    }

    public static ElgamalCipher elGamalencr(Pairing p, Element generator, byte[] m, Element Pk)
            throws UnsupportedEncodingException {
        // méthode de chiffrement hybrid combinant El-gamal et AES
        try {
            Element r = p.getZr().newRandomElement();
            Element K = p.getG1().newRandomElement(); // clef symmetrique
            Element V = Pk.duplicate().mulZn(r);
            V.add(K);
            byte[] ciphertext = AESCrypto.encrypt(m, K.toBytes());
            Element U = generator.duplicate().mulZn(r);
            return new ElgamalCipher(U, V, ciphertext);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String elGamaldec(Pairing p, Element generator, ElgamalCipher c, Element Sk) {
        // méthode de déchiffrement hybrid combinant El-gamal et AES

        try {
            Element u_p = c.getU().duplicate().mulZn(Sk);
            System.out.println("V_p=" + u_p);

            Element plain = c.getV().duplicate().sub(u_p); // clef symmetrique retrouvée
            System.out.println("retrievd key=" + plain);

            String plainmessage = AESCrypto.decrypt(c.getAESciphertext(), plain.toBytes());

            return plainmessage;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void messageEncryption_decryptiondemo(String message, Pairing pairing, Element generator,
            PairKeys pairkeys) {

        try {
            System.out.println("bytelenght:" + message.getBytes().length);

            ElgamalCipher c = elGamalencr(pairing, generator, message.getBytes("UTF-8"), pairkeys.getPubkey());

            System.out.println("the message is: \n" + elGamaldec(pairing, generator, c, pairkeys.getSecretkey()));

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void fileEncryption_decryptiondemo(String filepath, Pairing pairing, Element generator,
            PairKeys pairkeys) {

        try {
            FileInputStream in = new FileInputStream(filepath);

            byte[] filebytes = new byte[in.available()];

            System.out.println("taille de fichier en byte:" + filebytes.length);

            in.read(filebytes);

            System.out.println("bytelenght:" + filepath.getBytes().length);

            String message = new String(filebytes);

            ElgamalCipher c = elGamalencr(pairing, generator, message.getBytes("UTF-8"), pairkeys.getPubkey());

            String retrived_message = elGamaldec(pairing, generator, c, pairkeys.getSecretkey());

            System.out.println("the decrypted message is: \n" + retrived_message);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EXschnorsig.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
  
        // Pairing pairing = PairingFactory.getPairing("curves/d159.properties"); //chargement des paramètres de la courbe elliptique  
        //                                                                 //(replacer "curveParamsd159" par un chemin vers le fichier de configuration de la courbe)
        // Element generator=pairing.getG1().newRandomElement(); //génerateur
   
        // PairKeys pairkeys=keygen(pairing, generator); //keygen
     
        // //test chiffrement, déchiffrement, signature et vérification
        // fileEncryption_decryptiondemo("D:\\INSA\\4A ICY\\Cryptographie Avancée\\TP\\cryptotpmail\\src\\main\\java\\com\\cryptotpmail\\elgamal\\filetoencrypt.txt", pairing, generator, pairkeys);
        
        byte[] key="azerty".getBytes();
        MessageDigest digest=MessageDigest.getInstance("SHA256");
        digest.update(key);
        byte[] hash = digest.digest(); // Calcul du hash

        // Convertir le hash en format hexadécimal pour affichage
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        System.out.println("Hash SHA-256 : " + hexString.toString());
    }
}