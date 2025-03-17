package com.cryptotpmail.client;

import it.unisa.dia.gas.jpbc.Element;

public class ClientIBEParams {

    private Element p; // generateur
    private Element p_pub; // clef publique du système
    private Element sk; // secret key

    public ClientIBEParams(Element p, Element p_pub, Element sk) {
        this.p = p;
        this.p_pub = p_pub;
        this.sk = sk;
    }

    public Element getP_pub() {
        return p_pub;
    }

    public Element getP() {
        return p;
    }

    public Element getSk() {
        return sk;
    }

}
