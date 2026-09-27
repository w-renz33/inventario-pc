package com.sistema.inventario.proveedor.service;

import com.sistema.inventario.proveedor.dto.ProveedorDTO;
import com.sistema.inventario.proveedor.entity.Proveedor;
import com.sistema.inventario.proveedor.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<ProveedorDTO> findAll() {
        return proveedorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        return convertToDTO(proveedor);
    }

    public ProveedorDTO save(ProveedorDTO dto) {
        Proveedor proveedor = convertToEntity(dto);
        proveedor = proveedorRepository.save(proveedor);
        return convertToDTO(proveedor);
    }

    public ProveedorDTO update(Long id, ProveedorDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setNombre(dto.getNombre());
        proveedor.setRuc(dto.getRuc());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setContactoNombre(dto.getContactoNombre());
        proveedor.setContactoTelefono(dto.getContactoTelefono());
        proveedor.setActivo(dto.getActivo());

        proveedor = proveedorRepository.save(proveedor);
        return convertToDTO(proveedor);
    }

    public void delete(Long id) {
        proveedorRepository.deleteById(id);
    }

    private ProveedorDTO convertToDTO(Proveedor p) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setRuc(p.getRuc());
        dto.setTelefono(p.getTelefono());
        dto.setEmail(p.getEmail());
        dto.setDireccion(p.getDireccion());
        dto.setContactoNombre(p.getContactoNombre());
        dto.setContactoTelefono(p.getContactoTelefono());
        dto.setActivo(p.getActivo());
        return dto;
    }

    private Proveedor convertToEntity(ProveedorDTO dto) {
        Proveedor p = new Proveedor();
        p.setId(dto.getId());
        p.setNombre(dto.getNombre());
        p.setRuc(dto.getRuc());
        p.setTelefono(dto.getTelefono());
        p.setEmail(dto.getEmail());
        p.setDireccion(dto.getDireccion());
        p.setContactoNombre(dto.getContactoNombre());
        p.setContactoTelefono(dto.getContactoTelefono());
        p.setActivo(dto.getActivo());
        return p;
    }

}
