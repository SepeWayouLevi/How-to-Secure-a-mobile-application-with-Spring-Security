package org.example.accessbasededonnees.security;
import org.example.accessbasededonnees.model.AppUser;
import org.example.accessbasededonnees.repository.AppUserRepository;
import org.example.accessbasededonnees.util.ExtractTokenInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;
import static org.springframework.security.core.userdetails.User.withUsername;

@Component
public class AppUserDetailsService implements UserDetailsService{

    private final AppUserRepository appUserRepository;

    private final ExtractTokenInformation extractTokenInformation;

    private final JwtValidation jwtValidation;

    @Autowired
  public AppUserDetailsService(AppUserRepository appUserRepository, ExtractTokenInformation extractTokenInformation , JwtValidation jwtValidation) {
        this.appUserRepository = appUserRepository;
        this.extractTokenInformation = extractTokenInformation;
        this.jwtValidation = jwtValidation;

  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
      AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(()->
              new UsernameNotFoundException(String.format("User with email %s does not exist", email)));

      return withUsername(appUser.getEmail())
              .password(appUser.getPassword())
              .authorities(appUser.getProfiles())
              .accountExpired(appUser.isAccountExpired())
              .accountLocked(appUser.isAccountLocked())
              .credentialsExpired(false)
              .disabled(appUser.isAccountDisable())
              .build();
  }

    /**
     * Variante : extrait l'email du JWT puis recharge l'utilisateur en base.
     * Utile si l'on souhaite synchroniser l'état du compte (verrouillage, suppression, rôles à jour)
     * au moment de l'auth via token.
     *
     * @param jwtToken token JWT
     * @return Optional<UserDetails> : présent si token valide et utilisateur existant, vide sinon.
     */
    public Optional<UserDetails> loadUserByJwtTokenAndDatabase(String jwtToken) {
        if (jwtValidation.isValidToken(jwtToken)) {
            // Ici, loadUserByUsername peut lever UsernameNotFoundException si l'utilisateur n'existe plus.
            // La méthode encapsule dans un Optional.of(...) puisque le token est valide.
            return Optional.of(loadUserByUsername(extractTokenInformation.getEmail(jwtToken)));
        } else {
            return Optional.empty();
        }
    }


}
