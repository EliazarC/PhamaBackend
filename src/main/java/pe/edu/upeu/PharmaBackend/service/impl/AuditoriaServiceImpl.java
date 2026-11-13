package pe.edu.upeu.PharmaBackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.auditoria.EventoAuditoria;
import pe.edu.upeu.PharmaBackend.dto.AuditoriaEventoDTO;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.AuditoriaEvento;
import pe.edu.upeu.PharmaBackend.repository.AuditoriaEventoRepository;
import pe.edu.upeu.PharmaBackend.service.service.AuditoriaService;
import pe.edu.upeu.PharmaBackend.util.PaginacionUtil;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaServiceImpl.class);
    private static final Set<String> CAMPOS_ORDENABLES = Set.of("fecha", "usuario", "accion");
    private final AuditoriaEventoRepository repository;

    public AuditoriaServiceImpl(AuditoriaEventoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(EventoAuditoria evento) {
        AuditoriaEvento registro = new AuditoriaEvento();
        registro.setFecha(LocalDateTime.now());
        registro.setUsuario(evento.usuario());
        registro.setAccion(evento.accion());
        registro.setEntidad(evento.entidad());
        registro.setEntidadId(evento.entidadId());
        registro.setDetalle(evento.detalle());
        registro.setRequestId(evento.requestId());
        repository.save(registro);
        log.info("Auditoría {} | {} {} | usuario={}", evento.accion(),
                evento.entidad(), evento.entidadId(), evento.usuario());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<AuditoriaEventoDTO> buscar(
            String usuario, String accion, LocalDate desde, LocalDate hasta,
            int pagina, int tamanio) {
        Pageable pageable = PaginacionUtil.construir(pagina, tamanio, "fecha", "desc",
                CAMPOS_ORDENABLES, "fecha");
        LocalDateTime desdeHora = desde == null ? null : desde.atStartOfDay();
        LocalDateTime hastaHora = hasta == null ? null : hasta.plusDays(1).atStartOfDay();
        return PaginaResponseDTO.de(repository.buscar(
                vacioANulo(usuario), vacioANulo(accion), desdeHora, hastaHora, pageable)
                .map(AuditoriaEventoDTO::de));
    }

    private static String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
