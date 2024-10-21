package com.mar.ds.config;

import com.mar.ds.service.security.CustomRequestCache;
import com.mar.ds.service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConf {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors().disable()

                .requestCache()
                    .requestCache(new CustomRequestCache()) // сохраняем сессию в кеше
                .and()
                .authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll() // доступ для тех. данных
                    .anyRequest().authenticated() // остальные запросы через авторизацию
                .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                    .loginProcessingUrl("/login")
                    .successForwardUrl("/")
                    .failureUrl("/login?error")
                .and().build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(List.of(
                User.withUsername("marolok")
                        .password("{noop}123")
                        .roles("USER")
                        .build(),
                User.withUsername("mar")
                        .password("{noop}111")
                        .roles("USER")
                        .build()
        ));
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .antMatchers(
                        "/VAADIN/**",
                        "/icons/**",
                        "/manifest.webmanifest",
                        "/sw.js"
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
