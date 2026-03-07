package com.TreadX.district.sales.service;

import com.TreadX.district.sales.dto.LeadsHistoryRequestDTO;
import com.TreadX.district.sales.dto.LeadsHistoryResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.entity.LeadsHistory;
import com.TreadX.district.sales.mapper.LeadsHistoryMapper;
import com.TreadX.district.sales.repository.LeadsHistoryRepository;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LeadsHistoryService {

    private final LeadsHistoryRepository leadsHistoryRepository;
    private final LeadsRepository leadsRepository;
    private final UserRepository userRepository;
    private final LeadsHistoryMapper leadsHistoryMapper;

    public LeadsHistoryResponseDTO create(LeadsHistoryRequestDTO requestDTO) {
        Leads lead = leadsRepository.findById(requestDTO.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + requestDTO.getLeadId()));
        User validatedBy = requestDTO.getValidatedById() != null
                ? userRepository.findById(requestDTO.getValidatedById()).orElse(null)
                : null;
        User assignedTo = requestDTO.getAssignedToId() != null
                ? userRepository.findById(requestDTO.getAssignedToId()).orElse(null)
                : null;
        LeadsHistory entity = leadsHistoryMapper.toEntity(requestDTO, lead, validatedBy, assignedTo);
        entity = leadsHistoryRepository.save(entity);
        return leadsHistoryMapper.toResponseDTO(entity);
    }

    public LeadsHistoryResponseDTO getById(Long id) {
        LeadsHistory entity = leadsHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeadsHistory not found with id: " + id));
        return leadsHistoryMapper.toResponseDTO(entity);
    }

    public List<LeadsHistoryResponseDTO> getByLeadId(Long leadId) {
        return leadsHistoryRepository.findByLeadIdOrderByCreatedAtDesc(leadId).stream()
                .map(leadsHistoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
