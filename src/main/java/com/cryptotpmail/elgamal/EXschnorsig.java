/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.elgamal;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

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

            byte[] plainmessage = AESCrypto.decrypt(c.getAESciphertext(), plain.toBytes());

            return new String(plainmessage);
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
}