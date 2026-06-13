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
                    // 1. Rota Pública (Todos podem tentar logar)
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();

                    // 2. Rotas da Secretaria / Coordenação (ADMIN)
                    // Somente eles podem criar (POST), editar (PUT) ou deletar (DELETE) a estrutura acadêmica
                    req.requestMatchers(HttpMethod.POST, "/alunos/**", "/professores/**", "/turmas/**", "/disciplinas/**", "/matriculas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/alunos/**", "/professores/**", "/turmas/**", "/disciplinas/**", "/matriculas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/alunos/**", "/professores/**", "/turmas/**", "/disciplinas/**", "/matriculas/**").hasRole("ADMIN");

                    // 3. Rotas dos Professores (Diário Acadêmico)
                    // Somente professores (e admins, que herdam a role) podem lançar ou alterar notas e presenças
                    req.requestMatchers(HttpMethod.POST, "/notas/**", "/frequencias/**").hasRole("PROFESSOR");
                    req.requestMatchers(HttpMethod.PUT, "/notas/**", "/frequencias/**").hasRole("PROFESSOR");
                    req.requestMatchers(HttpMethod.DELETE, "/notas/**", "/frequencias/**").hasRole("PROFESSOR");

                    // 4. Rotas de Leitura (GET)
                    // Qualquer usuário que estiver logado (com crachá) pode consultar/listar os dados
                    req.requestMatchers(HttpMethod.GET, "/**").authenticated();

                    // 5. Medida de Segurança Final
                    // Se alguém criar um endpoint novo no futuro e esquecer de mapear, ele nasce trancado.
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
        // Coloque aqui a URL exata onde o React do David vai rodar
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a regra para a API inteira
        return source;
    }
}