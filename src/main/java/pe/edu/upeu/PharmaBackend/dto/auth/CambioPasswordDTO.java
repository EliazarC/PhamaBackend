package pe.edu.upeu.PharmaBackend.dto.auth;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.Size;
public record CambioPasswordDTO(@NotBlank String passwordActual,@NotBlank @Size(min=8,max=72) String passwordNueva) {}
