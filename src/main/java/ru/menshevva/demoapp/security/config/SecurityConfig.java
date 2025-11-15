package ru.menshevva.demoapp.security.config;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import ru.menshevva.demoapp.security.service.SecurityService;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
        return new SimpleAuthorityMapper();
    }

    @Configuration
    @Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
    @RequiredArgsConstructor
    public static class Oauth2SecurityConfiguration {

        private final ClientRegistrationRepository clientRegistrationRepository;

        private final GrantedAuthoritiesMapper authoritiesMapper;


        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth ->
                    auth.requestMatchers("/favicon.ico",
                                    "/styles.css",
                                    "/icons/**",
                                    "/images/**",
                                    "/styles/**",
                                    "/styles/**",
                                    "/logged-out",
                                    "/session-expired",
                                    "/no-access",
                                    "/blocked-user",
                                    "/no-such-user",
                                    "/back-channel-logout"
                            )
                            .permitAll()
            );
            http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
                    }
            );
            http
                    .oauth2Login(oauth2Login ->
                            oauth2Login.clientRegistrationRepository(clientRegistrationRepository)
                                    .userInfoEndpoint(userInfoEndpointConfig ->
                                            userInfoEndpointConfig.userAuthoritiesMapper(authoritiesMapper))
                                    .successHandler(new VaadinSavedRequestAwareAuthenticationSuccessHandler())

                    )
                    .logout(httpSecurityLogoutConfigurer ->
                            httpSecurityLogoutConfigurer
                                    .logoutSuccessHandler(logoutSuccessHandler())
                                    .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"))
                    );

            return http.build();
        }

        @Bean
        public OAuth2UserService<OidcUserRequest, OidcUser> getOidcUserOAuth2UserService(SecurityService securityService) {
            var delegate = new OidcUserService();
            return userRequest -> {
                var oidcUser = delegate.loadUser(userRequest);
                return securityService.getOidcUser(oidcUser);
            };
        }


        private OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler() {
            var logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
            logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logged-out");
            return logoutSuccessHandler;
        }


    }

}