package org.example.accessbasededonnees.util;

import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;


public class ExtractToken {

    /**
     * Extracts the raw token from a Bearer Authorization header.
     *
     * @param bearerPlusToken the Authorization header value (e.g. "Bearer eyJhbGci...")
     * @return the extracted token string
     * @throws NoSuchElementException if the header is null or does not start with "Bearer "
     */
    public String getToken(String bearerPlusToken) {
        StringBuilder theDesiredToken  = new StringBuilder();
        if ((bearerPlusToken == null || !bearerPlusToken.startsWith("Bearer "))) {
            throw new NoSuchElementException("Missing the bearer token");
        } else if (bearerPlusToken.startsWith("Bearer ")) {
            String theRefreshToken = bearerPlusToken.substring(7);
            theDesiredToken.append(theRefreshToken) ;
            return theDesiredToken.toString();
        } else{
            throw new NoSuchElementException("Missing the bearer token");
        }

    }
}
