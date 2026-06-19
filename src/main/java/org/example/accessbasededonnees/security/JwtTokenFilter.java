package org.example.accessbasededonnees.security;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private AppUserDetailsService appUserDetailsService;

    public JwtTokenFilter(AppUserDetailsService appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headerValue = request.getHeader("Authorization");
        getBearerToken(headerValue).ifPresent(token -> {
            appUserDetailsService.loadUserByJwtTokenAndDatabase(token).ifPresent(userDetails -> {
                PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken =
                        new PreAuthenticatedAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                preAuthenticatedAuthenticationToken.setDetails(new UserAuthenticationInformation(request));
                SecurityContextHolder.getContext().setAuthentication(preAuthenticatedAuthenticationToken);
            });
        });
        filterChain.doFilter(request, response);
    }

    private Optional<String> getBearerToken(String headerVal) {
        if (headerVal != null && headerVal.startsWith(BEARER_PREFIX)) {
            return Optional.of(headerVal.substring(BEARER_PREFIX.length()).trim());
        }
        return Optional.empty();
    }


}