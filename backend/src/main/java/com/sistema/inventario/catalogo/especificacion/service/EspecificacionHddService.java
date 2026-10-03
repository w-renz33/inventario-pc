package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionHddDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionHdd;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionHddRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionHddService extends EspecificacionBaseService<EspecificacionHdd, EspecificacionHddDTO> {

    private final EspecificacionHddRepository repository;

    public EspecificacionHddService(EspecificacionHddRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionHddDTO obtener(Long productoId) {
        EspecificacionHdd spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación HDD no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionHddDTO crear(Long productoId, EspecificacionHddDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.HDD);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación HDD para este producto");
        }
        EspecificacionHdd spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionHddDTO actualizar(Long productoId, EspecificacionHddDTO dto) {
        validarProducto(productoId, TipoComponente.HDD);
        EspecificacionHdd spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación HDD no encontrada"));
        spec.setCapacidadGb(dto.getCapacidadGb());
        spec.setFormato(dto.getFormato());
        spec.setInterfaz(dto.getInterfaz());
        spec.setRpm(dto.getRpm());
        spec.setCacheMb(dto.getCacheMb());
        spec.setVelocidadTransferenciaMbps(dto.getVelocidadTransferenciaMbps());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación HDD no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionHddDTO toDTO(EspecificacionHdd e) {
        EspecificacionHddDTO dto = new EspecificacionHddDTO();
        dto.setProductoId(e.getProductoId());
        dto.setCapacidadGb(e.getCapacidadGb());
        dto.setFormato(e.getFormato());
        dto.setInterfaz(e.getInterfaz());
        dto.setRpm(e.getRpm());
        dto.setCacheMb(e.getCacheMb());
        dto.setVelocidadTransferenciaMbps(e.getVelocidadTransferenciaMbps());
        return dto;
    }

    @Override
    protected EspecificacionHdd toEntity(EspecificacionHddDTO dto, Producto producto) {
        EspecificacionHdd e = new EspecificacionHdd();
        e.setProducto(producto);
        e.setCapacidadGb(dto.getCapacidadGb());
        e.setFormato(dto.getFormato());
        e.setInterfaz(dto.getInterfaz());
        e.setRpm(dto.getRpm());
        e.setCacheMb(dto.getCacheMb());
        e.setVelocidadTransferenciaMbps(dto.getVelocidadTransferenciaMbps());
        return e;
    }
}
