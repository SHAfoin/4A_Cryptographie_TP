/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.elgamal;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author imino
 */
public class AESCrypto {
    
    

    public static byte[] encrypt(byte[] m, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        //méthode de chiffrement AES d'un message m en utilisant la clef key
       
        Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest digest=MessageDigest.getInstance("SHA1");
        digest.update(key);
        byte[] AESkey=Arrays.copyOf(digest.digest(),16);
        SecretKeySpec keyspec=new SecretKeySpec(AESkey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keyspec);
        byte[] ciphertext=Base64.getEncoder().encode(cipher.doFinal(m));
        
        return ciphertext;
    }
    
    
    public static String decrypt(byte[] ciphertext, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
         //méthode de déchiffrement AES d'un ciphertext en utilisant la clef key
     
        Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest digest=MessageDigest.getInstance("SHA1");
        digest.update(key);
        byte[] AESkey=Arrays.copyOf(digest.digest(),16);
        SecretKeySpec keyspec=new SecretKeySpec(AESkey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keyspec);
        String plaintext=new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
        
        return plaintext;
  
    }
    
      public static byte[] encryptV2(byte[] m, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        
       Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        MessageDigest digest=MessageDigest.getInstance("SHA1");
        digest.update(key);
        byte[] AESkey=Arrays.copyOf(digest.digest(),16);
        SecretKeySpec keyspec=new SecretKeySpec(AESkey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keyspec);
        byte[] ciphertext=Base64.getEncoder().encode(cipher.doFinal(m));
        
        return ciphertext;
    }
}
