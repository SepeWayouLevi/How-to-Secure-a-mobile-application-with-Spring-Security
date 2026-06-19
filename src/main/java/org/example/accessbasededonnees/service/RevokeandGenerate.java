package org.example.accessbasededonnees.service;

import org.example.accessbasededonnees.security.JwtValidation;
import org.example.accessbasededonnees.util.ExtractToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RevokeandGenerate {
    private final TokenGeneratorService tokenGeneratorService;
    private final TokenRevocationService tokenRevocationService ;


    @Autowired
    public RevokeandGenerate(TokenGeneratorService tokenGeneratorService, TokenRevocationService tokenRevocationService , JwtValidation jwtValidation) {
        this.tokenGeneratorService = tokenGeneratorService;
        this.tokenRevocationService = tokenRevocationService;
    }


    public Map<String, String> revokeAndGenerateToken(String bearerToken){

        ExtractToken extractToken = new ExtractToken() ;
        String theRefreshToken = extractToken.getToken(bearerToken);
        tokenRevocationService.revokeCurrentToken(theRefreshToken);

        return  tokenGeneratorService.generateAccessToken(theRefreshToken);

    }
}
