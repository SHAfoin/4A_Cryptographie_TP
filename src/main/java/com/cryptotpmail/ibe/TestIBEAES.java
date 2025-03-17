package com.cryptotpmail.ibe;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author imino
 */
public class TestIBEAES {

    /**
     * @param args the command line arguments
     */
   
    public static void IBEalltypeoffilesEncryptiondecryptiondemo(Pairing pairing, SettingParameters sp, KeyPair keys, String filepath) {

        try {
            FileInputStream in = new FileInputStream(filepath); // ouverture d'un stream de lecture sur le fichier

            byte[] filebytes = new byte[in.available()]; // réservation d'un tableau de byte en fontion du nombre de bytes contenus dans  le fichier

            System.out.println("taille de fichier en byte:" + filebytes.length);

            in.read(filebytes); // lecture du fichier

            System.out.println("Encryption ....");
   
            IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, sp.getP(), sp.getP_pub(), filebytes, keys.getPk()); // chiffrement BasicID-IBE/AES
            
            System.out.println("---------------------");
   
            System.out.println("Decryption ....");
   
            byte[] resulting_bytes = IBEBasicIdent.IBEdecryption(pairing, sp.getP(), sp.getP_pub(), keys.getSk(), ibecipher); //déchiffrment Basic-ID IBE/AES

            File f = new File("decryptionresult" + filepath.substring(filepath.lastIndexOf("."))); // création d'un fichier pour l'enregistrement du résultat du déchiffrement

            f.createNewFile();

            FileOutputStream fout = new FileOutputStream(f);

            fout.write(filebytes); // ecriture du résultat de déchiffrement dans le fichier 

            System.out.println("to access the resulting file check the following path: " + f.getAbsolutePath());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        // TODO code application logic here

        Pairing pairing = PairingFactory.getPairing("Curve\\a.properties"); // chargement des paramètres de la courbe elliptique 
                                                                            // la configuration A offre un pairing symmetrique ce qui correspond à l'implementation du schema basicID
                                                                            // qui est basé sur l'utilisation du pairing symmetrique
        System.out.println("Setup ....");
     
        SettingParameters sp = IBEBasicIdent.setup(pairing); // génération des paramètres du système (ie: generateur, clef publique du système et clef du maitre)

        System.out.println("Paremètre du système :");
        
        System.out.println("generator:" + sp.getP());

        System.out.println("P_pub:" + sp.getP_pub());

        System.out.println("MSK:" + sp.getMsk());

        String id = "youcef.imine@gmail.com"; // id de test 

        System.out.println("-----------------------------");
        
        try {
            System.out.println("Key generation .....");
     
            KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), id); // genération d'une paire de clefs correspondante à id

            System.out.println("PK:" + keys.getPk());

            System.out.println("SK:" + keys.getSk());
        
            System.out.println("-----------------------------");
   
            IBEalltypeoffilesEncryptiondecryptiondemo(pairing, sp, keys, "C:\\Users\\imino\\OneDrive\\Bureau\\Sujet de thèse.pdf"); // démo de chiffrement/déchiffrement
        
            System.out.println("Fin ....");
   
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
