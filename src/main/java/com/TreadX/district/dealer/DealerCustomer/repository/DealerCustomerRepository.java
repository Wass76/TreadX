package com.TreadX.district.dealer.DealerCustomer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TreadX.district.dealer.DealerCustomer.entity.DealerCustomer;
import com.TreadX.district.dealer.entity.Dealer;

import java.util.List;

@Repository
public interface DealerCustomerRepository extends JpaRepository<DealerCustomer, Long> {

    List<DealerCustomer> findByDealer(Dealer dealer);

    Page<DealerCustomer> findByDealerId(Long dealerId, Pageable pageable);

    @Query("SELECT COUNT(c) > 0 FROM DealerCustomer c WHERE c.firstName = :firstName " +
           "AND c.lastName = :lastName AND c.address.streetNumber = :streetNumber " +
           "AND c.address.postalCode = :postalCode " +
           "AND c.phoneNumber = :phoneNumber")
    boolean existsDuplicateDealerCustomer(@Param("firstName") String firstName,
                                    @Param("lastName") String lastName,
                                    @Param("streetNumber") String streetNumber,  
                                    @Param("postalCode") String postalCode,
                                    @Param("phoneNumber") String phoneNumber);

    @Query("SELECT c FROM DealerCustomer c WHERE c.dealer.id = :dealerId " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<DealerCustomer> searchByDealerAndTerm(@Param("dealerId") Long dealerId,
                                         @Param("searchTerm") String searchTerm,
                                         Pageable pageable);
}
