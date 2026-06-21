package br.edu.ifal.sigamais.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // 1. Rota Pública (Todos podem tentar logar e criar o primeiro admin se precisarem)
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers("/error").permitAll();

                    // 2. Rotas da Secretaria / Coordenação (ADMIN)
                    // Adicionado o mapeamento duplo: "/rota" e "/rota/**" para o Spring Security 6+ não bloquear a raiz
                    req.requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/**", "/alunos", "/alunos/**", "/professores", "/professores/**", "/turmas", "/turmas/**", "/disciplinas", "/disciplinas/**", "/matriculas", "/matriculas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/usuarios", "/usuarios/**", "/alunos", "/alunos/**", "/professores", "/professores/**", "/turmas", "/turmas/**", "/disciplinas", "/disciplinas/**", "/matriculas", "/matriculas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/usuarios", "/usuarios/**", "/alunos", "/alunos/**", "/professores", "/professores/**", "/turmas", "/turmas/**", "/disciplinas", "/disciplinas/**", "/matriculas", "/matriculas/**").hasRole("ADMIN");

                    // 3. Rotas dos Professores (Diário Acadêmico)
                    req.requestMatchers(HttpMethod.POST, "/notas", "/notas/**", "/frequencias", "/frequencias/**").hasRole("PROFESSOR");
                    req.requestMatchers(HttpMethod.PUT, "/notas", "/notas/**", "/frequencias", "/frequencias/**").hasRole("PROFESSOR");
                    req.requestMatchers(HttpMethod.DELETE, "/notas", "/notas/**", "/frequencias", "/frequencias/**").hasRole("PROFESSOR");

                    // 4. Rotas de Leitura (GET)
                    req.requestMatchers(HttpMethod.GET, "/**").authenticated();

                    // 5. Medida de Segurança Final
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}