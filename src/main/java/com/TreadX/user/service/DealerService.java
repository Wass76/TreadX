//package com.TreadX.user.service;
//
//import com.TreadX.dealers.entity.Dealer;
//import com.TreadX.dealers.repository.DealerRepository;
//import com.TreadX.user.request.AuthenticationRequest;
//import com.TreadX.user.request.DealerRegisterRequest;
//import com.TreadX.user.response.DealerAuthenticationResponse;
//import com.TreadX.config.JwtService;
//import com.TreadX.config.RateLimiterConfig;
//import com.TreadX.utils.Mapper.ClassMapper;
//import com.TreadX.utils.exception.RequestNotValidException;
//import com.TreadX.utils.exception.TooManyRequestException;
//import com.TreadX.user.request.ChangePasswordRequest;
//import com.TreadX.utils.Validator.ObjectsValidator;
//import io.github.resilience4j.ratelimiter.RateLimiter;
//import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.security.Principal;
//
//@Service
//@RequiredArgsConstructor
//public class DealerService {
//
//    private final PasswordEncoder passwordEncoder;
//    private static final String LOGIN_RATE_LIMITER = "loginRateLimiter";
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    private final RateLimiterConfig rateLimiterConfig;
//    private final RateLimiterRegistry rateLimiterRegistry;
//    @Autowired
//    private DealerRepository dealerRepository;
//
//    @Autowired
//    private ObjectsValidator<DealerRegisterRequest> registerRequestValidator;
//    @Autowired
//    private ObjectsValidator<AuthenticationRequest> authenticationRequestValidator;
//
//    @Transactional
//    public DealerAuthenticationResponse dealerRegister(DealerRegisterRequest request) {
//        registerRequestValidator.validate(request);
//        Dealer existedEmail = dealerRepository.findByEmail(request.getEmail()).orElse(null);
//        if (existedEmail != null) {
//            throw new RequestNotValidException("Email already exists");
//        }
//
//        Dealer dealer = (Dealer) ClassMapper.INSTANCE.dealerDtoToEntity(request);
//        dealer.setPassword(passwordEncoder.encode(request.getPassword()));
//        dealerRepository.save(dealer);
//        var jwtToken = jwtService.generateToken(dealer);
//        DealerAuthenticationResponse response = ClassMapper.INSTANCE.entityToDto(dealer);
//        response.setToken(jwtToken);
//        return response;
//    }
//
//    @Transactional
//    public DealerAuthenticationResponse DealerLogin(AuthenticationRequest request, HttpServletRequest httpServletRequest) {
//
//        String userIp = httpServletRequest.getRemoteAddr();
//        if (rateLimiterConfig.getBlockedIPs().contains(userIp)) {
//            throw new TooManyRequestException("Too many login attempts. Please try again later.");
//        }
//
//        String rateLimiterKey = LOGIN_RATE_LIMITER + "-" + userIp;
//        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterKey);
//
//
//        if (rateLimiter.acquirePermission()) {
//            Authentication authentication;
//            try {
//                authentication = authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(
//                                request.getEmail(),
//                                request.getPassword()
//                        ));
//            } catch (AuthenticationException exception) {
//                throw new BadCredentialsException("invalid email or password");
//            }
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//            var user = dealerRepository.findByEmail(request.getEmail()).orElseThrow(
//                    () -> new RequestNotValidException("email not found")
//            );
//            var jwtToken = jwtService.generateToken(user);
//            DealerAuthenticationResponse response = ClassMapper.INSTANCE.entityToDto(user);
//            response.setToken(jwtToken);
//            return response;
//        }
//        else
//            rateLimiterConfig.blockIP(userIp);
//        throw new TooManyRequestException("Too many login attempts, Please try again later.");
//    }
//
//    public void changePassword(
//            ChangePasswordRequest request, Principal connectedUser){
//
//        var user  = (Dealer) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
//
//        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
//            throw new RequestNotValidException("Wrong password");
//        }
//        if(!request.getNewPassword().equals(request.getConfirmPassword())){
//            throw new RequestNotValidException("Password are not the same");
//        }
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        dealerRepository.save(user);
//    }
//}
