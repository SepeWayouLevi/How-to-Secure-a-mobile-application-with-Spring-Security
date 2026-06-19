package org.example.accessbasededonnees.util;

import org.example.accessbasededonnees.model.Profiles;
import org.example.accessbasededonnees.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class ExtractTokenInformation {
    private final JwtProvider jwtProvider;

    @Autowired
    public ExtractTokenInformation(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }

    public String getEmail(String token) {
        return jwtProvider.parseTokenClaims(token).getSubject();
    }

    public String getTokenId(String token) {
        return jwtProvider.parseTokenClaims(token).getId();
    }

    public Object getIssueDate(String token){
        return jwtProvider.parseTokenClaims(token).getIssuedAt();
    }

    public Object getTokenType(String token){
        return jwtProvider.parseTokenClaims(token).get("type") ;
    }

    public List<Profiles> getProfileFromToken(String token) {
        Object profile = jwtProvider.parseTokenClaims(token).get("roles");
        List<Map<String, String>> rolesMap = (List<Map<String, String>>) profile;
        // Conversion en objets Profiles (seul le profileName est nécessaire pour l'autorité)
        List<Profiles> profiles = rolesMap.stream()
                .map(map -> {
                    Profiles p = new Profiles();
                    p.setProfileName(map.get("authority")); // "authority" contient le nom du rôle
                    return p;
                })
                .collect(Collectors.toList());
        return profiles;
    }


}
