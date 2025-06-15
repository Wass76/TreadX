package com.TreadX.user.controller;

import com.TreadX.user.dto.UserCreateRequestDTO;
import com.TreadX.user.dto.UserRequestDTO;
import com.TreadX.user.dto.UserResponseDTO;
import com.TreadX.user.entity.User;
import com.TreadX.user.service.UserService;
import com.TreadX.user.request.AuthenticationRequest;
import com.TreadX.user.response.UserAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing system users and their information")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserAuthenticationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "429", description = "Too many login attempts")
    })
    public ResponseEntity<UserAuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpServletRequest) {
        UserAuthenticationResponse response = userService.login(request, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or @authorizationService.isCurrentUser(#id)")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their ID. Requires PLATFORM_ADMIN role or ownership."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create new user",
        description = "Creates a new user in the system. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserCreateRequestDTO user) {
        UserResponseDTO createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or @authorizationService.isCurrentUser(#id)")
    @Operation(
        summary = "Update user",
        description = "Updates an existing user's information. Requires PLATFORM_ADMIN role or ownership."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userDetails) {
        UserResponseDTO updatedUser = userService.updateUser(id, userDetails);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user from the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Update user permissions",
        description = "Updates the additional permissions for a user. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions updated successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> updateUserPermissions(
            @Parameter(description = "ID of the user to update permissions for") @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        UserResponseDTO updatedUser = userService.updateUserPermissions(id, permissionIds);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
} 