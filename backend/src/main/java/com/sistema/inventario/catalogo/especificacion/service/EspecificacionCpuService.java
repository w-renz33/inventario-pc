package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionCpuDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionCpu;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionCpuRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionCpuService extends EspecificacionBaseService<EspecificacionCpu, EspecificacionCpuDTO> {

    private final EspecificacionCpuRepository repository;

    public EspecificacionCpuService(EspecificacionCpuRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionCpuDTO obtener(Long productoId) {
        EspecificacionCpu spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación CPU no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionCpuDTO crear(Long productoId, EspecificacionCpuDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.CPU);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación CPU para este producto");
        }
        EspecificacionCpu spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionCpuDTO actualizar(Long productoId, EspecificacionCpuDTO dto) {
        validarProducto(productoId, TipoComponente.CPU);
        EspecificacionCpu spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación CPU no encontrada"));
        spec.setSocket(dto.getSocket());
        spec.setArquitectura(dto.getArquitectura());
        spec.setNucleos(dto.getNucleos());
        spec.setHilos(dto.getHilos());
        spec.setFrecuenciaBaseGhz(dto.getFrecuenciaBaseGhz());
        spec.setFrecuenciaBoostGhz(dto.getFrecuenciaBoostGhz());
        spec.setCacheMb(dto.getCacheMb());
        spec.setConsumoTdpW(dto.getConsumoTdpW());
        spec.setGraficaIntegrada(dto.getGraficaIntegrada());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación CPU no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionCpuDTO toDTO(EspecificacionCpu e) {
        EspecificacionCpuDTO dto = new EspecificacionCpuDTO();
        dto.setProductoId(e.getProductoId());
        dto.setSocket(e.getSocket());
        dto.setArquitectura(e.getArquitectura());
        dto.setNucleos(e.getNucleos());
        dto.setHilos(e.getHilos());
        dto.setFrecuenciaBaseGhz(e.getFrecuenciaBaseGhz());
        dto.setFrecuenciaBoostGhz(e.getFrecuenciaBoostGhz());
        dto.setCacheMb(e.getCacheMb());
        dto.setConsumoTdpW(e.getConsumoTdpW());
        dto.setGraficaIntegrada(e.getGraficaIntegrada());
        return dto;
    }

    @Override
    protected EspecificacionCpu toEntity(EspecificacionCpuDTO dto, Producto producto) {
        EspecificacionCpu e = new EspecificacionCpu();
        e.setProducto(producto);
        e.setSocket(dto.getSocket());
        e.setArquitectura(dto.getArquitectura());
        e.setNucleos(dto.getNucleos());
        e.setHilos(dto.getHilos());
        e.setFrecuenciaBaseGhz(dto.getFrecuenciaBaseGhz());
        e.setFrecuenciaBoostGhz(dto.getFrecuenciaBoostGhz());
        e.setCacheMb(dto.getCacheMb());
        e.setConsumoTdpW(dto.getConsumoTdpW());
        e.setGraficaIntegrada(dto.getGraficaIntegrada());
        return e;
    }
}
