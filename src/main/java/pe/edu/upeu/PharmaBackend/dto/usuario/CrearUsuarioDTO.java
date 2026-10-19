package pe.edu.upeu.PharmaBackend.dto.usuario;
import jakarta.validation.constraints.*; import java.util.List;
public record CrearUsuarioDTO(@NotBlank @Size(min=3,max=50) String username,@NotBlank @Size(min=8,max=72) String password,@NotNull Long empleadoId,@NotEmpty List<@NotNull Long> rolIds) {}
