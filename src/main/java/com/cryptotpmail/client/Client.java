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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imino
 */
public class Client {
    
    public static void main(String[] args) {
        
        
        try {
                  URL url = new URL("http://localhost:8080/service");
            // URL url = new URL("https://www.google.com");
           
              URLConnection urlConn = url.openConnection();
           urlConn.setDoInput(true);
           urlConn.setDoOutput(true);
           OutputStream out=urlConn.getOutputStream();
           //out.write(user_name.getBytes());
           System.out.println("salut....");
           out.write("salut...".getBytes());
           
           InputStream  dis = urlConn.getInputStream();
           byte[] b=new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
           dis.read(b);
           
           String response=new String(b);
            System.out.println("message reçu du serveur:"+response);
       
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
