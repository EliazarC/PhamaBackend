package pe.edu.upeu.PharmaBackend.controller;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import pe.edu.upeu.PharmaBackend.dto.auth.*; import pe.edu.upeu.PharmaBackend.dto.usuario.UsuarioResponseDTO; import pe.edu.upeu.PharmaBackend.service.SeguridadService;
@RestController @RequestMapping("/api/v1/auth") public class AuthController {private final SeguridadService service;public AuthController(SeguridadService s){service=s;}
 @PostMapping("/login") public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){return ResponseEntity.ok(service.login(dto));}
 @GetMapping("/me") public ResponseEntity<UsuarioResponseDTO> me(Authentication auth){return ResponseEntity.ok(service.me(auth.getName()));}
 @PutMapping("/password") public ResponseEntity<Void> cambiarPassword(Authentication auth,@Valid @RequestBody CambioPasswordDTO dto){service.cambiarPassword(auth.getName(),dto);return ResponseEntity.noContent().build();}
}
