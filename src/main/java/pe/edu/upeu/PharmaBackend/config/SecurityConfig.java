package pe.edu.upeu.PharmaBackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.upeu.PharmaBackend.security.JwtAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

 @Bean
 public ObjectMapper objectMapper() {
  return new ObjectMapper();
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
 }

 @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
  return c.getAuthenticationManager();
 }

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter, ObjectMapper mapper) throws Exception {
  http.csrf(csrf -> csrf.disable())
          .cors(cors -> {})
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .httpBasic(b -> b.disable())
          .formLogin(f -> f.disable())
          .logout(l -> l.disable())
          .exceptionHandling(ex -> ex
                  .authenticationEntryPoint((req, res, e) -> {
                   Object msg = req.getAttribute("security.error");
                   res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                   res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                   mapper.writeValue(res.getOutputStream(), Map.of(
                           "timestamp", LocalDateTime.now().toString(),
                           "status", 401,
                           "error", "Unauthorized",
                           "message", msg == null ? "Debe iniciar sesión para acceder a este recurso" : msg.toString(),
                           "path", req.getRequestURI()
                   ));
                  })
                  .accessDeniedHandler((req, res, e) -> {
                   res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                   res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                   mapper.writeValue(res.getOutputStream(), Map.of(
                           "timestamp", LocalDateTime.now().toString(),
                           "status", 403,
                           "error", "Forbidden",
                           "message", "No tiene permisos para realizar esta operación",
                           "path", req.getRequestURI()
                   ));
                  })
          )
          .authorizeHttpRequests(auth -> auth
                  .requestMatchers("/api/health", "/api/v1/auth/login", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/error").permitAll()
                  .requestMatchers("/api/v1/auth/password").authenticated()
                  .requestMatchers("/api/v1/auth/me").authenticated()
                  .requestMatchers("/api/v1/empleados/**", "/api/v1/roles/**", "/api/v1/usuarios/**").hasAuthority("USUARIOS_ADMINISTRAR")
                  .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**", "/api/v1/productos/**").hasAuthority("CATALOGO_LEER")
                  .requestMatchers("/api/v1/categorias/**", "/api/v1/productos/**").hasAuthority("CATALOGO_ESCRIBIR")
                  .requestMatchers(HttpMethod.GET, "/api/v1/clientes/**").hasAuthority("CLIENTES_LEER")
                  .requestMatchers("/api/v1/clientes/**").hasAuthority("CLIENTES_ESCRIBIR")
                  .requestMatchers(HttpMethod.GET, "/api/v1/ventas/**").hasAuthority("VENTAS_LEER")
                  .requestMatchers("/api/v1/ventas/**").hasAuthority("VENTAS_REGISTRAR")
                  .requestMatchers("/api/v1/reportes/**").hasAuthority("REPORTES_VER")
                  .anyRequest().authenticated()
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

  return http.build();
 }
}