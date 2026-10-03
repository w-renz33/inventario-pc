package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionFuenteDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionFuente;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionFuenteRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionFuenteService extends EspecificacionBaseService<EspecificacionFuente, EspecificacionFuenteDTO> {

    private final EspecificacionFuenteRepository repository;

    public EspecificacionFuenteService(EspecificacionFuenteRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionFuenteDTO obtener(Long productoId) {
        EspecificacionFuente spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Fuente no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionFuenteDTO crear(Long productoId, EspecificacionFuenteDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.FUENTE);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación Fuente para este producto");
        }
        EspecificacionFuente spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionFuenteDTO actualizar(Long productoId, EspecificacionFuenteDTO dto) {
        validarProducto(productoId, TipoComponente.FUENTE);
        EspecificacionFuente spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Fuente no encontrada"));
        spec.setPotenciaW(dto.getPotenciaW());
        spec.setCertificacion80Plus(dto.getCertificacion80Plus());
        spec.setFormato(dto.getFormato());
        spec.setModularidad(dto.getModularidad());
        spec.setVentiladorMm(dto.getVentiladorMm());
        spec.setConectorAtx(dto.getConectorAtx());
        spec.setConectorCpu(dto.getConectorCpu());
        spec.setConectorPcie(dto.getConectorPcie());
        spec.setConectorSata(dto.getConectorSata());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación Fuente no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionFuenteDTO toDTO(EspecificacionFuente e) {
        EspecificacionFuenteDTO dto = new EspecificacionFuenteDTO();
        dto.setProductoId(e.getProductoId());
        dto.setPotenciaW(e.getPotenciaW());
        dto.setCertificacion80Plus(e.getCertificacion80Plus());
        dto.setFormato(e.getFormato());
        dto.setModularidad(e.getModularidad());
        dto.setVentiladorMm(e.getVentiladorMm());
        dto.setConectorAtx(e.getConectorAtx());
        dto.setConectorCpu(e.getConectorCpu());
        dto.setConectorPcie(e.getConectorPcie());
        dto.setConectorSata(e.getConectorSata());
        return dto;
    }

    @Override
    protected EspecificacionFuente toEntity(EspecificacionFuenteDTO dto, Producto producto) {
        EspecificacionFuente e = new EspecificacionFuente();
        e.setProducto(producto);
        e.setPotenciaW(dto.getPotenciaW());
        e.setCertificacion80Plus(dto.getCertificacion80Plus());
        e.setFormato(dto.getFormato());
        e.setModularidad(dto.getModularidad());
        e.setVentiladorMm(dto.getVentiladorMm());
        e.setConectorAtx(dto.getConectorAtx());
        e.setConectorCpu(dto.getConectorCpu());
        e.setConectorPcie(dto.getConectorPcie());
        e.setConectorSata(dto.getConectorSata());
        return e;
    }
}