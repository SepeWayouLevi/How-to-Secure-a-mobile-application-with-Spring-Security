package org.example.accessbasededonnees.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class UserAuthenticationInformation extends WebAuthenticationDetails {
    private final String userIpAddress;
    private final String userRemoteHost;
    private final String userQueryMethod;
    private final int userPort ;

    public UserAuthenticationInformation(HttpServletRequest request) {
        super(request);
        this.userIpAddress = request.getRemoteAddr();
        this.userRemoteHost = request.getRemoteHost();
        this.userQueryMethod = request.getMethod();
        this.userPort =  request.getRemotePort();
    }
}
