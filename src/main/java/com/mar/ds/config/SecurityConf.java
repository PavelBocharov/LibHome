package com.mar.ds.config;

import com.mar.ds.service.security.CustomRequestCache;
import com.mar.ds.service.security.JwtTokenFilter;
import com.mar.ds.service.security.JwtTokenUtil;
import com.mar.ds.service.security.SecurityUtils;
import com.mar.ds.service.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConf {

    private final UserService userService;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtTokenUtil jwtTokenUtil;

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
                .failureUrl("/login?error")
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    User user = (User) authentication.getPrincipal();
                    httpServletResponse.addHeader(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenUtil.createJwtToken(user.getUsername())
                    );
                    httpServletResponse.sendRedirect("/");
                })
                .and()

                // Add JWT token filter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(userService.findAll());
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
