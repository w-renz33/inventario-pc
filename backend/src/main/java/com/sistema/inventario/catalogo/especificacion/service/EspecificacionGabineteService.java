package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionGabineteDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionGabinete;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionGabineteRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionGabineteService extends EspecificacionBaseService<EspecificacionGabinete, EspecificacionGabineteDTO> {

    private final EspecificacionGabineteRepository repository;

    public EspecificacionGabineteService(EspecificacionGabineteRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionGabineteDTO obtener(Long productoId) {
        EspecificacionGabinete spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Gabinete no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionGabineteDTO crear(Long productoId, EspecificacionGabineteDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.GABINETE);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación Gabinete para este producto");
        }
        EspecificacionGabinete spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionGabineteDTO actualizar(Long productoId, EspecificacionGabineteDTO dto) {
        validarProducto(productoId, TipoComponente.GABINETE);
        EspecificacionGabinete spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Gabinete no encontrada"));
        spec.setFormato(dto.getFormato());
        spec.setCompatibilidadPlaca(dto.getCompatibilidadPlaca());
        spec.setLongitudGpuMaxMm(dto.getLongitudGpuMaxMm());
        spec.setAlturaDisipadorMaxMm(dto.getAlturaDisipadorMaxMm());
        spec.setFuenteSoportada(dto.getFuenteSoportada());
        spec.setVentiladoresIncluidos(dto.getVentiladoresIncluidos());
        spec.setVentiladoresMax(dto.getVentiladoresMax());
        spec.setRadiadorMaxMm(dto.getRadiadorMaxMm());
        spec.setBahias35(dto.getBahias35());
        spec.setBahias25(dto.getBahias25());
        spec.setPuertosUsb(dto.getPuertosUsb());
        spec.setPuertoAudio(dto.getPuertoAudio());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación Gabinete no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionGabineteDTO toDTO(EspecificacionGabinete e) {
        EspecificacionGabineteDTO dto = new EspecificacionGabineteDTO();
        dto.setProductoId(e.getProductoId());
        dto.setFormato(e.getFormato());
        dto.setCompatibilidadPlaca(e.getCompatibilidadPlaca());
        dto.setLongitudGpuMaxMm(e.getLongitudGpuMaxMm());
        dto.setAlturaDisipadorMaxMm(e.getAlturaDisipadorMaxMm());
        dto.setFuenteSoportada(e.getFuenteSoportada());
        dto.setVentiladoresIncluidos(e.getVentiladoresIncluidos());
        dto.setVentiladoresMax(e.getVentiladoresMax());
        dto.setRadiadorMaxMm(e.getRadiadorMaxMm());
        dto.setBahias35(e.getBahias35());
        dto.setBahias25(e.getBahias25());
        dto.setPuertosUsb(e.getPuertosUsb());
        dto.setPuertoAudio(e.getPuertoAudio());
        return dto;
    }

    @Override
    protected EspecificacionGabinete toEntity(EspecificacionGabineteDTO dto, Producto producto) {
        EspecificacionGabinete e = new EspecificacionGabinete();
        e.setProducto(producto);
        e.setFormato(dto.getFormato());
        e.setCompatibilidadPlaca(dto.getCompatibilidadPlaca());
        e.setLongitudGpuMaxMm(dto.getLongitudGpuMaxMm());
        e.setAlturaDisipadorMaxMm(dto.getAlturaDisipadorMaxMm());
        e.setFuenteSoportada(dto.getFuenteSoportada());
        e.setVentiladoresIncluidos(dto.getVentiladoresIncluidos());
        e.setVentiladoresMax(dto.getVentiladoresMax());
        e.setRadiadorMaxMm(dto.getRadiadorMaxMm());
        e.setBahias35(dto.getBahias35());
        e.setBahias25(dto.getBahias25());
        e.setPuertosUsb(dto.getPuertosUsb());
        e.setPuertoAudio(dto.getPuertoAudio());
        return e;
    }
}
