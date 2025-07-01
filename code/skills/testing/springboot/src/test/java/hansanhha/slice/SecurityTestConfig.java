package hansanhha.slice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").hasAnyRole("admin")
                        .anyRequest().permitAll())
                .build();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = CustomSecurityContextFactory.class)
    public @interface WithMockCustomUser {
        String username();
        String password() default "password";
        String role();
    }

    static class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new CustomAuthentication(withMockCustomUser.username(), withMockCustomUser.password(), withMockCustomUser.role());
            context.setAuthentication(authentication);
            return context;
        }
    }

    static class CustomAuthentication implements Authentication {
        private final String username;
        private final String password;
        private final String role;
        private boolean authenticated = true;

        public CustomAuthentication(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(() -> "ROLE_" + role);
        }

        @Override
        public Object getCredentials() {
            return password;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return username;
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.authenticated = isAuthenticated;
        }

        @Override
        public String getName() {
            return username;
        }
    }

    @Bean
    UserDetailsService simpleUserDetailsService() {
        return new SimpleUserDetailsService();
    }

    static class SimpleUserDetailsService implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return new SimpleUserDetails(username, "password", "admin");
        }
    }


    static class SimpleUserDetails implements UserDetails{

        private final String username;
        private final String password;
        private final String role;

        public SimpleUserDetails(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(() -> "ROLE_" + role);
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }
    }
}
