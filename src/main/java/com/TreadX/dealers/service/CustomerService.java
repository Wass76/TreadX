package com.TreadX.dealers.service;

import com.TreadX.dealers.dto.CustomerRequestDTO;
import com.TreadX.dealers.dto.CustomerResponseDTO;
import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.CustomerPhone;
import com.TreadX.dealers.repository.CustomerRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.CustomerPhoneRepository;
import com.TreadX.address.entity.City;
import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.address.service.AddressService;
import com.TreadX.address.repository.CountryRepository;
import com.TreadX.address.repository.StateRepository;
import com.TreadX.address.repository.CityRepository;
import com.TreadX.dealers.enums.PhoneType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final DealerRepository dealerRepository;
    private final CustomerPhoneRepository customerPhoneRepository;
    private final AddressService addressService;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        // Validate and get address entities
        Country country = countryRepository.findByName(dto.getCountryName())
                .orElseThrow(() -> new RuntimeException("Country not found: " + dto.getCountryName()));
        State state = stateRepository.findByName(dto.getStateName())
                .orElseThrow(() -> new RuntimeException("State not found: " + dto.getStateName()));
        City city = cityRepository.findByName(dto.getCityName())
                .orElseThrow(() -> new RuntimeException("City not found: " + dto.getCityName()));

        // Process address and get or create system entities
        addressService.processAddressForSystemEntries(country, state, city);

        // Get system entities to get their unique IDs
        SystemCountry systemCountry = addressService.getSystemCountryByEntity(country)
                .orElseThrow(() -> new RuntimeException("System country not found for: " + country.getName()));
        SystemProvince systemProvince = addressService.getSystemProvinceByEntity(state)
                .orElseThrow(() -> new RuntimeException("System province not found for: " + state.getName()));
        SystemCity systemCity = addressService.getSystemCityByEntity(city)
                .orElseThrow(() -> new RuntimeException("System city not found for: " + city.getName()));

        // Create customer
        Customer customer = new Customer();
        Dealer dealer = dealerRepository.findById(dto.getDealerId())
                .orElseThrow(() -> new RuntimeException("Dealer not found with id: " + dto.getDealerId()));
        
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setDealer(dealer);
        customer.setCustomerUniqueId(dto.getCustomerUniqueId());
        customer.setDealerCustomerUniqueId(dto.getDealerCustomerUniqueId());
        
        // Set address fields
        customer.setStreetNumber(dto.getStreetNumber());
        customer.setStreetName(dto.getStreetName());
        customer.setPostalCode(dto.getPostalCode());
        customer.setUnitNumber(dto.getUnitNumber());
        customer.setSpecialInstructions(dto.getSpecialInstructions());
        customer.setCountry(country);
        customer.setState(state);
        customer.setCity(city);
        
        // Save customer first to get the ID
        customer = customerRepository.save(customer);
        
        // Create and save customer phones if provided
        if (dto.getCustomerPhone() != null) {
            createCustomerPhone(customer, dto.getCustomerPhone(), "CUSTOMER", "SYSTEM");
        }
        if (dto.getHomePhone() != null) {
            createCustomerPhone(customer, dto.getHomePhone(), "HOME", "SYSTEM");
        }
        if (dto.getBusinessPhone() != null) {
            createCustomerPhone(customer, dto.getBusinessPhone(), "BUSINESS", "SYSTEM");
        }
        
        return toResponseDTO(customer, systemCountry, systemProvince, systemCity);
    }

    private void createCustomerPhone(Customer customer, String phoneNumber, String phoneType, String addedBy) {
        CustomerPhone phone = new CustomerPhone();
        phone.setDealerCustomer(customer);
        phone.setPhoneNumber(phoneNumber);
        phone.setPhoneType(PhoneType.valueOf(phoneType));
        phone.setPhoneStatus("ACTIVE");
        phone.setAddedDate(LocalDateTime.now());
        phone.setAddedBy(addedBy);
        customerPhoneRepository.save(phone);
    }

    public CustomerResponseDTO getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        SystemCountry systemCountry = addressService.getSystemCountryByEntity(customer.getCountry())
                .orElseThrow(() -> new RuntimeException("System country not found"));
        SystemProvince systemProvince = addressService.getSystemProvinceByEntity(customer.getState())
                .orElseThrow(() -> new RuntimeException("System province not found"));
        SystemCity systemCity = addressService.getSystemCityByEntity(customer.getCity())
                .orElseThrow(() -> new RuntimeException("System city not found"));
        
        return toResponseDTO(customer, systemCountry, systemProvince, systemCity);
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> {
                    SystemCountry systemCountry = addressService.getSystemCountryByEntity(customer.getCountry())
                            .orElseThrow(() -> new RuntimeException("System country not found"));
                    SystemProvince systemProvince = addressService.getSystemProvinceByEntity(customer.getState())
                            .orElseThrow(() -> new RuntimeException("System province not found"));
                    SystemCity systemCity = addressService.getSystemCityByEntity(customer.getCity())
                            .orElseThrow(() -> new RuntimeException("System city not found"));
                    return toResponseDTO(customer, systemCountry, systemProvince, systemCity);
                })
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        Dealer dealer = dealerRepository.findById(dto.getDealerId())
                .orElseThrow(() -> new RuntimeException("Dealer not found with id: " + dto.getDealerId()));
        
        // Validate and get address entities
        Country country = countryRepository.findByName(dto.getCountryName())
                .orElseThrow(() -> new RuntimeException("Country not found: " + dto.getCountryName()));
        State state = stateRepository.findByName(dto.getStateName())
                .orElseThrow(() -> new RuntimeException("State not found: " + dto.getStateName()));
        City city = cityRepository.findByName(dto.getCityName())
                .orElseThrow(() -> new RuntimeException("City not found: " + dto.getCityName()));

        // Process address and get or create system entities
        addressService.processAddressForSystemEntries(country, state, city);

        // Get system entities to get their unique IDs
        SystemCountry systemCountry = addressService.getSystemCountryByEntity(country)
                .orElseThrow(() -> new RuntimeException("System country not found for: " + country.getName()));
        SystemProvince systemProvince = addressService.getSystemProvinceByEntity(state)
                .orElseThrow(() -> new RuntimeException("System province not found for: " + state.getName()));
        SystemCity systemCity = addressService.getSystemCityByEntity(city)
                .orElseThrow(() -> new RuntimeException("System city not found for: " + city.getName()));
        
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setDealer(dealer);
        customer.setCustomerUniqueId(dto.getCustomerUniqueId());
        customer.setDealerCustomerUniqueId(dto.getDealerCustomerUniqueId());
        
        // Update address fields
        customer.setStreetNumber(dto.getStreetNumber());
        customer.setStreetName(dto.getStreetName());
        customer.setPostalCode(dto.getPostalCode());
        customer.setUnitNumber(dto.getUnitNumber());
        customer.setSpecialInstructions(dto.getSpecialInstructions());
        customer.setCountry(country);
        customer.setState(state);
        customer.setCity(city);
        
        // Update or create phone numbers
        updateCustomerPhone(customer, dto.getCustomerPhone(), "CUSTOMER");
        updateCustomerPhone(customer, dto.getHomePhone(), "HOME");
        updateCustomerPhone(customer, dto.getBusinessPhone(), "BUSINESS");
        
        customer = customerRepository.save(customer);
        return toResponseDTO(customer, systemCountry, systemProvince, systemCity);
    }

    private void updateCustomerPhone(Customer customer, String phoneNumber, String phoneType) {
        if (phoneNumber != null) {
            CustomerPhone phone = customerPhoneRepository.findByDealerCustomerAndPhoneType(customer, PhoneType.valueOf(phoneType))
                    .orElse(new CustomerPhone());
            phone.setDealerCustomer(customer);
            phone.setPhoneNumber(phoneNumber);
            phone.setPhoneType(PhoneType.valueOf(phoneType));
            phone.setPhoneStatus("ACTIVE");
            phone.setUpdatedDate(LocalDateTime.now());
            phone.setUpdatedBy("SYSTEM");
            if (phone.getAddedDate() == null) {
                phone.setAddedDate(LocalDateTime.now());
                phone.setAddedBy("SYSTEM");
            }
            customerPhoneRepository.save(phone);
        }
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Delete associated phone numbers first
        customerPhoneRepository.deleteByDealerCustomer(customer);
        
        customerRepository.delete(customer);
    }

    private CustomerResponseDTO toResponseDTO(Customer customer, SystemCountry systemCountry, SystemProvince systemProvince, SystemCity systemCity) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setDealerId(customer.getDealer() != null ? customer.getDealer().getId() : null);
        dto.setCustomerUniqueId(customer.getCustomerUniqueId());
        dto.setDealerCustomerUniqueId(customer.getDealerCustomerUniqueId());
        
        // Set address fields
        dto.setStreetNumber(customer.getStreetNumber());
        dto.setStreetName(customer.getStreetName());
        dto.setPostalCode(customer.getPostalCode());
        dto.setUnitNumber(customer.getUnitNumber());
        dto.setSpecialInstructions(customer.getSpecialInstructions());
        dto.setCountryName(customer.getCountry().getName());
        dto.setStateName(customer.getState().getName());
        dto.setCityName(customer.getCity().getName());
        dto.setCountryUniqueId(String.valueOf(systemCountry.getCountryUniqueId()));
        dto.setStateUniqueId(String.valueOf(systemProvince.getProvinceUniqueId()));
        dto.setCityUniqueId(String.valueOf(systemCity.getCityUniqueId()));
        
        // Get all phone numbers
        List<CustomerPhone> phones = customerPhoneRepository.findAllByDealerCustomer(customer);
        for (CustomerPhone phone : phones) {
            switch (phone.getPhoneType().name()) {
                case "CUSTOMER":
                    dto.setCustomerPhone(phone.getPhoneNumber());
                    break;
                case "HOME":
                    dto.setHomePhone(phone.getPhoneNumber());
                    break;
                case "BUSINESS":
                    dto.setBusinessPhone(phone.getPhoneNumber());
                    break;
            }
        }
        
        return dto;
    }
} 