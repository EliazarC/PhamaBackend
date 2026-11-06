package pe.edu.upeu.PharmaBackend.dto;

import java.io.Serializable;
import java.util.List;

/** Respuesta paginada común para listados de alto volumen. */
public record PaginaResponseDTO<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas,
        boolean primera,
        boolean ultima
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
