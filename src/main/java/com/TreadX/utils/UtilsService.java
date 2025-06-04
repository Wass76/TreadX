package com.TreadX.utils;

import com.TreadX.user.entity.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {

//    @Autowired
//    private DealerRepository dealerRepository;
//
//    public BaseUser extractCurrentUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication.getName() == null) {
//            throw new BadCredentialsException("Authentication error");
//        }
//        var user = dealerRepository.findByEmail(authentication.getName()).orElse(null);
//        if (user == null) {
//            throw new BadCredentialsException("Unauthorized");
//        }
//        return user;
//    }

}