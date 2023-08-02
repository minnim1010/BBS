package spring.bbs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spring.bbs.jwt.JwtAccessDeniedHandler;
import spring.bbs.jwt.JwtAuthenticationEntryPoint;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.JwtSecurityConfig;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisTemplate<String, Object> redisTemplate;

    public SecurityConfig(JwtProvider jwtProvider,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          RedisTemplate<String, Object> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http

                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers((headers) ->
                        headers.addHeaderWriter(
                                new XFrameOptionsHeaderWriter(
                                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

                .authorizeHttpRequests((request) ->
                        request
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/api-docs/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                                .requestMatchers("/home", "/error").permitAll()
                                .requestMatchers("/api/v1/login", "/api/v1/join").permitAll()
                                .requestMatchers("/api/v1/logout").authenticated()
                                .requestMatchers(HttpMethod.GET).permitAll()
                                .anyRequest().authenticated())

                .apply(new JwtSecurityConfig(jwtProvider, redisTemplate));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
