package com.TreadX.user.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;


@RequiredArgsConstructor
public enum Role {

    SUPER_ADMIN(Set.of()),
    HR_MANAGER(Set.of()),
    PLATFORM_ADMIN(Set.of()),
    SALES_MANAGER(Set.of()),
    SALES_AGENT(Set.of()),
    SUPPORT_AGENT(Set.of()),

    DEALER_ADMIN(Set.of()),
    DEALER_EMPLOYEE(Set.of()),
    DEALER_TECHNICIAN(Set.of()),;

    @Getter
    private final Set<Permission> permissions;

//    public List<SimpleGrantedAuthority> getAuthorities(){
//       var authorities=   getPermissions()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());
//
//       authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
//
//       return authorities;
//    }
}
