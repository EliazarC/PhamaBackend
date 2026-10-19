package pe.edu.upeu.PharmaBackend.dto.usuario;
import pe.edu.upeu.PharmaBackend.entity.Empleado;
public record EmpleadoResponseDTO(Long id,String dni,String nombres,String apellidos,String cargo,String email,String telefono,boolean estado,boolean tieneUsuario){ public static EmpleadoResponseDTO from(Empleado e,boolean tiene){return new EmpleadoResponseDTO(e.getId(),e.getDni(),e.getNombres(),e.getApellidos(),e.getCargo(),e.getEmail(),e.getTelefono(),Boolean.TRUE.equals(e.getEstado()),tiene);} }
