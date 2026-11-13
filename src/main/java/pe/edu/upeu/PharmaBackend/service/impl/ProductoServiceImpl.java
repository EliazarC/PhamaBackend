package pe.edu.upeu.PharmaBackend.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import pe.edu.upeu.PharmaBackend.auditoria.EventoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.RecursosNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.repository.CategoriaRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;


@Service
public class ProductoServiceImpl implements ProductoService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ApplicationEventPublisher eventos;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ApplicationEventPublisher eventos) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public ProductoResponseDTO create(ProductoRequestDTO t) {
        String nombre = t.getNombre().trim();
        if(productoRepository.existsByNombreIgnoreCase(nombre)){
            throw new ReglaNegocioException(
                    "Ya existe un producto con el nombre "+ nombre
            );
        }

        Categoria categoria = buscarCategoria(t.getCategoriaId());

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria);

        Producto prodCreado = productoRepository.save(producto);
        eventos.publishEvent(EventoAuditoria.de("PRODUCTO_CREADO", "Producto",
                prodCreado.getId(), prodCreado.getNombre()));

        return convertirResponse(prodCreado);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public ProductoResponseDTO update(Long aLong, ProductoRequestDTO t) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(()->
                new RecursosNoEncontradoException(
                        "Producto no encontrado con id: "+ aLong
                )
        );

        BigDecimal precioAnterior = producto.getPrecio();
        String nombre = t.getNombre().trim();
        if(productoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, aLong)){
            throw new ReglaNegocioException(
                    "Ya existe un producto con el nombre "+ nombre
            );
        }

        Categoria categoria = buscarCategoria(t.getCategoriaId());

        producto.setNombre(nombre);
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria);

        Producto prodActualizado = productoRepository.saveAndFlush(producto);
        if (precioAnterior.compareTo(prodActualizado.getPrecio()) != 0) {
            eventos.publishEvent(EventoAuditoria.de("PRECIO_MODIFICADO", "Producto",
                    prodActualizado.getId(), precioAnterior + " -> " + prodActualizado.getPrecio()));
        }

        return convertirResponse(prodActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos", key = "'id-' + #p0")
    public ProductoResponseDTO read(Long aLong) {
        Producto producto = productoRepository.findById(aLong)
                .orElseThrow(()->
                        new RecursosNoEncontradoException(
                                "Producto no encontrado con id: "+ aLong
                        )
                );
        return convertirResponse(producto);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "productos", allEntries = true)
    public void delete(Long aLong) {
        Producto producto = productoRepository.findById(aLong).orElseThrow(()->
                new RecursosNoEncontradoException(
                        "Producto no encontrado con id: "+ aLong
                )
        );
        eventos.publishEvent(EventoAuditoria.de("PRODUCTO_ELIMINADO", "Producto",
                producto.getId(), producto.getNombre()));
        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos", key = "'todas'")
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productos", key = "#p0 + '-' + #p1 + '-' + #p2 + '-' + #p3")
    public PaginaResponseDTO<ProductoResponseDTO> listar(int pagina, int tamanio,
                                                          String ordenarPor, String direccion) {
        if (pagina < 0) {
            throw new ReglaNegocioException("La página no puede ser negativa");
        }
        if (tamanio < 1 || tamanio > 100) {
            throw new ReglaNegocioException("El tamaño de página debe estar entre 1 y 100");
        }

        java.util.Set<String> camposPermitidos = java.util.Set.of(
                "id", "nombre", "precio", "stock", "estado", "fechaCreacion", "fechaModificacion"
        );
        String campo = (ordenarPor == null || ordenarPor.isBlank()) ? "nombre" : ordenarPor.trim();
        if (!camposPermitidos.contains(campo)) {
            throw new ReglaNegocioException("Campo de ordenamiento no permitido: " + campo);
        }

        String sentido = (direccion == null || direccion.isBlank()) ? "asc" : direccion.trim();
        if (!sentido.equalsIgnoreCase("asc") && !sentido.equalsIgnoreCase("desc")) {
            throw new ReglaNegocioException("La dirección debe ser asc o desc");
        }

        Sort sort = sentido.equalsIgnoreCase("desc")
                ? Sort.by(campo).descending()
                : Sort.by(campo).ascending();
        Page<ProductoResponseDTO> resultado = productoRepository
                .findAll(PageRequest.of(pagina, tamanio, sort))
                .map(this::convertirResponse);

        return new PaginaResponseDTO<>(
                resultado.getContent(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.isFirst(),
                resultado.isLast()
        );
    }

    private Categoria buscarCategoria(Long categoriaId){
        return categoriaRepository.findById(categoriaId).orElseThrow(()->
                new RecursosNoEncontradoException(
                        "Categoria no encontrada con id: "+ categoriaId
                )
        );
    }

    private ProductoResponseDTO convertirResponse(Producto producto){
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getEstado(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getFechaCreacion(),
                producto.getFechaModificacion(),
                producto.getCreadoPor(),
                producto.getModificadoPor()
        );
    }
}