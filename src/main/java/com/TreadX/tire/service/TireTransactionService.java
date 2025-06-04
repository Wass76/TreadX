package com.TreadX.tire.service;

import com.TreadX.tire.entity.TireTransaction;
import com.TreadX.tire.repository.TireTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TireTransactionService {

    private final TireTransactionRepository tireTransactionRepository;

    @Autowired
    public TireTransactionService(TireTransactionRepository tireTransactionRepository) {
        this.tireTransactionRepository = tireTransactionRepository;
    }

    public TireTransaction createTransaction(TireTransaction transaction) {
        return tireTransactionRepository.save(transaction);
    }

    public List<TireTransaction> getAllTransactions() {
        return tireTransactionRepository.findAll();
    }

    public Optional<TireTransaction> getTransactionById(Long id) {
        return tireTransactionRepository.findById(id);
    }

    public TireTransaction updateTransaction(Long id, TireTransaction transactionDetails) {
        TireTransaction transaction = tireTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        
        transaction.setTire(transactionDetails.getTire());
        transaction.setQuantity(transactionDetails.getQuantity());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setTransactionType(transactionDetails.getTransactionType());
        
        return tireTransactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        TireTransaction transaction = tireTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        tireTransactionRepository.delete(transaction);
    }
} 