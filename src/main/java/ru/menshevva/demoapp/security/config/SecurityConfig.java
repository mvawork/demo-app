package ru.menshevva.demoapp.security.config;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import ru.menshevva.demoapp.security.service.SecurityService;
import ru.menshevva.demoapp.ui.LoginView;

@Slf4j
@Configuration
@EnableWebSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    @Bean
    public GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
        return new SimpleAuthorityMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure your static resources with public access
        http.authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/favicon.ico",
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
                        "/back-channel-logout")
                .permitAll());

        // Configure Vaadin's security using VaadinSecurityConfigurer
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(LoginView.class);
            //configurer.oauth2LoginPage("/oauth2/authorization/keycloak");
        });
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

    @Bean
    public UserDetailsService userDetailsService(SecurityService securityService) {
        return username -> {
            // 1. Ищем пользователя по логину в базе данных
            var user = securityService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("Пользователь не найден");
                // Если пользователь не существует
            }
            // 2. Возвращаем полную информацию о пользователе
            return user;
        };
    }

}