package pe.edu.upeu.PharmaBackend.controller;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import pe.edu.upeu.PharmaBackend.dto.usuario.*; import pe.edu.upeu.PharmaBackend.service.SeguridadService; import java.util.List;
@RestController @RequestMapping("/api/v1") public class UsuarioAdminController {private final SeguridadService service;public UsuarioAdminController(SeguridadService s){service=s;}
 @GetMapping("/usuarios") public List<UsuarioResponseDTO> usuarios(){return service.listarUsuarios();}
 @PostMapping("/usuarios") public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody CrearUsuarioDTO dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));}
 @GetMapping("/empleados") public List<EmpleadoResponseDTO> empleados(){return service.listarEmpleados();}
 @GetMapping("/roles") public List<RolResponseDTO> roles(){return service.listarRoles();}
}
