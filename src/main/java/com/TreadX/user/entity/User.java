package com.TreadX.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@SuperBuilder(builderMethodName = "builder")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseUser {

    private String position;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_permissions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> additionalPermissions = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Add role-based authorities
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        
        // Add role permissions
        authorities.addAll(role.getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .collect(Collectors.toSet()));
        
        // Add additional permissions
        authorities.addAll(additionalPermissions.stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .collect(Collectors.toSet()));
            
        return authorities;
    }

    @Override
    protected String getSequenceName() {
        return "user_id_seq";
    }

//    @Override
//    public String getUsername() {
//        return email;  // Use email as username for authentication
//    }
}
