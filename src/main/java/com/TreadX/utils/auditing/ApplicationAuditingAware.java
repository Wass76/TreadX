package com.TreadX.utils.auditing;


import com.TreadX.user.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditingAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(authentication == null ||
        ! authentication.isAuthenticated() ||
         authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }
        User dealerPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(dealerPrincipal.getId());
    }
}
