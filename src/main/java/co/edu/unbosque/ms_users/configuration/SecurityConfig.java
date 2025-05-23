package co.edu.unbosque.ms_users.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

     // Este bean configura la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita la protección CSRF si no la necesitas específicamente para tus APIs
            .csrf(csrf -> csrf.disable())
            // Configura la autorización de las peticiones HTTP
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Permite el acceso a todas las peticiones sin autenticación
                    // Esto es lo que evita que Spring Security bloquee tus endpoints por defecto
                    .anyRequest().permitAll()
            );
            // Si necesitaras autenticación más adelante, la configurarías aquí
            // .formLogin(...) o .httpBasic(...) etc.

        return http.build();
    }

    // Este bean define el codificador de contraseñas que usaremos
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder es una implementación segura recomendada
        return new BCryptPasswordEncoder();
    }
    
}
