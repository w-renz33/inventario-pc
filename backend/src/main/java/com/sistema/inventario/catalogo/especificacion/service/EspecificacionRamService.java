package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionRamDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionRam;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionRamRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionRamService extends EspecificacionBaseService<EspecificacionRam, EspecificacionRamDTO> {

    private final EspecificacionRamRepository repository;

    public EspecificacionRamService(EspecificacionRamRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionRamDTO obtener(Long productoId) {
        EspecificacionRam spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación RAM no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionRamDTO crear(Long productoId, EspecificacionRamDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.RAM);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación RAM para este producto");
        }
        EspecificacionRam spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionRamDTO actualizar(Long productoId, EspecificacionRamDTO dto) {
        validarProducto(productoId, TipoComponente.RAM);
        EspecificacionRam spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación RAM no encontrada"));
        spec.setCapacidadGb(dto.getCapacidadGb());
        spec.setTipoMemoria(dto.getTipoMemoria());
        spec.setFrecuenciaMhz(dto.getFrecuenciaMhz());
        spec.setLatencia(dto.getLatencia());
        spec.setVoltaje(dto.getVoltaje());
        spec.setFormato(dto.getFormato());
        spec.setEcc(dto.getEcc());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación RAM no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionRamDTO toDTO(EspecificacionRam e) {
        EspecificacionRamDTO dto = new EspecificacionRamDTO();
        dto.setProductoId(e.getProductoId());
        dto.setCapacidadGb(e.getCapacidadGb());
        dto.setTipoMemoria(e.getTipoMemoria());
        dto.setFrecuenciaMhz(e.getFrecuenciaMhz());
        dto.setLatencia(e.getLatencia());
        dto.setVoltaje(e.getVoltaje());
        dto.setFormato(e.getFormato());
        dto.setEcc(e.getEcc());
        return dto;
    }

    @Override
    protected EspecificacionRam toEntity(EspecificacionRamDTO dto, Producto producto) {
        EspecificacionRam e = new EspecificacionRam();
        e.setProducto(producto);
        e.setCapacidadGb(dto.getCapacidadGb());
        e.setTipoMemoria(dto.getTipoMemoria());
        e.setFrecuenciaMhz(dto.getFrecuenciaMhz());
        e.setLatencia(dto.getLatencia());
        e.setVoltaje(dto.getVoltaje());
        e.setFormato(dto.getFormato());
        e.setEcc(dto.getEcc());
        return e;
    }
}
