package org.example.accessbasededonnees.security;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis.active-token")
@Setter
public class ActiveTokenProperties implements TokenRedisProperties{

    private String host;
    private int port;
    private int database;
    private String username;
    private String password;


    @Override
    public int getDatabase() {
        return database ;
    }

    @Override
    public int getPort() {
        return port ;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
