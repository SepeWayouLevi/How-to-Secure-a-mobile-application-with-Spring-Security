package org.example.accessbasededonnees.controller;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.accessbasededonnees.Login.LoginInformations;
import org.example.accessbasededonnees.service.AppUserService;
import org.example.accessbasededonnees.service.RevokeandGenerate;
import org.example.accessbasededonnees.service.TokenGeneratorService;
import org.example.accessbasededonnees.service.TokenRevocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final TokenRevocationService tokenRevocationService ;
    private final RevokeandGenerate revokeandGenerate ;
    private final AppUserService appUserService;

    public AuthenticationController(TokenRevocationService tokenRevocationService, TokenGeneratorService tokenGeneratorService, RevokeandGenerate revokeandGenerate, AppUserService appUserService) {
        this.tokenRevocationService = tokenRevocationService;
        this.revokeandGenerate = revokeandGenerate;
        this.appUserService = appUserService;
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@RequestBody @Valid LoginInformations loginInformations) {
        return appUserService.signin(loginInformations.getEmail(), loginInformations.getPassword());
    }


    @PostMapping("/users/disconnect")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN'," +
            " 'ROLE_PRICING'," +
           " 'ROLE_GENERAL', " +
            "'ROLE_LAW'," +
            " 'ROLE_PURCHASE', " +
            "'ROLE_STOCK')")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String bearerPlusToken ,
            @RequestHeader("Refresh-Token") String refreshToken) {
        tokenRevocationService.revokeToken(bearerPlusToken , refreshToken);
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @GetMapping("/accessToken")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN'," +
            " 'ROLE_PRICING'," +
            " 'ROLE_GENERAL', " +
            "'ROLE_LAW'," +
            " 'ROLE_PURCHASE', " +
            "'ROLE_STOCK')")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public Map<String, String> getAccessToken(@RequestHeader("Authorization") String  bearerPlusToken)
    {
        return revokeandGenerate.revokeAndGenerateToken(bearerPlusToken);

    }






}
