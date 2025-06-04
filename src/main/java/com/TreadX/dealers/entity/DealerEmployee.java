package com.TreadX.dealers.entity;

import com.TreadX.user.entity.User;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "dealer_employee")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DealerEmployee extends User implements UserDetails {
    private String status;
    private Integer accessLevel;
    private String dealerEmployeeNumber;
    private String dealerEmployeeUniqueId;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // Assuming accessLevel or status can determine roles/authorities
//        // This is a basic example, you might need more complex logic
//        return List.of(new SimpleGrantedAuthority(String.valueOf(accessLevel)));
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement account expiration logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement account locking logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement credentials expiration logic if needed
    }

    @Override
    public boolean isEnabled() {
        // Assuming status can indicate if the employee is enabled
        return true;
//        return "active".equalsIgnoreCase(status); // Implement employee enabled logic
    }
} 