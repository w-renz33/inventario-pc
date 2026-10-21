package com.sistema.inventario.catalogo.producto.service;

import com.sistema.inventario.catalogo.especificacion.service.EspecificacionResolver;
import com.sistema.inventario.common.EstadoProducto;
import com.sistema.inventario.common.MotivoMovimiento;
import com.sistema.inventario.common.Pagina;
import com.sistema.inventario.common.Paginacion;
import com.sistema.inventario.common.ReferenciaTipo;
import com.sistema.inventario.common.TipoMovimiento;
import com.sistema.inventario.catalogo.producto.dto.ProductCreateDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductResponseDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductoDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductoUpdateDTO;
import com.sistema.inventario.catalogo.categoria.entity.Categoria;
import com.sistema.inventario.movimiento.entity.MovimientoStock;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.categoria.repository.CategoriaRepository;
import com.sistema.inventario.movimiento.repository.MovimientoStockDao;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoDao productoDao;

    private final CategoriaRepository categoriaRepository;

    private final MovimientoStockDao movimientoStockDao;

    private final SecurityUtils securityUtils;

    private final EspecificacionResolver especificacionResolver;

    public ProductoService(ProductoDao productoDao,
                           CategoriaRepository categoriaRepository,
                           MovimientoStockDao movimientoStockDao,
                           SecurityUtils securityUtils,
                           EspecificacionResolver especificacionResolver) {
        this.productoDao = productoDao;
        this.categoriaRepository = categoriaRepository;
        this.movimientoStockDao = movimientoStockDao;
        this.securityUtils = securityUtils;
        this.especificacionResolver = especificacionResolver;
    }

    public Pagina<ProductoDTO> findPaged(int page, int size, Long categoriaId) {
        int pageSafe = Paginacion.sanearPagina(page);
        int sizeSafe = Paginacion.sanearTamano(size);
        List<ProductoDTO> content = productoDao.findPage(pageSafe, sizeSafe, categoriaId).stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());
        long total = productoDao.count(categoriaId);
        return Pagina.of(content, pageSafe, sizeSafe, total);
    }

    public ProductoDTO findById(Long id) {
        Producto producto = productoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return converToDTO(producto);
    }

    // Busca por nombre o SKU para el modal de consulta.
    // Exige al menos 2 caracteres y limita el resultado a 50 elementos.
    public List<ProductoDTO> buscar(String q, int limit) {
        String texto = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        if (texto.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El texto de busqueda debe tener al menos 2 caracteres");
        }
        int limite = Math.min(Math.max(limit, 1), 50);
        return productoDao.buscarPorNombreOSku(texto, limite).stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO save(ProductCreateDTO dto) {
        Producto producto = converCreateToEntity(dto);

        // Crea la categoría si no existe.
        if (dto.getCategoria() != null && !dto.getCategoria().isEmpty()) {
            Categoria categoria = categoriaRepository.findByNombre(dto.getCategoria())
                    .orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(dto.getCategoria());
                        return categoriaRepository.save(nueva);
                    });
            producto.setCategoria(categoria);
        }
        if (producto.getStock() == null) {
            producto.setStock(0);
        }

        validarTipoComponente(producto);

        producto.setSku(resolverSku(dto.getSku(), producto));
        producto = productoDao.save(producto);

        // Registra el stock inicial como entrada cuando es mayor que cero.
        if (producto.getStock() != null && producto.getStock() > 0) {
            registrarMovimiento(producto, producto.getStock(), TipoMovimiento.ENTRADA, null,
                    MotivoMovimiento.INVENTARIO_INICIAL,
                    ReferenciaTipo.AJUSTE, null,
                    "Stock inicial al crear producto");
        }

        return convertToResponseDTO(producto);

    }

    @Transactional
    public ProductoDTO update(Long id, ProductoUpdateDTO dto) {
        Producto producto = productoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int stockAnterior = producto.getStock();

        // Aplica solo los campos enviados.
        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getStock() != null) {
            producto.setStock(dto.getStock());
        }
        if (dto.getPrecioVenta() != null) {
            producto.setPrecioVenta(dto.getPrecioVenta());
        }
        if (dto.getStockMinimo() != null) {
            producto.setStockMinimo(dto.getStockMinimo());
        }
        if (dto.getCodigoBarras() != null) {
            producto.setCodigoBarras(dto.getCodigoBarras());
        }
        if (dto.getSku() != null) {
            producto.setSku(dto.getSku());
        }
        if (dto.getMarca() != null) {
            producto.setMarca(dto.getMarca());
        }
        if (dto.getModelo() != null) {
            producto.setModelo(dto.getModelo());
        }
        if (dto.getTipoComponente() != null) {
            producto.setTipoComponente(dto.getTipoComponente());
        }
        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }
        if (dto.getPrecioCompra() != null) {
            producto.setPrecioCompra(dto.getPrecioCompra());
        }

        // Asigna la categoría indicada, creándola si no existe.
        if (dto.getCategoria() != null && !dto.getCategoria().isEmpty()) {
            Categoria categoria = categoriaRepository.findByNombre(dto.getCategoria())
                    .orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(dto.getCategoria());
                        return categoriaRepository.save(nueva);
                    });
            producto.setCategoria(categoria);
        }

        producto = productoDao.save(producto);

        // Registra el ajuste cuando cambia el stock; el tipo AJUSTE cumple la restricción CHECK.
        if (stockAnterior != producto.getStock()) {
            int diferencia = Math.abs(producto.getStock() - stockAnterior);
            registrarMovimiento(producto, diferencia, TipoMovimiento.AJUSTE, stockAnterior,
                    MotivoMovimiento.AJUSTE_INVENTARIO, ReferenciaTipo.AJUSTE, null,
                    String.format("Stock actualizado de %d a %d", stockAnterior, producto.getStock()));
        }

        return converToDTO(producto);

    }

    // Baja lógica: marca el producto como DESCONTINUADO en vez de borrarlo.
    // Conserva el kardex porque movimientos_stock e ingreso_detalle lo
    // referencian con FK restrictiva. El stock restante sale como MERMA.
    @Transactional
    public void delete(Long id)  {
        Producto producto = productoDao.findById(id)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (EstadoProducto.DESCONTINUADO.equals(producto.getEstado())) {
            return;
        }

        // Registra como MERMA el stock restante antes de descontinuar el producto.
        if (producto.getStock() != null && producto.getStock() > 0) {
            registrarMovimiento(producto, producto.getStock(), TipoMovimiento.SALIDA, null,
                    MotivoMovimiento.MERMA, ReferenciaTipo.AJUSTE, null,
                    "Producto dado de baja (DESCONTINUADO)");
        }

        producto.setEstado(EstadoProducto.DESCONTINUADO);
        productoDao.save(producto);
    }

    @Transactional
    public ProductoDTO ajustarStock(Long id, int cantidad, String motivo) {
        Producto producto = productoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (cantidad == 0) {
            return converToDTO(producto);
        }

        int stockAnterior = producto.getStock();
        int stockNuevo = stockAnterior + cantidad;

        if (stockNuevo < 0) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + stockAnterior + ", intentas restar: " + Math.abs(cantidad));
        }

        producto.setStock(stockNuevo);
        producto = productoDao.save(producto);

        // Conserva el motivo indicado por el usuario; si no llega, usa una nota genérica.
        String nota = (motivo == null || motivo.isBlank())
                ? String.format("Ajuste manual: %s%d", cantidad > 0 ? "+" : "", cantidad)
                : String.format("Ajuste manual: %s%d — %s", cantidad > 0 ? "+" : "", cantidad, motivo.trim());

        registrarMovimiento(
                producto,
                Math.abs(cantidad),
                TipoMovimiento.AJUSTE,
                stockAnterior,
                MotivoMovimiento.AJUSTE_INVENTARIO,
                ReferenciaTipo.AJUSTE, null,
                nota);

        return converToDTO(producto);
    }

    // Registra un movimiento de stock.
    // Si stockAnteriorKnown llega, se usa como stock previo. Si no, se deduce
    // según el tipo: ENTRADA resta la cantidad al stock nuevo, SALIDA la suma y
    // AJUSTE usa el stock nuevo como anterior.
    // stockAnteriorKnown: valor previo real; si es null, se deduce.
    private void registrarMovimiento(Producto producto, int cantidad, TipoMovimiento tipo,
                                     Integer stockAnteriorKnown, MotivoMovimiento motivo,
                                     ReferenciaTipo referenciaTipo, Long referenciaId, String nota) {
        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProducto(producto);
        movimiento.setCantidad(Math.abs(cantidad));
        int stockNuevo = producto.getStock() == null ? 0 : producto.getStock();
        if (stockAnteriorKnown != null) {
            movimiento.setStockAnterior(stockAnteriorKnown);
        } else if (tipo == TipoMovimiento.ENTRADA) {
            movimiento.setStockAnterior(stockNuevo - Math.abs(cantidad));
        } else if (tipo == TipoMovimiento.SALIDA) {
            movimiento.setStockAnterior(stockNuevo + Math.abs(cantidad));
        } else {
            movimiento.setStockAnterior(stockNuevo);
        }
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setMotivo(motivo);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setObservacion(nota);
        movimiento.setUsuario(securityUtils.getCurrentUser());

        movimientoStockDao.save(movimiento);

    }

    private ProductoDTO converToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : "SIN CATEGORÍA");
        dto.setCategoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setPrecioCompra(producto.getPrecioCompra());
        dto.setStock(producto.getStock());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setSku(producto.getSku());
        dto.setMarca(producto.getMarca());
        dto.setModelo(producto.getModelo());
        dto.setTipoComponente(producto.getTipoComponente());
        dto.setEstado(producto.getEstado());
        dto.setTieneEspecificacion(especificacionResolver.existe(producto.getId(), producto.getTipoComponente()));

        return dto;
    }

    private ProductResponseDTO convertToResponseDTO(Producto producto) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null);
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setSku(producto.getSku());
        dto.setMarca(producto.getMarca());
        dto.setModelo(producto.getModelo());
        dto.setTipoComponente(producto.getTipoComponente());
        dto.setEstado(producto.getEstado());
        dto.setPrecioCompra(producto.getPrecioCompra());
        return dto;
    }

    // Devuelve el SKU indicado o genera uno único cuando llega vacío.
    // El SKU es obligatorio y único en base de datos. El valor generado combina
    // el tipo de componente y el nombre en mayúsculas, con un sufijo numérico
    // si ya existe otro producto con el mismo valor.
    private String resolverSku(String skuSolicitado, Producto producto) {
        if (skuSolicitado != null && !skuSolicitado.isBlank()) {
            return skuSolicitado.trim();
        }
        String prefijo = producto.getTipoComponente() != null
                ? producto.getTipoComponente().name().replace("_", "-")
                : "GEN";
        String base = prefijo + "-" + producto.getNombre().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String candidato = base.length() > 45 ? base.substring(0, 45) : base;

        String sku = candidato;
        int sufijo = 1;
        while (skuYaExiste(sku)) {
            sufijo++;
            sku = candidato + "-" + sufijo;
        }
        return sku;
    }

    private boolean skuYaExiste(String sku) {
        return productoDao.findBySku(sku).isPresent();
    }

    private Producto converCreateToEntity(ProductCreateDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setPrecioCompra(dto.getPrecioCompra() != null ? dto.getPrecioCompra() : java.math.BigDecimal.ZERO);
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setSku(dto.getSku());
        producto.setMarca(dto.getMarca());
        producto.setModelo(dto.getModelo());
        producto.setTipoComponente(dto.getTipoComponente());

        return producto;
    }

    // Valida la relación entre el producto y su especificación 1-1.
    // Todos los valores de TipoComponente tienen especificación asociada, así que
    // no hay tipos inválidos que rechazar; un producto sin tipo queda como
    // categoría libre.
    private void validarTipoComponente(Producto producto) {
        // Sin tipo no hay especificación que completar; el producto queda como
        // categoría libre. Si aparece un tipo sin especificación, este es el punto
        // donde corresponde rechazar la operación.
    }
}
