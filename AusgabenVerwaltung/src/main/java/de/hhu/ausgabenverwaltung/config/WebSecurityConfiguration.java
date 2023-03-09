package de.hhu.ausgabenverwaltung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
        chainBuilder.authorizeHttpRequests(
                configurer -> configurer
                    .requestMatchers("/api/**", "/error").permitAll()
                    .anyRequest().authenticated()
            )
            .csrf().ignoringRequestMatchers("/api/**")
            .and()
            .oauth2Login();

        return chainBuilder.build();
    }

}
