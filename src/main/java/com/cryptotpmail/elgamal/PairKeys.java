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
public class PairKeys {
    
    private Element pubkey;
    private Element secretkey;

    public PairKeys(Element pubkey, Element secretkey) {
        this.pubkey = pubkey;
        this.secretkey = secretkey;
    }

    public Element getPubkey() {
        return pubkey;
    }

    public Element getSecretkey() {
        return secretkey;
    }
    
}
