package com.uade.demo.config;

import java.util.List;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uade.demo.model.Rol;
import com.uade.demo.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/usuarios/login",
                                "/api/usuarios/registro",
                                "/error",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        // Lectura pública: lo que el front muestra sin iniciar sesión
                        .requestMatchers(HttpMethod.GET,
                                "/api/peliculas/**",
                                "/api/funciones/**",
                                "/api/salas/**")
                        .permitAll()
                        .requestMatchers("/api/usuarios/crear-admin").hasRole(Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/usuarios")
                        .hasRole(Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**")
                        .hasRole(Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                        .hasRole(Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.POST,
                                "/api/peliculas/**", "/api/salas/**", "/api/funciones/**", "/api/descuentos/**")
                        .hasAnyRole(Rol.ADMIN.name(), Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT,
                                "/api/peliculas/**", "/api/salas/**", "/api/funciones/**")
                        .hasAnyRole(Rol.ADMIN.name(), Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/peliculas/**", "/api/salas/**", "/api/funciones/**", "/api/descuentos/**")
                        .hasAnyRole(Rol.ADMIN.name(), Rol.SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/descuentos/**")
                        .hasAnyRole(Rol.ADMIN.name(), Rol.SUPER_ADMIN.name())
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}