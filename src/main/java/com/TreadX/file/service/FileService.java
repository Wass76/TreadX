package com.TreadX.file.service;

import com.TreadX.file.dto.FileRequestDTO;
import com.TreadX.file.dto.FileResponseDTO;
import com.TreadX.file.entity.File;
import com.TreadX.file.mapper.FileMapper;
import com.TreadX.file.repository.FileRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    public FileResponseDTO create(FileRequestDTO requestDTO) {
        File file = fileMapper.toEntity(requestDTO);
        file = fileRepository.save(file);
        return fileMapper.toResponseDTO(file);
    }

    public FileResponseDTO getById(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
        return fileMapper.toResponseDTO(file);
    }

    public File getEntityById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }

    public void deleteById(Long id) {
        if (!fileRepository.existsById(id)) {
            throw new ResourceNotFoundException("File not found with id: " + id);
        }
        fileRepository.deleteById(id);
    }
}
