package pe.edu.upeu.PharmaBackend.dto;

import java.io.Serializable;
import org.springframework.data.domain.Page;
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

    public static <T> PaginaResponseDTO<T> de(Page<T> page) {
        return new PaginaResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
