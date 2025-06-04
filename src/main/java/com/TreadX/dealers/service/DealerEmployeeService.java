package com.TreadX.dealers.service;

import com.TreadX.dealers.entity.DealerEmployee;
import com.TreadX.dealers.repository.DealerEmployeeRepository;
import com.TreadX.config.JwtService;
import com.TreadX.config.RateLimiterConfig;
import com.TreadX.utils.exception.RequestNotValidException;
import com.TreadX.utils.exception.TooManyRequestException;
import com.TreadX.user.request.AuthenticationRequest;
import com.TreadX.dealers.dto.DealerEmployeeAuthenticationResponse;
import com.TreadX.utils.Validator.ObjectsValidator;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DealerEmployeeService {

    private final DealerEmployeeRepository dealerEmployeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RateLimiterConfig rateLimiterConfig;
    private final RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private ObjectsValidator<AuthenticationRequest> authenticationRequestValidator;


    @Autowired
    public DealerEmployeeService(DealerEmployeeRepository dealerEmployeeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, RateLimiterConfig rateLimiterConfig, RateLimiterRegistry rateLimiterRegistry) {
        this.dealerEmployeeRepository = dealerEmployeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.rateLimiterConfig = rateLimiterConfig;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    public DealerEmployee createDealerEmployee(DealerEmployee dealerEmployee) {
        return dealerEmployeeRepository.save(dealerEmployee);
    }

    public List<DealerEmployee> getAllDealerEmployees() {
        return dealerEmployeeRepository.findAll();
    }

    public Optional<DealerEmployee> getDealerEmployeeById(Long id) {
        return dealerEmployeeRepository.findById(id);
    }

    public DealerEmployee updateDealerEmployee(Long id, DealerEmployee dealerEmployeeDetails) {
        DealerEmployee dealerEmployee = dealerEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer Employee not found with id: " + id));
        
        dealerEmployee.setFirstName(dealerEmployeeDetails.getFirstName());
        dealerEmployee.setLastName(dealerEmployeeDetails.getLastName());
        dealerEmployee.setEmail(dealerEmployeeDetails.getEmail());
        dealerEmployee.setDealer(dealerEmployeeDetails.getDealer());
        
        return dealerEmployeeRepository.save(dealerEmployee);
    }

    public void deleteDealerEmployee(Long id) {
        DealerEmployee dealerEmployee = dealerEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer Employee not found with id: " + id));
        dealerEmployeeRepository.delete(dealerEmployee);
    }

    public DealerEmployeeAuthenticationResponse login(AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        String userIp = httpServletRequest.getRemoteAddr();
        if (rateLimiterConfig.getBlockedIPs().contains(userIp)) {
            throw new TooManyRequestException("Too many login attempts. Please try again later.");
        }

        String rateLimiterKey = "employeeLoginRateLimiter-" + userIp;
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterKey);

        if (rateLimiter.acquirePermission()) {
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        ));
            } catch (AuthenticationException exception) {
                throw new BadCredentialsException("invalid email or password");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            var employee = dealerEmployeeRepository.findByEmail(request.getEmail()).orElseThrow(
                    () -> new RequestNotValidException("Employee email not found")
            );

            var jwtToken = jwtService.generateToken(employee);
            
            DealerEmployeeAuthenticationResponse response = dealerEmployeeEntityToDto(employee);
            response.setToken(jwtToken);

            return response;
        } else {
            rateLimiterConfig.blockIP(userIp);
            throw new TooManyRequestException("Too many login attempts, Please try again later.");
        }
    }

    private DealerEmployeeAuthenticationResponse dealerEmployeeEntityToDto(DealerEmployee dealerEmployee) {
        DealerEmployeeAuthenticationResponse dealerEmployeeAuthenticationResponse = new DealerEmployeeAuthenticationResponse();
        dealerEmployeeAuthenticationResponse.setEmail(dealerEmployee.getEmail());
        dealerEmployeeAuthenticationResponse.setFirstName(dealerEmployee.getFirstName());
        dealerEmployeeAuthenticationResponse.setLastName(dealerEmployee.getLastName());
        return dealerEmployeeAuthenticationResponse;
    }
} 