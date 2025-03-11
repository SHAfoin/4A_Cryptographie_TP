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
import java.security.NoSuchAlgorithmException;

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

        // 1 : le serveur mail envoie son mail (clé publique) à l'autorité
        // 2 : serveur mail & l'autorité génèrent les pairing
        // 3 : l'autorité définit le générateur et l'envoie au serveur mail
        // 4 : mail & autorité génèrent leurs clés
        // 5 : l'autorité génère la clé secrète et le transmet au serveur mail via
        // elgamal
        // 6 : le serveur mail à sa clé privée
       
        Pairing pairing = PairingFactory.getPairing("curves/curveParamsd159"); //chargement des paramètres de la courbe elliptique  
                                                                        //(replacer "curveParamsd159" par un chemin vers le fichier de configuration de la courbe)
        Element generator=pairing.getG1().newRandomElement(); //génerateur
   
        PairKeys pairkeys=keygen(pairing, generator); //keygen
     
        //test chiffrement, déchiffrement, signature et vérification
        fileEncryption_decryptiondemo("C:\\Users\\imino\\OneDrive\\Bureau\\filetoencrypt.txt", pairing, generator, pairkeys);
        
    }
}
