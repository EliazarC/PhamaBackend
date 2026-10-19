package pe.edu.upeu.PharmaBackend.dto.usuario;
import pe.edu.upeu.PharmaBackend.entity.Rol;
public record RolResponseDTO(Long id,String nombre,String descripcion){public static RolResponseDTO from(Rol r){return new RolResponseDTO(r.getId(),r.getNombre(),r.getDescripcion());}}
