/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.autorite;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imino
 */
public class HttpServeur {
    
    
    
    public static void main(String[] args) {
     
        try {
             // InetSocketAddress s = new InetSocketAddress("localhost", 8080);
             System.out.println("my address:"+InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);
              
              
/*  135 */               HttpServer server = HttpServer.create(s, 1000);
/*  136 */               System.out.println(server.getAddress());
/*  137 */               server.createContext("/service", new HttpHandler()
/*      */                   {
/*      */                     public void handle(HttpExchange he) throws IOException {
/*  140 */                        byte[] bytes1 = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
/*  298 */                  he.getRequestBody().read(bytes1);
/*  299 */                  String msg = new String(bytes1);
/*      */     
/*  301 */              System.out.println("message reçu " + msg);
/*      */     
/*  303 */     byte[] bytes = "bonjour client ..".getBytes();
/*      */     
/*  305 */     he.sendResponseHeaders(200, bytes.length);
/*      */     
/*  307 */     OutputStream os = he.getResponseBody();
/*      */     
/*  309 */     os.write(bytes);
/*  310 */     System.out.println("sending response done....");
/*  311 */     os.close();
/*      */                     }
/*      */                   });
/*      */ 
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
