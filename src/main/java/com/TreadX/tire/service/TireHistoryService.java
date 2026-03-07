package com.TreadX.tire.service;

import com.TreadX.tire.entity.TireHistory;
import com.TreadX.tire.repository.TireHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TireHistoryService {

    private final TireHistoryRepository tireHistoryRepository;

    @Autowired
    public TireHistoryService(TireHistoryRepository tireHistoryRepository) {
        this.tireHistoryRepository = tireHistoryRepository;
    }

    public TireHistory createHistory(TireHistory history) {
        return tireHistoryRepository.save(history);
    }

    public List<TireHistory> getAllTransactions() {
        return tireHistoryRepository.findAll();
    }

    public Optional<TireHistory> getHistoryById(Long id) {
        return tireHistoryRepository.findById(id);
    }

    public TireHistory updateHistory(Long id, TireHistory historyDetails) {
        TireHistory history = tireHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History not found with id: " + id));
        
        history.setTire(historyDetails.getTire());
        history.setQuantity(historyDetails.getQuantity());
        history.setHistoryDate(historyDetails.getHistoryDate());
        history.setHistoryType(historyDetails.getHistoryType());
        
        return tireHistoryRepository.save(history);
    }

    public void deleteTransaction(Long id) {
        TireHistory history = tireHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        tireHistoryRepository.delete(history);
    }
} 