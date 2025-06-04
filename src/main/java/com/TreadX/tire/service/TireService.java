package com.TreadX.tire.service;

import com.TreadX.tire.entity.Tire;
import com.TreadX.tire.dto.TireDTO;
import com.TreadX.tire.repository.TireRepository;
import com.TreadX.utils.exception.RequestNotValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TireService {
    private final TireRepository tireRepository;

    @Transactional
    public TireDTO createTire(TireDTO tireDTO) {
        Tire tire = new Tire();
        tire.setTireType(tireDTO.getTireType());
        tire.setTreadWidth(tireDTO.getTreadWidth());
        tire.setAspectRatio(tireDTO.getAspectRatio());
        tire.setConstruction(tireDTO.getConstruction());
        tire.setDiameter(tireDTO.getDiameter());
        
        Tire savedTire = tireRepository.save(tire);
        return convertToDTO(savedTire);
    }

    public List<TireDTO> getAllTires() {
        return tireRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TireDTO getTireById(Long id) {
        Tire tire = tireRepository.findById(id)
                .orElseThrow(() -> new RequestNotValidException("Tire not found with id: " + id));
        return convertToDTO(tire);
    }

    @Transactional
    public TireDTO updateTire(Long id, TireDTO tireDTO) {
        Tire existingTire = tireRepository.findById(id)
                .orElseThrow(() -> new RequestNotValidException("Tire not found with id: " + id));

        existingTire.setTireType(tireDTO.getTireType());
        existingTire.setTreadWidth(tireDTO.getTreadWidth());
        existingTire.setAspectRatio(tireDTO.getAspectRatio());
        existingTire.setConstruction(tireDTO.getConstruction());
        existingTire.setDiameter(tireDTO.getDiameter());

        Tire updatedTire = tireRepository.save(existingTire);
        return convertToDTO(updatedTire);
    }

    @Transactional
    public void deleteTire(Long id) {
        if (!tireRepository.existsById(id)) {
            throw new RequestNotValidException("Tire not found with id: " + id);
        }
        tireRepository.deleteById(id);
    }

    private TireDTO convertToDTO(Tire tire) {
        return TireDTO.builder()
                .id(tire.getId())
                .tireType(tire.getTireType())
                .treadWidth(tire.getTreadWidth())
                .aspectRatio(tire.getAspectRatio())
                .construction(tire.getConstruction())
                .diameter(tire.getDiameter())
                .build();
    }
} 