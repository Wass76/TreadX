package com.TreadX.tire.service;


import com.TreadX.tire.entity.DealerPackages;
import com.TreadX.tire.repository.DealerPackagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DealerPackagesService {

    private final DealerPackagesRepository dealerPackagesRepository;

    @Autowired
    public DealerPackagesService(DealerPackagesRepository dealerPackagesRepository) {
        this.dealerPackagesRepository = dealerPackagesRepository;
    }

    public DealerPackages createDealerPackage(DealerPackages dealerPackage) {
        return dealerPackagesRepository.save(dealerPackage);
    }

    public List<DealerPackages> getAllDealerPackages() {
        return dealerPackagesRepository.findAll();
    }

    public Optional<DealerPackages> getDealerPackageById(Long id) {
        return dealerPackagesRepository.findById(id);
    }

    public DealerPackages updateDealerPackage(Long id, DealerPackages dealerPackageDetails) {
        DealerPackages dealerPackage = dealerPackagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer Package not found with id: " + id));
        
        dealerPackage.setDealer(dealerPackageDetails.getDealer());
        dealerPackage.setAPackage(dealerPackageDetails.getAPackage());
        dealerPackage.setPrice(dealerPackageDetails.getPrice());
        
        return dealerPackagesRepository.save(dealerPackage);
    }

    public void deleteDealerPackage(Long id) {
        DealerPackages dealerPackage = dealerPackagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer Package not found with id: " + id));
        dealerPackagesRepository.delete(dealerPackage);
    }
} 