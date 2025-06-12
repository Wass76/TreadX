package com.TreadX.contact.repository;

import com.TreadX.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contact c WHERE c.id = :contactId AND c.owner.id = :userId")
    boolean isContactOwner(@Param("contactId") Long contactId, @Param("userId") Long userId);
} 