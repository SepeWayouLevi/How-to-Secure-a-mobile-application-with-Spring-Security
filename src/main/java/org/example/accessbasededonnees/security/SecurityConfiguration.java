package org.example.accessbasededonnees.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.security.SecureRandom;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration   {
    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    public SecurityConfiguration(AppUserDetailsService appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers(HttpMethod.POST , "/api/authentication/signin").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/forms/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/forms/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/forms/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/forms").authenticated()
                                .requestMatchers(HttpMethod.GET , "/api/authentication/accessToken").authenticated()
                                .requestMatchers(HttpMethod.POST , "/api/authentication/users/disconnect").authenticated()
                        .requestMatchers(HttpMethod.POST , "/api/authentication/revokeCurrentToken").authenticated()
                        .anyRequest().authenticated()
                )
                // 2) CSRF : désactivé car on est en authentification par token (pas de cookie de session)
                .csrf(csrf -> csrf.disable())

                // 3) Sessions : totalement stateless (le contexte est recréé à chaque requête via JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                // 4) Filtre JWT : s’exécute avant le filtre UsernamePasswordAuthenticationFilter
       .addFilterBefore(new JwtTokenFilter(appUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        // Construction de l’objet SecurityFilterChain
        return http.build();
    }
    /**
     * Expose un AuthenticationManager à partir de la configuration auto de Spring Security.
     * Utile si un service métier a besoin d’authentifier (ex. /signin) via AuthenticationManager.authenticate(...).
     */


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    /**
     * Encodeur de mots de passe : BCrypt avec force 12 (compromis sécurité/perf).
     * À utiliser lors de l’inscription et du changement de mot de passe.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        SecureRandom random = new SecureRandom() ;
        return new BCryptPasswordEncoder(12 , random);
    }





    /**
     * Configures and returns a {@link StrictHttpFirewall} bean that enforces strict
     * HTTP security rules on all incoming requests.
     *
     * ALLOWED HTTP METHODS
     * Only the following HTTP methods are permitted :
     * POST, GET, PATCH, PUT.
     * All other methods (DELETE, HEAD, OPTIONS, TRACE, etc.) are rejected.
     * This protects against HTTP Verb Tampering and Cross-Site Tracing (XST) attacks.
     *
     * BLOCKED URL PATTERNS
     * The following URL patterns are explicitly rejected to prevent common attack vectors :
     *
     * - Backslash (\) : prevents path traversal and directory traversal attacks
     *   on Windows-based file systems.
     *
     * - Semicolon (;) : prevents session hijacking via JSESSIONID in URL,
     *   security filter bypass, and Reflected File Download (RFD) attacks.
     *
     * - URL-encoded Carriage Return (%0D) : prevents HTTP Response Splitting
     *   by blocking injection of CR characters into response headers.
     *
     * - URL-encoded double slash (%2F%2F) : prevents security filter bypass
     *   and path traversal attacks using consecutive encoded slashes.
     *
     * - URL-encoded Line Feed (%0A) : prevents HTTP Response Splitting
     *   by blocking injection of LF characters into response headers.
     *   A single LF is often sufficient to split a response on non-strict servers,
     *   making this even more critical than CR alone.
     *
     * - URL-encoded Line Separator (%E2%80%A8, U+2028) : prevents XSS attacks
     *   and security filter bypasses caused by inconsistent interpretation
     *   of this Unicode character across Java, JavaScript, and JSON parsers.
     *
     * - URL-encoded Paragraph Separator (%E2%80%A9, U+2029) : prevents the same
     *   class of attacks as the Line Separator. Both Unicode separators are
     *   invisible in logs and interpreted inconsistently across parsers.
     *
     * - URL-encoded Period (%2E) : prevents directory traversal attacks using
     *   encoded dot sequences (e.g. %2E%2E resolves to ..) to escape the
     *   application root and access protected resources.
     *
     * - Any HTTP method (unsafe) : HTTP method validation is enforced.
     *   Disabling it entirely via setUnsafeAllowAnyHttpMethod would expose
     *   the application to Verb Tampering and XST attacks.
     *
     * - Null character (%00) : prevents null byte injection attacks that can
     *   be used to truncate strings, bypass file extension checks, or
     *   cause unexpected behavior in underlying system calls.
     *
     * - Authorization      : carries the Bearer token for JWT authentication.
     * - Refresh-Token      : carries the token used to renew an expired JWT.
     * - User-Email         : carries the email address of the authenticated user.
     * - Accept             : indicates the media types the client can process
     *                        (e.g. application/json). Listed in both lowercase
     *                        and capitalized forms to handle strict matching.
     * - Cache-Control      : conveys caching directives for the request
     *                        (e.g. no-cache, max-age=0).
     * - Postman-Token      : internal Postman header used to bypass browser cache.
     *                        Should be removed or restricted to dev profiles in production.
     * - Host               : specifies the target host and port of the request.
     *                        Mandatory in HTTP/1.1.
     * - Content-Length     : indicates the size in bytes of the request body.
     *                        Required for POST and PUT requests.
     * - Connection         : controls whether the network connection stays open
     *                        after the current transaction (e.g. keep-alive).
     *                        Listed in both lowercase and capitalized forms.
     * - If-None-Match      : used for conditional requests and HTTP cache validation
     *                        via ETag comparison.
     * - User-Agent         : identifies the client software originating the request.
     * - Accept-Encoding    : indicates the content encodings the client supports
     *                        (e.g. gzip, deflate).
     * - Content-Type       : indicates the media type of the request body
     *                        (e.g. application/json, multipart/form-data).
     *
     * ALLOWED QUERY PARAMETERS
     * Only the query parameter named "typeofreference" is accepted.
     * All other parameter names are rejected by the firewall.
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("POST" , "GET","PATCH", "PUT"));
        firewall.setAllowBackSlash(false);
        firewall.setAllowSemicolon(false);
        firewall.setAllowUrlEncodedCarriageReturn(false);
        firewall.setAllowUrlEncodedDoubleSlash(false);
        firewall.setAllowUrlEncodedLineFeed(false);
        firewall.setAllowUrlEncodedLineSeparator(false);
        firewall.setAllowUrlEncodedParagraphSeparator(false);
        firewall.setAllowUrlEncodedPeriod(false);
        firewall.setUnsafeAllowAnyHttpMethod(false);
        firewall.setAllowedParameterNames(parameter-> parameter.equals("typeofreference"));
        firewall.setAllowNull(false);
        return firewall;
    }

}



