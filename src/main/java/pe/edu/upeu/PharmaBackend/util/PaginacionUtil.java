package pe.edu.upeu.PharmaBackend.util;

import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;

/** Validación común para evitar tamaños excesivos y nombres de propiedades arbitrarios. */
public final class PaginacionUtil {
    public static final int TAMANIO_MAXIMO = 100;

    private PaginacionUtil() { }

    public static Pageable construir(int pagina, int tamanio, String ordenarPor,
                                      String direccion, Set<String> camposPermitidos,
                                      String campoDefecto) {
        if (pagina < 0) {
            throw new ReglaNegocioException("La página no puede ser negativa");
        }
        if (tamanio < 1 || tamanio > TAMANIO_MAXIMO) {
            throw new ReglaNegocioException("El tamaño de página debe estar entre 1 y "
                    + TAMANIO_MAXIMO);
        }

        String campo = ordenarPor == null || ordenarPor.isBlank()
                ? campoDefecto : ordenarPor.trim();
        if (!camposPermitidos.contains(campo)) {
            throw new ReglaNegocioException("Campo de ordenamiento no permitido: " + campo);
        }

        String sentido = direccion == null || direccion.isBlank() ? "asc" : direccion.trim();
        Sort sort;
        if ("asc".equalsIgnoreCase(sentido)) {
            sort = Sort.by(campo).ascending();
        } else if ("desc".equalsIgnoreCase(sentido)) {
            sort = Sort.by(campo).descending();
        } else {
            throw new ReglaNegocioException("La dirección debe ser asc o desc");
        }
        return PageRequest.of(pagina, tamanio, sort);
    }
}
