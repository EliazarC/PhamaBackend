package pe.edu.upeu.PharmaBackend.auditoria;

import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Evento de negocio publicado dentro de la transacción que realizó el cambio. */
public record EventoAuditoria(
        String usuario,
        String accion,
        String entidad,
        Long entidadId,
        String detalle,
        String requestId) {

    public static EventoAuditoria de(String accion, String entidad, Long entidadId, String detalle) {
        return new EventoAuditoria(usuarioActual(), accion, entidad, entidadId,
                recortar(detalle, 500), MDC.get("requestId"));
    }

    /** El login aún no dispone de una autenticación en SecurityContext. */
    public static EventoAuditoria login(String accion, String username) {
        return new EventoAuditoria(recortar(username, 50), accion, "Usuario",
                null, null, MDC.get("requestId"));
    }

    private static String usuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "anonimo";
        }
        return recortar(authentication.getName(), 50);
    }

    private static String recortar(String texto, int maximo) {
        return texto == null || texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
