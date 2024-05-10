package self.project.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import self.project.websocket.security.AuthenticationSuccessHandlerImpl;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth // TODO() configure
                        .requestMatchers("/**")
                        .authenticated())
                .formLogin(login -> login
//                        .loginPage("/login")
//                        .permitAll()
                        .successHandler(authenticationSuccessHandler()))
//                .formLogin(login -> login
//                        .loginPage("/auth/login")
//                        .loginProcessingUrl("/process_login")
//                        .defaultSuccessUrl("/index.html", true)
//                        .successHandler(authenticationSuccessHandler)
//                        .failureUrl("/auth/login?error"))
                .httpBasic(Customizer.withDefaults()); // TODO() check if needed
//                .authenticationProvider() TODO() configure
//                .userDetailsService() TODO() configure
//                .logout(out -> out // TODO() configure
//                        .logoutUrl("/logout")
//                        .invalidateHttpSession(true)
//                        .logoutSuccessUrl("/auth/login"));
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }
}
