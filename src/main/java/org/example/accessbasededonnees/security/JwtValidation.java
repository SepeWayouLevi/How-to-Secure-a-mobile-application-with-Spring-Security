package org.example.accessbasededonnees.security;
import io.jsonwebtoken.*;
import org.example.accessbasededonnees.util.ExtractTokenInformation;
import org.example.accessbasededonnees.util.RedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;


@Component
public class JwtValidation {
    private final RedisConnection redisConnection ;
    private final JwtConfiguration jwtConfiguration;
    private ExtractTokenInformation extractTokenInformation;
    private JwtProvider jwtProvider;


    @Autowired
    public JwtValidation(RedisConnection redisConnection ,
                         JwtConfiguration jwtConfiguration ,
                         ExtractTokenInformation extractTokenInformation,
                         JwtProvider jwtProvider
                                  ){
        this.redisConnection = redisConnection;
        this.jwtConfiguration = jwtConfiguration;
        this.extractTokenInformation=extractTokenInformation;
        this.jwtProvider = jwtProvider;
    }


    public  boolean isValidToken(String token) {
        boolean isValid  = false ;
        if((redisContainsAccessToken(token) || redisContainsRefreshToken(token))
                && (!isTokenRevoked(token) && isValidIssueDate(token)) ){
            try {
                jwtProvider.parseTokenWithKey(token, jwtProvider.getCurrentSigningKey());
                isValid =  true ;
                return isValid;
            }
            catch (JwtException e) {
                // Si échec et rotation activée, essai avec l'ancienne clé
                if (jwtConfiguration.isRotationEnabled()) {
                    Key previousKey = jwtProvider.getPreviousSigningKey();
                    if (previousKey != null) {
                        try {
                            jwtProvider.parseTokenWithKey(token, previousKey);
                            isValid =  true ;
                            return isValid;
                        } catch (JwtException e2) {
                            // Les deux clés ont échoué
                            isValid = false;
                            return isValid;
                        }
                    }
                }
                return isValid;
            }
        } else {
            isValid = false;
            return isValid;
        }
    }

    public boolean redisContainsAccessToken(String token) {
        return redisConnection.getActiveTokensRedis().hasKey(extractTokenInformation.getTokenId(token));
    }

    public boolean redisContainsRefreshToken(String token) {
        return redisConnection.getActiveTokensRedis().hasKey(extractTokenInformation.getTokenId(token));
    }

    private boolean isTokenRevoked(String token) {
        String key =  extractTokenInformation.getTokenId(token);
        return redisConnection.getExpiredTokensRedis().hasKey(key);
    }

    private boolean isValidIssueDate(String token){
        String key = extractTokenInformation.getTokenId(token);
        long tokenIssueDateTime = Long.parseLong(redisConnection.getActiveTokensRedis().opsForHash().entries(key).get("tokenIssueDate").toString());
        Date dateFromRedis = new Date(tokenIssueDateTime);

        long notBeforeDate = Long.parseLong(redisConnection.getActiveTokensRedis().opsForHash().entries(key).get("notBefore").toString());
        Date notBeforeFromRedis = new Date(notBeforeDate);

        return dateFromRedis.equals(extractTokenInformation.getIssueDate(token)) && notBeforeFromRedis.equals(extractTokenInformation.getIssueDate(token));
    }







}
