package org.example.accessbasededonnees.service;
import org.example.accessbasededonnees.model.Profiles;
import org.example.accessbasededonnees.security.JwtProvider;
import org.example.accessbasededonnees.security.JwtValidation;
import org.example.accessbasededonnees.util.ExtractToken;
import org.example.accessbasededonnees.util.ExtractTokenInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenGeneratorService {

    private final JwtValidation jwtValidation;
    private final ExtractTokenInformation extractTokenInformation ;
    private final JwtProvider jwtProvider;


    @Autowired
    public TokenGeneratorService(
            JwtValidation jwtValidation ,
            ExtractTokenInformation  extractTokenInformation ,
            JwtProvider jwtProvider) {
        this.jwtValidation = jwtValidation;
        this.extractTokenInformation = extractTokenInformation;
        this.jwtProvider = jwtProvider;
    }

    public Map<String, String> generateAccessToken(String theRefreshToken) {
        Map<String, String> theAccessToken = new HashMap<>();
        if ("refresh".equals(extractTokenInformation.getTokenType(theRefreshToken)) && jwtValidation.isValidToken(theRefreshToken)) {
            List<Profiles> theProfile = extractTokenInformation.getProfileFromToken(theRefreshToken);
            String theToken =  jwtProvider.createAccessToken(extractTokenInformation.getEmail(theRefreshToken), theProfile ,"access_token");
            theAccessToken.put("access_token", theToken);
            return theAccessToken ;
        } else {
            throw new NoSuchElementException("You must use a refresh token");
        }


    }
}