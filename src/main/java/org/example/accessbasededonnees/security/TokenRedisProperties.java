package org.example.accessbasededonnees.security;

public interface TokenRedisProperties {
    String getHost();
    int getPort();
    int getDatabase();
    String getUsername();
    String getPassword();
}
