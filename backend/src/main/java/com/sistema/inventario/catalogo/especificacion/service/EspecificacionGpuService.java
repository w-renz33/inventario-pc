package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionGpuDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionGpu;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionGpuRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionGpuService extends EspecificacionBaseService<EspecificacionGpu, EspecificacionGpuDTO> {

    private final EspecificacionGpuRepository repository;

    public EspecificacionGpuService(EspecificacionGpuRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionGpuDTO obtener(Long productoId) {
        EspecificacionGpu spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación GPU no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionGpuDTO crear(Long productoId, EspecificacionGpuDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.GPU);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación GPU para este producto");
        }
        EspecificacionGpu spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionGpuDTO actualizar(Long productoId, EspecificacionGpuDTO dto) {
        validarProducto(productoId, TipoComponente.GPU);
        EspecificacionGpu spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación GPU no encontrada"));
        spec.setChip(dto.getChip());
        spec.setArquitectura(dto.getArquitectura());
        spec.setVramGb(dto.getVramGb());
        spec.setTipoMemoria(dto.getTipoMemoria());
        spec.setBusMemoriaBits(dto.getBusMemoriaBits());
        spec.setFrecuenciaBaseMhz(dto.getFrecuenciaBaseMhz());
        spec.setFrecuenciaBoostMhz(dto.getFrecuenciaBoostMhz());
        spec.setConsumoTdpW(dto.getConsumoTdpW());
        spec.setInterfaz(dto.getInterfaz());
        spec.setHdmi(dto.getHdmi());
        spec.setDisplayport(dto.getDisplayport());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación GPU no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionGpuDTO toDTO(EspecificacionGpu e) {
        EspecificacionGpuDTO dto = new EspecificacionGpuDTO();
        dto.setProductoId(e.getProductoId());
        dto.setChip(e.getChip());
        dto.setArquitectura(e.getArquitectura());
        dto.setVramGb(e.getVramGb());
        dto.setTipoMemoria(e.getTipoMemoria());
        dto.setBusMemoriaBits(e.getBusMemoriaBits());
        dto.setFrecuenciaBaseMhz(e.getFrecuenciaBaseMhz());
        dto.setFrecuenciaBoostMhz(e.getFrecuenciaBoostMhz());
        dto.setConsumoTdpW(e.getConsumoTdpW());
        dto.setInterfaz(e.getInterfaz());
        dto.setHdmi(e.getHdmi());
        dto.setDisplayport(e.getDisplayport());
        return dto;
    }

    @Override
    protected EspecificacionGpu toEntity(EspecificacionGpuDTO dto, Producto producto) {
        EspecificacionGpu e = new EspecificacionGpu();
        e.setProducto(producto);
        e.setChip(dto.getChip());
        e.setArquitectura(dto.getArquitectura());
        e.setVramGb(dto.getVramGb());
        e.setTipoMemoria(dto.getTipoMemoria());
        e.setBusMemoriaBits(dto.getBusMemoriaBits());
        e.setFrecuenciaBaseMhz(dto.getFrecuenciaBaseMhz());
        e.setFrecuenciaBoostMhz(dto.getFrecuenciaBoostMhz());
        e.setConsumoTdpW(dto.getConsumoTdpW());
        e.setInterfaz(dto.getInterfaz());
        e.setHdmi(dto.getHdmi());
        e.setDisplayport(dto.getDisplayport());
        return e;
    }
}
