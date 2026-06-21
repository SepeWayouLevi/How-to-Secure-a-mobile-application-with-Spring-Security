package org.example.accessbasededonnees.security;
import io.jsonwebtoken.*;
import org.example.accessbasededonnees.model.Profiles;
import org.example.accessbasededonnees.util.RedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
public class JwtProvider {
    private static final String THE_AUDIENCE =  "com.example.forms";
    private static final String ROLES_KEY = "roles";
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String ISSUER = "org.example.accessbasededonnees";

    @Value("${security.jwt.token.refresh-additional-time-validity}")
    private long refreshAdditionalTimeValidity;  // 7 days

    @Value("${security.jwt.token.access-additional-time-validity}")
    private long accessAdditionalTimeValidity;  // 15 minutes

    @Value("${security.jwt.token.refresh-threshold}")
    private long rotationThreshold; // 1 day


    private static Date TIME_REQUIRED_TO_EXPIRE = new Date(System.currentTimeMillis() + 604800000); // now + 7 days
    private Key currentSigningKey;
    private Key previousSigningKey;
    private Key newKey ;
    private Date expirationDate ;
    private final JwtConfiguration jwtConfiguration;
    private final RedisConnection  redisConnection;

    @Autowired
    public JwtProvider(JwtConfiguration jwtConfiguration,
                       RedisConnection redisConnection
    ) {
        this.jwtConfiguration = jwtConfiguration;
        this.redisConnection = redisConnection;
        initializeKeys();
    }


    private void initializeKeys() {
        if(jwtConfiguration.getSecretKey() != null && jwtConfiguration.getPreviousSecretKey() != null){
            this.currentSigningKey = createSigningKey(jwtConfiguration.getSecretKey());
            this.previousSigningKey = createSigningKey(jwtConfiguration.getPreviousSecretKey());
        } else {
            throw new  NoSuchElementException("Signing keys cannot be null");
        }
    }

    private Key createSigningKey(String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }


    Key getCurrentSigningKey() {
        return currentSigningKey;
    }


    Key getPreviousSigningKey() {
        return previousSigningKey;
    }


    public String createAccessToken(String email, List<Profiles> profiles, String tokenType) {
        manageExpirationDate(tokenType);
        String theAccessToken  = createToken(email, profiles, "access");
        setTokenInformation(theAccessToken, email);
        return theAccessToken;
    }

    public String createRefreshToken(String email, List<Profiles> profiles , String tokenType){
        manageExpirationDate(tokenType);
        String theRefreshToken  = createToken(email, profiles, "refresh");
        setTokenInformation(theRefreshToken, email);
        return theRefreshToken;
    }


    private String createToken(String email, List<Profiles> profiles, String tokenType) {
        ClaimsBuilder claimsBuilder = Jwts.claims()
                .subject(email)
                .issuer(ISSUER)
                .id(UUID.randomUUID().toString()); // Unique ID for revocation
        claimsBuilder.add(ROLES_KEY, profiles.stream()//         // Adding role
                .map(profile -> new SimpleGrantedAuthority(profile.getAuthority()))
                .collect(Collectors.toList()));
        claimsBuilder.audience().add(THE_AUDIENCE);
        claimsBuilder.add(TOKEN_TYPE_KEY, tokenType); // add token type
        Claims claims = claimsBuilder.build();
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(this.expirationDate) // setting the correct expiration date
                .notBefore(now)
                .signWith(getCurrentSigningKey())
                .compact();
    }

    /**
     * Verify token signature with a key
     */
     public Jws<Claims> parseTokenWithKey(String token, Key key) {
        return Jwts.parser()
                .requireAudience(THE_AUDIENCE)
                .requireIssuer(ISSUER)
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);
    }


     public Claims parseTokenClaims(String token) {
        // Try with current key
        try {
            return parseTokenWithKey(token, getCurrentSigningKey()).getPayload();
        }
        catch (JwtException e) {
            // if rotation is enabled, try with the previous key
            if (jwtConfiguration.isRotationEnabled()) {
                Key previousKey = getPreviousSigningKey();
                if (previousKey != null) {
                    return parseTokenWithKey(token, previousKey).getPayload();
                }
            }
            throw e;
        }
    }


    public void setTokenInformation(String token , String email){
        Map<String, String> tokenInformation = new HashMap<>() ;
        Claims claimsOfToken = parseTokenClaims(token);
        String tokenId = claimsOfToken.getId();
        Date expirationOfTheToken = claimsOfToken.getExpiration();
        Date notBefore = claimsOfToken.getNotBefore();
        Date tokenIssueDate =  claimsOfToken.getIssuedAt();
        tokenInformation.put("idOfTheToken", tokenId);
        tokenInformation.put("notBefore", String.valueOf(notBefore.getTime()));
        tokenInformation.put("tokenIssueDate" , String.valueOf(tokenIssueDate.getTime())) ;
        tokenInformation.put("email", email);
        Instant now  = Instant.now();
        Instant tokenExpiration =  expirationOfTheToken.toInstant();
        Duration timeToLive  =  Duration.between(now, tokenExpiration);
        redisConnection.getActiveTokensRedis().opsForHash().putAll(tokenId, tokenInformation);
        redisConnection.getActiveTokensRedis().expire(tokenId, timeToLive.getSeconds(), TimeUnit.SECONDS);
    }


    /**
     * Rotate Keys
     */
     private void rotateKeys() {
         this.previousSigningKey = this.currentSigningKey;
         this.currentSigningKey = this.newKey;
    }

    private void manageExpirationDate(String tokenType){
         Date now = new Date();
         if("access_token".equals(tokenType)){
            this.expirationDate = new Date(now.getTime() + accessAdditionalTimeValidity); // now plus 15 minutes
         } else if("refresh_token".equals(tokenType)  && (TIME_REQUIRED_TO_EXPIRE.getTime() - now.getTime() > rotationThreshold)) {  // 1 day
            this.expirationDate = TIME_REQUIRED_TO_EXPIRE; // remaining time to expire
         } else if("refresh_token".equals(tokenType)  && (TIME_REQUIRED_TO_EXPIRE.getTime() - now.getTime() <= rotationThreshold)) {
            this.expirationDate = new Date(now.getTime() + refreshAdditionalTimeValidity); // now plus 7 days
        }
    }



    @Scheduled(initialDelayString  = "${security.jwt.token.refresh-additional-time-validity}")
    public void refreshAdditionalTimeValidity(){
        TIME_REQUIRED_TO_EXPIRE = new Date(TIME_REQUIRED_TO_EXPIRE.getTime() + refreshAdditionalTimeValidity);
    }

    private String generateSecureKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @Scheduled(initialDelayString = "${security.jwt.token.rotation-period}") // Each 6 days
    private void scheduledRotation(){
         if(jwtConfiguration.isRotationEnabled()){
             String newGeneratedKey = generateSecureKey();
             this.newKey = createSigningKey(newGeneratedKey);
             rotateKeys();
         }
    }



}