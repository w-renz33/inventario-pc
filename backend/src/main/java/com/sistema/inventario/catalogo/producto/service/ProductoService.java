package com.sistema.inventario.catalogo.producto.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoDao productoDao;

    private final CategoriaRepository categoriaRepository;

    private final MovimientoStockDao movimientoStockDao;

    private final SecurityUtils securityUtils;

    public ProductoService(ProductoDao productoDao,
                           CategoriaRepository categoriaRepository,
                           MovimientoStockDao movimientoStockDao,
                           SecurityUtils securityUtils) {
        this.productoDao = productoDao;
        this.categoriaRepository = categoriaRepository;
        this.movimientoStockDao = movimientoStockDao;
        this.securityUtils = securityUtils;
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

    @Transactional
    public ProductResponseDTO save(ProductCreateDTO dto) {
        Producto producto = converCreateToEntity(dto);

        // manejar categoria
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
        producto = productoDao.save(producto);

        // registrar movimiento inicial de stock (solo si stock > 0)
        if (producto.getStock() != null && producto.getStock() > 0) {
            registrarMovimiento(producto, producto.getStock(), TipoMovimiento.ENTRADA,
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

        //actualizar campos
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
        if (dto.getPrecioVenta() != null) {
            producto.setPrecioVenta(dto.getPrecioVenta());
        }

        //manejar categoria
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

        //registrar movimiento si cambió el stock (ajuste manual: tipo AJUSTE coherente con el CHECK)
        if (stockAnterior != producto.getStock()) {
            int diferencia = Math.abs(producto.getStock() - stockAnterior);
            registrarMovimiento(producto, diferencia, TipoMovimiento.AJUSTE, MotivoMovimiento.AJUSTE_INVENTARIO,
                    ReferenciaTipo.AJUSTE, null,
                    String.format("Stock actualizado de %d a %d,", stockAnterior, producto.getStock()));
        }

        return converToDTO(producto);

    }

    @Transactional
    public void delete(Long id)  {
        Producto producto = productoDao.findById(id)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        //registrar movimiento de eliminacion (stock a 0)
        if (producto.getStock() != null && producto.getStock() > 0) {
            registrarMovimiento(producto, producto.getStock(), TipoMovimiento.SALIDA, MotivoMovimiento.MERMA,
                    ReferenciaTipo.AJUSTE, null,
                    "Producto eliminado del sistema");
        }
        productoDao.deleteById(id);
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

        registrarMovimiento(
                producto,
                Math.abs(cantidad),
                TipoMovimiento.AJUSTE,
                MotivoMovimiento.AJUSTE_INVENTARIO,
                ReferenciaTipo.AJUSTE, null,
                String.format("Ajustar manual: %s%d", cantidad > 0 ? "+" : "", cantidad));

        return converToDTO(producto);
    }

    private void registrarMovimiento(Producto producto, int cantidad, TipoMovimiento tipo, MotivoMovimiento motivo, ReferenciaTipo referenciaTipo, Long referenciaId, String nota) {
        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProducto(producto);
        movimiento.setCantidad(Math.abs(cantidad));
        int stockNuevo = producto.getStock() == null ? 0 : producto.getStock();
        if (tipo == TipoMovimiento.ENTRADA) {
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

    /**
     * Regla Opcion A: segun tipoComponente corresponde una unica spec 1-1.
     * Las specs se gestionan en sus repositorios; aqui solo se valida el discriminador.
     */
    public void validarTipoComponente(Producto producto) {
        if (producto.getTipoComponente() == null) {
            return;
        }
        switch (producto.getTipoComponente()) {
            case CPU, GPU, RAM, SSD, HDD, PLACA_MADRE, FUENTE, GABINETE -> { /* spec correspondiente */ }
            default -> throw new RuntimeException("Tipo de componente no soportado");
        }
    }
}
