package de.hhu.ausgabenverwaltung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
        chainBuilder.authorizeHttpRequests(
                configurer -> configurer
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                    //.anyRequest().permitAll()
                    .anyRequest().authenticated()
            )
            .csrf().disable() // TODO: Das ist eine schlechte Idee :D
            .oauth2Login();

        return chainBuilder.build();
    }

}
