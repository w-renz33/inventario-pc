package com.sistema.inventario.ingreso.service;

import com.sistema.inventario.ingreso.dto.IngresoDTO;
import com.sistema.inventario.ingreso.dto.IngresoDetalleDTO;
import com.sistema.inventario.common.MotivoMovimiento;
import com.sistema.inventario.common.ReferenciaTipo;
import com.sistema.inventario.common.TipoMovimiento;
import com.sistema.inventario.ingreso.entity.Ingreso;
import com.sistema.inventario.ingreso.entity.IngresoDetalle;
import com.sistema.inventario.movimiento.entity.MovimientoStock;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.proveedor.entity.Proveedor;
import com.sistema.inventario.ingreso.repository.IngresoDao;
import com.sistema.inventario.movimiento.repository.MovimientoStockDao;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.proveedor.repository.ProveedorRepository;
import com.sistema.inventario.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.sistema.inventario.common.EstadoIngreso;

@Service
public class IngresoService {

    /** Tasa de IGV aplicada al total. El frontend usa la misma constante. */
    private static final BigDecimal TASA_IGV = new BigDecimal("0.18");

    private final IngresoDao ingresoDao;
    private final ProductoDao productoDao;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoStockDao movimientoStockDao;
    private final SecurityUtils securityUtils;

    public IngresoService(IngresoDao ingresoDao,
                          ProductoDao productoDao,
                          ProveedorRepository proveedorRepository,
                          MovimientoStockDao movimientoStockDao,
                          SecurityUtils securityUtils) {
        this.ingresoDao = ingresoDao;
        this.productoDao = productoDao;
        this.proveedorRepository = proveedorRepository;
        this.movimientoStockDao = movimientoStockDao;
        this.securityUtils = securityUtils;
    }

