/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cryptotpmail.ibe;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author imino
 */
public class SettingParameters {
    
    
   private Element p; //generateur
    
   private Element p_pub; // clef publique du système
    
    private Element msk; // clef du maitre

    public SettingParameters(Element p, Element p_pub, Element msk) {
        this.p = p;
        this.p_pub = p_pub;
        this.msk=msk;
    }

    public Element getP_pub() {
        return p_pub;
    }

    public Element getP() {
        return p;
    }

    public Element getMsk() {
        return msk;
    }
   
    
}
