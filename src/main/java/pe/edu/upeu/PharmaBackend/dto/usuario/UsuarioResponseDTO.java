package pe.edu.upeu.PharmaBackend.dto.usuario;
import pe.edu.upeu.PharmaBackend.entity.Usuario; import java.util.List;
public record UsuarioResponseDTO(Long id,String username,boolean estado,Long empleadoId,String nombreCompleto,List<String> roles) {
 public static UsuarioResponseDTO from(Usuario u){return new UsuarioResponseDTO(u.getId(),u.getUsername(),Boolean.TRUE.equals(u.getEstado()),u.getEmpleado().getId(),u.getEmpleado().getNombreCompleto(),u.getRoles().stream().map(r->r.getNombre()).sorted().toList());}
}
