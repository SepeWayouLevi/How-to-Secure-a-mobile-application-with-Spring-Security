package org.example.accessbasededonnees.util;

import jakarta.servlet.http.HttpServletRequest;

public class SanitizeHeader {

    public void sanitizeHeader( String email , HttpServletRequest theRequest) {
        if(!email.equals(theRequest.getUserPrincipal().getName())){
            throw new IllegalArgumentException("It's not authorized to get the data of others users by changing the header value");
        }
    }
}
