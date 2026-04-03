package com.stockpro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stockpro.jwt")
public class JwtProperties {

    /**
     * HMAC secret — must be long/ random in production (case study: JWT for internal SSO).
     */
    private String secret = "dev-secret-change-me";

    /** Token TTL in milliseconds (default 8h aligns with NFR in case study). */
    private long expirationMs = 28_800_000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
