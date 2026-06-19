package org.example.accessbasededonnees.service;
import org.example.accessbasededonnees.security.JwtValidation;
import org.example.accessbasededonnees.util.ExtractToken;
import org.example.accessbasededonnees.util.ExtractTokenInformation;
import org.example.accessbasededonnees.util.RedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class TokenRevocationService {
    private final JwtValidation jwtValidation;
    private final ExtractTokenInformation extractTokenInformation;
    private final RedisConnection redisConnection;


    @Autowired
    public TokenRevocationService(
                                  JwtValidation jwtValidation,
                                  ExtractTokenInformation extractTokenInformation,
                                  RedisConnection redisConnection
    ) {
        this.jwtValidation = jwtValidation;
        this.extractTokenInformation = extractTokenInformation;
        this.redisConnection = redisConnection;
    }


    public void revokeTokenById(String token) {
        redisConnection.getExpiredTokensRedis()
                .opsForValue()
                .set(extractTokenInformation.getTokenId(token), "revoked");
        redisConnection.getActiveTokensRedis().delete(extractTokenInformation.getTokenId(token));
    }

    public void revokeToken(String bearerPlusToken, String refreshToken) {
        ExtractToken extractToken = new ExtractToken();
        String theToken = extractToken.getToken(bearerPlusToken);
        if (extractTokenInformation.getEmail(theToken).equals(extractTokenInformation.getEmail(refreshToken)) &&
                jwtValidation.isValidToken(theToken) && jwtValidation.isValidToken(refreshToken)) {
                revokeTokenById(theToken);
                revokeTokenById(refreshToken);
        }else{
            throw new IllegalArgumentException("Bearer token is missing");
        }

    }

    public void revokeCurrentToken(String theRefreshToken) {
        if(jwtValidation.isValidToken(theRefreshToken) && "refresh".equals(extractTokenInformation.getTokenType(theRefreshToken))){
            String theEmailFromRefreshToken  =  extractTokenInformation.getEmail(theRefreshToken);
            //Scan all keys in getActiveTokensRedis()
            ScanOptions options = ScanOptions.scanOptions().match("*").build();

            //create a cursor to iterate over the  keys
            Cursor<String> cursor = redisConnection.getActiveTokensRedis().scan(options);

            List<String> activeTokenIds= cursor.stream().filter(element->theEmailFromRefreshToken.equals(redisConnection.getActiveTokensRedis().opsForHash().entries(element).get("email"))).toList();

            cursor.close();

            for(int i =0 ; i < activeTokenIds.size(); i++){
                if(redisConnection.getActiveTokensRedis().getExpire(activeTokenIds.get(i)) <= 60){
                    redisConnection.getExpiredTokensRedis().opsForValue().set(activeTokenIds.get(i), "revoked");
                    redisConnection.getActiveTokensRedis().delete(activeTokenIds.get(i));
                }
            }
        } else{
            throw new NoSuchElementException("You must use a refresh token");
        }

    }

}