    public List<IngresoDTO> findAll() {
        return ingresoDao.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public IngresoDTO findById(Long id) {
        return toDTO(ingresoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado")));
    }

    @Transactional
    public IngresoDTO registrar(IngresoDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        ingresoDao.findByTipoDocumentoAndNumeroDocumentoAndProveedorId(
                dto.getTipoDocumento(), dto.getNumeroDocumento(), proveedor.getId()).ifPresent(i -> {
            throw new RuntimeException("Documento duplicado para este proveedor");
        });

        Ingreso ingreso = new Ingreso();
        ingreso.setTipoDocumento(dto.getTipoDocumento());
        ingreso.setNumeroDocumento(dto.getNumeroDocumento());
        ingreso.setFecha(dto.getFecha());
        ingreso.setProveedor(proveedor);
        ingreso.setObservacion(dto.getObservacion());
        ingreso.setEstado(EstadoIngreso.REGISTRADO);
        ingreso.setUsuario(securityUtils.getCurrentUser());

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new RuntimeException("El ingreso debe tener al menos un producto en el detalle");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<MovimientoStock> movimientosDelIngreso = new ArrayList<>();
        for (IngresoDetalleDTO d : dto.getDetalles()) {
            if (d.getCantidad() == null || d.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad debe ser > 0");
            }
            if (d.getPrecioUnitario() == null || d.getPrecioUnitario().signum() < 0) {
                throw new RuntimeException("El precio unitario es obligatorio y no puede ser negativo");
            }
            Producto producto = productoDao.findById(d.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + d.getProductoId()));

            IngresoDetalle detalle = new IngresoDetalle();
            detalle.setIngreso(ingreso);
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setSubtotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            ingreso.getDetalles().add(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());

            // ENTRADA de stock + trazabilidad
            int anterior = producto.getStock() == null ? 0 : producto.getStock();
            producto.setStock(anterior + d.getCantidad());
            productoDao.save(producto);

            MovimientoStock mov = new MovimientoStock();
            mov.setProducto(producto);
            mov.setTipoMovimiento(TipoMovimiento.ENTRADA);
            mov.setMotivo(MotivoMovimiento.COMPRA);
            mov.setCantidad(d.getCantidad());
            mov.setStockAnterior(anterior);
            mov.setStockNuevo(producto.getStock());
            mov.setCostoUnitario(d.getPrecioUnitario());
            mov.setReferenciaTipo(ReferenciaTipo.INGRESO);
            // referenciaId se completa tras guardar el ingreso
            mov.setUsuario(securityUtils.getCurrentUser());
            mov.setObservacion("Ingreso " + dto.getTipoDocumento() + " " + dto.getNumeroDocumento());
            movimientosDelIngreso.add(mov);
        }

        ingreso.setSubtotal(subtotal);
        ingreso.setIgv(subtotal.multiply(TASA_IGV));
        ingreso.setTotal(subtotal.add(ingreso.getIgv()));
        ingreso = ingresoDao.save(ingreso);

            // El movimiento se guardo antes que el ingreso porque necesitaba un id
            // que todavia no existia. Ahora se enlaza usando la referencia directa a
        // cada entidad: antes se re-consultaba el kardex del producto y se
        // tomaba el primer movimiento sin referencia, lo que era una consulta
        // por linea de detalle y podia enlazar al movimiento equivocado si el
        // mismo producto aparecia mas de una vez.
        for (MovimientoStock mov : movimientosDelIngreso) {
            mov.setReferenciaId(ingreso.getId());
            movimientoStockDao.save(mov);
        }

        return toDTO(ingreso);
    }

    @Transactional
    public void anular(Long id) {
        Ingreso ingreso = ingresoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        if (EstadoIngreso.ANULADO.equals(ingreso.getEstado())) {
            return;
        }
        // guarda previa al CHECK: la reversa no puede dejar stock negativo
        for (IngresoDetalle detalle : ingreso.getDetalles()) {
            int stockActual = detalle.getProducto().getStock() == null ? 0 : detalle.getProducto().getStock();
            if (stockActual < detalle.getCantidad()) {
                throw new RuntimeException("No se puede anular: stock insuficiente en '"
                        + detalle.getProducto().getNombre() + "' (actual " + stockActual
                        + ", a reversar " + detalle.getCantidad() + ")");
            }
        }
        // reversa simple: resta stock y registra SALIDA por devolucion
        for (IngresoDetalle detalle : ingreso.getDetalles()) {
            Producto producto = detalle.getProducto();
            int anterior = producto.getStock();
            producto.setStock(anterior - detalle.getCantidad());
            productoDao.save(producto);

            MovimientoStock mov = new MovimientoStock();
            mov.setProducto(producto);
            mov.setTipoMovimiento(TipoMovimiento.SALIDA);
            mov.setMotivo(MotivoMovimiento.DEVOLUCION);
            mov.setCantidad(detalle.getCantidad());
            mov.setStockAnterior(anterior);
            mov.setStockNuevo(producto.getStock());
            mov.setReferenciaTipo(ReferenciaTipo.INGRESO);
            mov.setReferenciaId(ingreso.getId());
            mov.setUsuario(securityUtils.getCurrentUser());
            mov.setObservacion("Anulacion de ingreso #" + ingreso.getId());
            movimientoStockDao.save(mov);
        }
        ingreso.setEstado(EstadoIngreso.ANULADO);
        ingresoDao.save(ingreso);
    }

    private IngresoDTO toDTO(Ingreso ingreso) {
        IngresoDTO dto = new IngresoDTO();
        dto.setId(ingreso.getId());
        dto.setTipoDocumento(ingreso.getTipoDocumento());
        dto.setNumeroDocumento(ingreso.getNumeroDocumento());
        dto.setFecha(ingreso.getFecha());
        dto.setProveedorId(ingreso.getProveedor().getId());
        dto.setProveedorNombre(ingreso.getProveedor().getNombre());
        dto.setSubtotal(ingreso.getSubtotal());
        dto.setIgv(ingreso.getIgv());
        dto.setTotal(ingreso.getTotal());
        dto.setObservacion(ingreso.getObservacion());
        dto.setEstado(ingreso.getEstado());
        if (ingreso.getDetalles() != null) {
            dto.setDetalles(ingreso.getDetalles().stream().map(d -> {
                IngresoDetalleDTO dd = new IngresoDetalleDTO();
                dd.setId(d.getId());
                dd.setProductoId(d.getProducto().getId());
                dd.setProductoNombre(d.getProducto().getNombre());
                dd.setCantidad(d.getCantidad());
                dd.setPrecioUnitario(d.getPrecioUnitario());
                dd.setSubtotal(d.getSubtotal());
                return dd;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
