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
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

       .addFilterBefore(new JwtTokenFilter(appUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        SecureRandom random = new SecureRandom() ;
        return new BCryptPasswordEncoder(12 , random);
    }





    /**
     * Configures and returns a {@link StrictHttpFirewall} bean that enforces strict
     * HTTP security rules on all incoming requests.
     * ALLOWED HTTP METHODS
     * Only the following HTTP methods are permitted :
     * POST, GET, PATCH, PUT.
     * All other methods (DELETE, HEAD, OPTIONS, TRACE, etc.) are rejected.
     * This protects against HTTP Verb Tampering and Cross-Site Tracing (XST) attacks.
     * BLOCKED URL PATTERNS
     * The following URL patterns are explicitly rejected to prevent common attack vectors :
     * - Backslash (\) : prevents path traversal and directory traversal attacks
     *   on Windows-based file systems.
     *   security filter bypass, and Reflected File Download (RFD) attacks.
     * - URL-encoded Carriage Return (%0D) : prevents HTTP Response Splitting
     *   by blocking injection of CR characters into response headers.
     * - URL-encoded double slash (%2F%2F) : prevents security filter bypass
     *   and path traversal attacks using consecutive encoded slashes.
     * - URL-encoded Line Feed (%0A) : prevents HTTP Response Splitting
     *   by blocking injection of LF characters into response headers.
     *   A single LF is often sufficient to split a response on non-strict servers,
     *   making this even more critical than CR alone.
     * - URL-encoded Line Separator (%E2%80%A8, U+2028) : prevents XSS attacks
     *   and security filter bypasses caused by inconsistent interpretation
     *   of this Unicode character across Java, JavaScript, and JSON parsers.
     * - URL-encoded Paragraph Separator (%E2%80%A9, U+2029) : prevents the same
     *   class of attacks as the Line Separator. Both Unicode separators are
     *   invisible in logs and interpreted inconsistently across parsers.
     * - URL-encoded Period (%2E) : prevents directory traversal attacks using
     *   application root and access protected resources.
     * - Any HTTP method (unsafe) : HTTP method validation is enforced.
     *   Disabling it entirely via setUnsafeAllowAnyHttpMethod would expose
     *   the application to Verb Tampering and XST attacks.
     * - Null character (%00) : prevents null byte injection attacks that can
     *   be used to truncate strings, bypass file extension checks, or
     *   cause unexpected behavior in underlying system calls.
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



