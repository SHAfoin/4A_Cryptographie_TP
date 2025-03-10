/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.elgamal;

import it.unisa.dia.gas.jpbc.Element;


/**
 *
 * @author imino
 */
public class ElgamalCipher {
   private Element u;
    private Element v;
    private byte[] AESciphertext;

    public ElgamalCipher(Element u, Element v, byte[] AESciphertext) {
        this.u = u;
        this.v = v;
        this.AESciphertext=AESciphertext;
    }

    public Element getU() {
        return u;
    }

    public Element getV() {
        return v;
    }

    public byte[] getAESciphertext() {
        return AESciphertext;
    }
    
    
}
