package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionPlacaMadreDTO;
import com.sistema.inventario.catalogo.especificacion.entity.EspecificacionPlacaMadre;
import com.sistema.inventario.catalogo.especificacion.repository.EspecificacionPlacaMadreRepository;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EspecificacionPlacaMadreService extends EspecificacionBaseService<EspecificacionPlacaMadre, EspecificacionPlacaMadreDTO> {

    private final EspecificacionPlacaMadreRepository repository;

    public EspecificacionPlacaMadreService(EspecificacionPlacaMadreRepository repository, ProductoDao productoDao) {
        super(productoDao);
        this.repository = repository;
    }

    public EspecificacionPlacaMadreDTO obtener(Long productoId) {
        EspecificacionPlacaMadre spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Placa Madre no encontrada"));
        return toDTO(spec);
    }

    @Transactional
    public EspecificacionPlacaMadreDTO crear(Long productoId, EspecificacionPlacaMadreDTO dto) {
        Producto producto = validarProducto(productoId, TipoComponente.PLACA_MADRE);
        if (repository.existsById(productoId)) {
            throw new RuntimeException("Ya existe una especificación Placa Madre para este producto");
        }
        EspecificacionPlacaMadre spec = toEntity(dto, producto);
        return toDTO(repository.save(spec));
    }

    @Transactional
    public EspecificacionPlacaMadreDTO actualizar(Long productoId, EspecificacionPlacaMadreDTO dto) {
        validarProducto(productoId, TipoComponente.PLACA_MADRE);
        EspecificacionPlacaMadre spec = repository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Especificación Placa Madre no encontrada"));
        spec.setSocket(dto.getSocket());
        spec.setChipset(dto.getChipset());
        spec.setFormato(dto.getFormato());
        spec.setTipoMemoria(dto.getTipoMemoria());
        spec.setSlotsRam(dto.getSlotsRam());
        spec.setMemoriaMaxGb(dto.getMemoriaMaxGb());
        spec.setSlotsM2(dto.getSlotsM2());
        spec.setSlotsPcie(dto.getSlotsPcie());
        spec.setPuertosSata(dto.getPuertosSata());
        spec.setPuertoLan(dto.getPuertoLan());
        spec.setWifi(dto.getWifi());
        spec.setBluetooth(dto.getBluetooth());
        return toDTO(repository.save(spec));
    }

    @Transactional
    public void eliminar(Long productoId) {
        if (!repository.existsById(productoId)) {
            throw new RuntimeException("Especificación Placa Madre no encontrada");
        }
        repository.deleteById(productoId);
    }

    @Override
    protected EspecificacionPlacaMadreDTO toDTO(EspecificacionPlacaMadre e) {
        EspecificacionPlacaMadreDTO dto = new EspecificacionPlacaMadreDTO();
        dto.setProductoId(e.getProductoId());
        dto.setSocket(e.getSocket());
        dto.setChipset(e.getChipset());
        dto.setFormato(e.getFormato());
        dto.setTipoMemoria(e.getTipoMemoria());
        dto.setSlotsRam(e.getSlotsRam());
        dto.setMemoriaMaxGb(e.getMemoriaMaxGb());
        dto.setSlotsM2(e.getSlotsM2());
        dto.setSlotsPcie(e.getSlotsPcie());
        dto.setPuertosSata(e.getPuertosSata());
        dto.setPuertoLan(e.getPuertoLan());
        dto.setWifi(e.getWifi());
        dto.setBluetooth(e.getBluetooth());
        return dto;
    }

    @Override
    protected EspecificacionPlacaMadre toEntity(EspecificacionPlacaMadreDTO dto, Producto producto) {
        EspecificacionPlacaMadre e = new EspecificacionPlacaMadre();
        e.setProducto(producto);
        e.setSocket(dto.getSocket());
        e.setChipset(dto.getChipset());
        e.setFormato(dto.getFormato());
        e.setTipoMemoria(dto.getTipoMemoria());
        e.setSlotsRam(dto.getSlotsRam());
        e.setMemoriaMaxGb(dto.getMemoriaMaxGb());
        e.setSlotsM2(dto.getSlotsM2());
        e.setSlotsPcie(dto.getSlotsPcie());
        e.setPuertosSata(dto.getPuertosSata());
        e.setPuertoLan(dto.getPuertoLan());
        e.setWifi(dto.getWifi());
        e.setBluetooth(dto.getBluetooth());
        return e;
    }
}