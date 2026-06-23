package org.example.accessbasededonnees.service;
import java.util.*;
import org.example.accessbasededonnees.model.AppUser;
import org.example.accessbasededonnees.model.Profiles;
import org.example.accessbasededonnees.repository.AppUserRepository;
import org.example.accessbasededonnees.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AppUserService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;

    private final AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;

    private JwtProvider jwtProvider;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder ,
                          JwtProvider jwtProvider
    ) {
         this.appUserRepository = appUserRepository;
         this.authenticationManager = authenticationManager;
         this.passwordEncoder = passwordEncoder;
         this.jwtProvider = jwtProvider;
     }

    public Map<String, String> signin(String email, String password) throws AuthenticationException {

        Optional<String> AccessToken = Optional.empty();
        Optional<String> refreshToken = Optional.empty();
        Map<String, String>  myDictionnary  = new HashMap<>();

        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        Map<String,Boolean> isUserAccountLockedOrDisableorExpired  =  appUserRepository.checkIfUserAccountIsDisabledOrExpiredOrLocked(email) ;
        Boolean anyIssueWithAccount  =  false;

        for(Boolean element : isUserAccountLockedOrDisableorExpired.values()){
            if(element.equals(true)){
                anyIssueWithAccount =  true;
                break ;
            } else  {
                continue;
            }
        }

        if (appUser.isPresent() && !anyIssueWithAccount.booleanValue()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
                List<Profiles> profiles = List.of(appUser.get().getProfiles());
                AccessToken = Optional.of(jwtProvider.createAccessToken(email, profiles , "access_token"));
                refreshToken = Optional.of(jwtProvider.createRefreshToken(email , profiles , "refresh_token"));
                myDictionnary.put("access_token", AccessToken.get());
                myDictionnary.put("refresh_token", refreshToken.get());
            } catch (AuthenticationException e){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentication failed");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account forbidden");
        }
        return myDictionnary;
    }


}
