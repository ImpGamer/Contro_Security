package com.api.facturas.security;

import com.api.facturas.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    //Registro de rutas permitidas
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        try {
            httpSecurity.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                    .and()
                    .csrf().disable()
                    .authorizeHttpRequests()
                    //endpoints protegidas por springSecurity
                    .requestMatchers("/user/signUp",
                            "/user/login","/user/forgotPassword","/user/getAll","/user/edit/{id}"
                                    ,"/user/changePassword","/user/regainPassword","/category/add"
                    ,"/category/get","/category/update/{id}","/products/add","/products",
                            "/products/update/{productID}","/products/delete/{id}","/products/category"
                    ,"/products/{id}")
                    //Permitir a cualquier usuario ingresar a estas rutas
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and().exceptionHandling()
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return httpSecurity.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception {
        return configuration.getAuthenticationManager();
    }
}
