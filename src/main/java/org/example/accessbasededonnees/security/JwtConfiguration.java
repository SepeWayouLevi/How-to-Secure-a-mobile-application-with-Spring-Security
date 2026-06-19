package org.example.accessbasededonnees.security;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix ="security.jwt.token")
public class JwtConfiguration {

    private String secretKey;
    private String previousSecretKey;
    private long expiration ;
    private long  refreshExpiration ;
    private boolean rotationEnabled;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPreviousSecretKey() {
        return previousSecretKey;
    }

    public void setPreviousSecretKey(String previousSecretKey) {
        this.previousSecretKey = previousSecretKey;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }
    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    public boolean isRotationEnabled() {
        return rotationEnabled;
    }

    public void setRotationEnabled(boolean rotationEnabled) {
        this.rotationEnabled = rotationEnabled;
    }

}
