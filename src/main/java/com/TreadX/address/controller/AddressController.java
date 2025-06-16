package com.TreadX.address.controller;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.dto.CountryResponseDTO;
import com.TreadX.address.dto.StateResponseDTO;
import com.TreadX.address.dto.CityResponseDTO;
import com.TreadX.address.entity.City;
import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Address Management", description = "APIs for managing addresses and location data")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(
        summary = "Get all addresses",
        description = "Retrieves a list of all addresses in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all addresses",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AddressResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        List<AddressResponseDTO> addresses = addressService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("/countries")
    @Operation(
        summary = "Get all countries",
        description = "Retrieves a list of all countries available in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all countries",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllCountries() {
        List<SystemCountry> countries = addressService.getAllCountries();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    @GetMapping("/base/countries")
    @Operation(
        summary = "Get all base countries",
        description = "Retrieves a list of all base countries from the database."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all base countries",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CountryResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CountryResponseDTO>> getAllBaseCountries() {
        List<CountryResponseDTO> countries = addressService.getAllBaseCountries();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    @GetMapping("/base/states")
    @Operation(
        summary = "Get all base states",
        description = "Retrieves a list of all base states from the database."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all base states",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StateResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StateResponseDTO>> getAllBaseStates() {
        List<StateResponseDTO> states = addressService.getAllBaseStates();
        return new ResponseEntity<>(states, HttpStatus.OK);
    }

    @GetMapping("/base/cities")
    @Operation(
        summary = "Get all base cities",
        description = "Retrieves a list of all base cities from the database."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all base cities",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CityResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CityResponseDTO>> getAllBaseCities() {
        List<CityResponseDTO> cities = addressService.getAllBaseCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/base/provinces/{provinceId}/cities")
    @Operation(
        summary = "Get base cities by province",
        description = "Retrieves a list of all base cities for a specific province/state."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cities for the province",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CityResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Province not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CityResponseDTO>> getBaseCitiesByProvince(
            @Parameter(description = "ID of the province/state", required = true) @PathVariable("provinceId") Long provinceId) {
        List<CityResponseDTO> cities = addressService.getBaseCitiesByProvince(provinceId);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/base/countries/{countryId}/cities")
    @Operation(
        summary = "Get base cities by country",
        description = "Retrieves a list of all base cities for a specific country."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cities for the country",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CityResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Country not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CityResponseDTO>> getBaseCitiesByCountry(
            @Parameter(description = "ID of the country", required = true) @PathVariable("countryId") Long countryId) {
        List<CityResponseDTO> cities = addressService.getBaseCitiesByCountry(countryId);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/base/countries/{countryId}/provinces")
    @Operation(
        summary = "Get base provinces by country",
        description = "Retrieves a list of all base provinces/states for a specific country."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved provinces for the country",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = StateResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Country not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StateResponseDTO>> getBaseProvincesByCountry(
            @Parameter(description = "ID of the country", required = true) @PathVariable("countryId") Long countryId) {
        List<StateResponseDTO> provinces = addressService.getBaseProvincesByCountry(countryId);
        return new ResponseEntity<>(provinces, HttpStatus.OK);
    }

    @GetMapping("/countries/{countryId}/provinces")
    @Operation(
        summary = "Get provinces by country",
        description = "Retrieves a list of all provinces/states for a specific country."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved provinces for the country",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Country not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getProvincesByCountry(
            @Parameter(description = "ID of the country", required = true) @PathVariable("countryId") Long countryId) {
        List<SystemProvince> provinces = addressService.getProvincesByCountry(countryId);
        return new ResponseEntity<>(provinces, HttpStatus.OK);
    }

    @GetMapping("/provinces/{provinceId}/cities")
    @Operation(
        summary = "Get cities by province",
        description = "Retrieves a list of all cities for a specific province/state."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cities for the province",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Province not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getCitiesByProvince(
            @Parameter(description = "ID of the province", required = true) @PathVariable("provinceId") Long provinceId) {
        List<SystemCity> cities = addressService.getCitiesByProvince(provinceId);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/countries/{countryId}/cities")
    @Operation(
        summary = "Get cities by country",
        description = "Retrieves a list of all cities for a specific country."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cities for the country",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Country not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getCitiesByCountry(
            @Parameter(description = "ID of the country", required = true) @PathVariable("countryId") Long countryId) {
        List<SystemCity> cities = addressService.getCitiesByCountry(countryId);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

//    @PostMapping
//    @Operation(
//        summary = "Create new address",
//        description = "Creates a new address in the system."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "Successfully created the address",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = AddressResponseDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Invalid input data"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<AddressResponseDTO> createAddress(
//            @Parameter(description = "Address data", required = true) @RequestBody AddressRequestDTO address) {
//        AddressResponseDTO createdAddress = addressService.createAddress(address);
//        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
//    }

//    @PutMapping("/{id}")
//    @Operation(
//        summary = "Update address",
//        description = "Updates an existing address in the system."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Successfully updated the address",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = AddressResponseDTO.class))),
//        @ApiResponse(responseCode = "404", description = "Address not found"),
//        @ApiResponse(responseCode = "400", description = "Invalid input data"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<AddressResponseDTO> updateAddress(
//            @Parameter(description = "ID of the address", required = true) @PathVariable("id") Long id,
//            @Parameter(description = "Updated address data", required = true) @RequestBody AddressRequestDTO addressDetails) {
//        AddressResponseDTO updatedAddress = addressService.updateAddress(id, addressDetails);
//        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
//    }

//    @DeleteMapping("/{id}")
//    @Operation(
//        summary = "Delete address",
//        description = "Deletes an address from the system."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "204", description = "Successfully deleted the address"),
//        @ApiResponse(responseCode = "404", description = "Address not found"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<Void> deleteAddress(
//            @Parameter(description = "ID of the address", required = true) @PathVariable("id") Long id) {
//        addressService.deleteAddress(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
} 