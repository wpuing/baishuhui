package com.baishuhui.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 用户服务 Spring Security 配置。
 *
 * @author wei yz
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private final InternalTokenFilter internalTokenFilter;

    /**
     * 密码编码器。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 操作人上下文过滤器：仅挂到 Security 链，不注册为 Servlet Filter。
     */
    @Bean
    public OperatorContextFilter operatorContextFilter() {
        return new OperatorContextFilter();
    }

    /**
     * 关闭 Servlet 容器对 OperatorContextFilter 的自动注册，避免与 Security 链重复执行。
     */
    @Bean
    public FilterRegistrationBean<OperatorContextFilter> operatorContextFilterRegistration(
            OperatorContextFilter operatorContextFilter) {
        FilterRegistrationBean<OperatorContextFilter> registration =
                new FilterRegistrationBean<>(operatorContextFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * 内部令牌过滤器不重复注册为 Servlet Filter（仅挂 Security 链）。
     */
    @Bean
    public FilterRegistrationBean<InternalTokenFilter> internalTokenFilterRegistration(
            InternalTokenFilter internalTokenFilter) {
        FilterRegistrationBean<InternalTokenFilter> registration =
                new FilterRegistrationBean<>(internalTokenFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * HTTP 安全过滤链。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, OperatorContextFilter operatorContextFilter) throws Exception {
        // 无状态 JWT：关闭 CSRF/Session，登录与文档路径放行，管理端强制 ADMIN/SUPER_ADMIN
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/captcha",
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/areas/**",
                                "/api/categories",
                                "/api/categories/**",
                                "/internal/ip/**",
                                "/internal/user/**",
                                "/internal/wallet/**",
                                "/actuator/**",
                                "/doc.html",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/webjars/**")
                        .permitAll()
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .anyRequest().authenticated())
                // 钱包内部接口先校验共享令牌，再进入 JWT 链（钱包路径本身 permitAll）
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT 解析后再写入操作人上下文，供审计字段填充
                .addFilterAfter(operatorContextFilter, JwtAuthFilter.class);
        return http.build();
    }
}
