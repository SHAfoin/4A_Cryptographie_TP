/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.autorite;

import com.cryptotpmail.elgamal.EXschnorsig;
import com.cryptotpmail.elgamal.ElgamalCipher;
import com.cryptotpmail.ibe.IBEBasicIdent;
import com.cryptotpmail.ibe.KeyPair;
import com.cryptotpmail.ibe.SettingParameters;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.HashMap; // import the HashMap class

/**
 *
 * @author imino
 */
public class HttpServeur {

    public static void main(String[] args) {

        // Sauvegarde des utilisateurs
        HashMap<String, String> users = new HashMap<String, String>();

        try {

            Encoder encoder = Base64.getEncoder();
            Decoder decoder = Base64.getDecoder();

            // PAIRING & SETUP DU IBE
            Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");
            SettingParameters param = IBEBasicIdent.setup(pairingIBE);

            // PAIRING DU ELGAMAL
            Pairing pairingElGamal = PairingFactory.getPairing("curves/d159.properties");

            // MISE EN PLACE DU SERVEUR
            System.out.println("my address:" + InetAddress.getLocalHost());
            // InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(),
            // 8080);
            InetSocketAddress s = new InetSocketAddress("localhost", 8080);
            HttpServer server = HttpServer.create(s, 0);
            System.out.println(server.getAddress());
            server.createContext("/service", new HttpHandler() {

                // REQUÊTE SUR /service SUR LE SERVEUR
                public void handle(HttpExchange he) throws IOException {
                    try {

                        // RECUPERATION DE id, password, generatorElGamal, clientPubKeyElGamal DU CLIENT
                        byte[] bytes = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                        he.getRequestBody().read(bytes);
                        String content = new String(bytes);

                        String id = content.split(",")[0];
                        String password = content.split(",")[1];
                        Element generatorElGamal = pairingElGamal.getG1()
                                .newElementFromBytes(decoder.decode(content.split(",")[2]));
                        Element clientPubKey = pairingElGamal.getG1()
                                .newElementFromBytes(decoder.decode(content.split(",")[3]));

                        // VERIFICATION QUE L'UTILISATEUR EST BIEN ENREGISTRE
                        if (users.containsKey(id)) {
                            // VERIFICATION DU MOT DE PASSE
                            if (!users.get(id).equals(password)) { // SI LE MOT DE PASSE EST FAUX
                                byte[] authentificationKO = "false".getBytes();
                                he.sendResponseHeaders(200, authentificationKO.length);
                                OutputStream os = he.getResponseBody();
                                os.write(authentificationKO);
                                os.close();
                                return;
                            }

                        } else { // SINON L'ENREGISTRER
                            users.put(id, password);
                        }

                        // GENERATION DE LA CLE IBE DU CLIENT
                        KeyPair Kp = IBEBasicIdent.keygen(pairingIBE, param.getMsk(), id);
                        byte[] skBytes = Kp.getSk().toBytes();
                        byte[] skBytesBase64 = encoder.encode(skBytes);

                        // CHIFFREMENT DE LA CLE IBE DU CLIENT AVEC ELGAMAL
                        ElgamalCipher cypherElgamal = EXschnorsig.elGamalencr(pairingElGamal, generatorElGamal,
                                skBytesBase64, clientPubKey);

                        // ENVOI DE LA REPONSE AU CLIENT
                        byte[] authentificationOK = "true".getBytes();
                        byte[] ibePBase64 = encoder.encode(param.getP().toBytes());
                        byte[] ibePpubBase64 = encoder.encode(param.getP_pub().toBytes());
                        byte[] uBase64 = Base64.getEncoder().encode(cypherElgamal.getU().toBytes());
                        byte[] vBase64 = Base64.getEncoder().encode(cypherElgamal.getV().toBytes());
                        byte[] cipherBase64 = Base64.getEncoder().encode(cypherElgamal.getAESciphertext());

                        he.sendResponseHeaders(200, authentificationOK.length + ibePBase64.length + ibePpubBase64.length
                                + uBase64.length + vBase64.length + cipherBase64.length + 5);
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

                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
