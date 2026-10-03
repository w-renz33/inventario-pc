package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.especificacion.repository.*;
import com.sistema.inventario.common.TipoComponente;

import org.springframework.stereotype.Component;

/*
 * Unico punto donde se acoplan los 8 tipos de especificacion.
 * Permite responder "existe spec para este producto?" sin ensuciar
 * ProductoService con 8 repositorios.
 */


@Component
public class EspecificacionResolver {

    private final EspecificacionCpuRepository cpuRepository;
    private final EspecificacionGpuRepository gpuRepository;
    private final EspecificacionRamRepository ramRepository;
    private final EspecificacionSsdRepository ssdRepository;
    private final EspecificacionHddRepository hddRepository;
    private final EspecificacionPlacaMadreRepository placaMadreRepository;
    private final EspecificacionFuenteRepository fuenteRepository;
    private final EspecificacionGabineteRepository gabineteRepository;

    public EspecificacionResolver(EspecificacionCpuRepository cpuRepository,
                                  EspecificacionGpuRepository gpuRepository,
                                  EspecificacionRamRepository ramRepository,
                                  EspecificacionSsdRepository ssdRepository,
                                  EspecificacionHddRepository hddRepository,
                                  EspecificacionPlacaMadreRepository placaMadreRepository,
                                  EspecificacionFuenteRepository fuenteRepository,
                                  EspecificacionGabineteRepository gabineteRepository) {
        this.cpuRepository = cpuRepository;
        this.gpuRepository = gpuRepository;
        this.ramRepository = ramRepository;
        this.ssdRepository = ssdRepository;
        this.hddRepository = hddRepository;
        this.placaMadreRepository = placaMadreRepository;
        this.fuenteRepository = fuenteRepository;
        this.gabineteRepository = gabineteRepository;
    }

    public boolean existe(Long productoId, TipoComponente tipo) {
        if (productoId == null || tipo == null) {
            return false;
        }
        return switch (tipo) {
            case CPU -> cpuRepository.existsById(productoId);
            case GPU -> gpuRepository.existsById(productoId);
            case RAM -> ramRepository.existsById(productoId);
            case SSD -> ssdRepository.existsById(productoId);
            case HDD -> hddRepository.existsById(productoId);
            case PLACA_MADRE -> placaMadreRepository.existsById(productoId);
            case FUENTE -> fuenteRepository.existsById(productoId);
            case GABINETE -> gabineteRepository.existsById(productoId);
        };
    }

}
