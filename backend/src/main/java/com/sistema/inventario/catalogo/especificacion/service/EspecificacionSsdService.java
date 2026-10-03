package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionSsdDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionSsd;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionSsdRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionSsdService extends EspecificacionBaseService<EspecificacionSsd, EspecificacionSsdDTO> {

    private final EspecificacionSsdRepository repository;

    public EspecificacionSsdService(EspecificacionSsdRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionSsdDTO obtener(Long productoId) {
        EspecificacionSsd spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación SSD no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionSsdDTO crear(Long productoId, EspecificacionSsdDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.SSD);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación SSD para este producto");
        }
        EspecificacionSsd spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionSsdDTO actualizar(Long productoId, EspecificacionSsdDTO dto) {
        validarProducto(productoId, TipoComponente.SSD);
        EspecificacionSsd spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación SSD no encontrada"));
        spec.setCapacidadGb(dto.getCapacidadGb());
        spec.setFormato(dto.getFormato());
        spec.setInterfaz(dto.getInterfaz());
        spec.setTipoNand(dto.getTipoNand());
        spec.setVelocidadLecturaMbps(dto.getVelocidadLecturaMbps());
        spec.setVelocidadEscrituraMbps(dto.getVelocidadEscrituraMbps());
        spec.setTbw(dto.getTbw());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación SSD no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionSsdDTO toDTO(EspecificacionSsd e) {
        EspecificacionSsdDTO dto = new EspecificacionSsdDTO();
        dto.setProductoId(e.getProductoId());
        dto.setCapacidadGb(e.getCapacidadGb());
        dto.setFormato(e.getFormato());
        dto.setInterfaz(e.getInterfaz());
        dto.setTipoNand(e.getTipoNand());
        dto.setVelocidadLecturaMbps(e.getVelocidadLecturaMbps());
        dto.setVelocidadEscrituraMbps(e.getVelocidadEscrituraMbps());
        dto.setTbw(e.getTbw());
        return dto;
    }

    @Override
    protected EspecificacionSsd toEntity(EspecificacionSsdDTO dto, Producto producto) {
        EspecificacionSsd e = new EspecificacionSsd();
        e.setProducto(producto);
        e.setCapacidadGb(dto.getCapacidadGb());
        e.setFormato(dto.getFormato());
        e.setInterfaz(dto.getInterfaz());
        e.setTipoNand(dto.getTipoNand());
        e.setVelocidadLecturaMbps(dto.getVelocidadLecturaMbps());
        e.setVelocidadEscrituraMbps(dto.getVelocidadEscrituraMbps());
        e.setTbw(dto.getTbw());
        return e;
    }
}
