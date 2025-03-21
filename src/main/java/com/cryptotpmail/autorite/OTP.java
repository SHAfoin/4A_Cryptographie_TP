package com.cryptotpmail.autorite;

import java.time.Instant;

public class OTP {
    private String otp;
    private Instant timestamp;

    public OTP(String otp, Instant email) {
        this.otp = otp;
        this.timestamp = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant email) {
        this.timestamp = email;
    }
}
