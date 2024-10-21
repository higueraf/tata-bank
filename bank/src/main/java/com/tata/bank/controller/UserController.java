package com.tata.bank.controller;

import com.tata.bank.dto.*;
import com.tata.bank.entity.User;
import com.tata.bank.security.SecurityUtil;
import com.tata.bank.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponseDto<UserResponseDto>> createUser(
            @RequestBody CreateUserRequestDto createUserRequestDto) {
        //DataSessionDto dataSession = SecurityUtil.getDataSession();
        //UUID uuidCreatedBy = UUID.fromString(dataSession.getUserId());
        UUID uuidCreatedBy = UUID.fromString("aeff0e88-3717-4730-b8be-00294de21a73");
        UserDto userDto = createUserRequestDto.getUser();
        List<UUID> roleIds = createUserRequestDto.getRoleIds();
        UserResponseDto createdUser = userService.createUser(
                userDto,
                roleIds,
                uuidCreatedBy);
        ApiResponseDto<UserResponseDto> apiResponseDto = new ApiResponseDto<>(
                true,
                "User created successfully",
                createdUser);
        return ResponseEntity.ok(apiResponseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @PathVariable UUID userId,
            @RequestBody CreateUserRequestDto updateUserRequestDto) {
        DataSessionDto dataSession = SecurityUtil.getDataSession();
        UUID uuidUpdateddBy = UUID.fromString(dataSession.getUserId());
        UserDto userDto = updateUserRequestDto.getUser();
        List<UUID> roleIds = new ArrayList<>();
        if (updateUserRequestDto.getRoleIds() != null) {
            roleIds = updateUserRequestDto.getRoleIds();
        }
        userDto.setId(userId);
        ApiResponseDto<UserResponseDto> apiResponseDto = userService.updateUser(
                userDto,
                roleIds,
                uuidUpdateddBy);
        if (apiResponseDto.isSuccess()) {
            return ResponseEntity.ok(apiResponseDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDto);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users != null) {
            List<UserDto> userDtos = users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
            return ResponseEntity.ok(new ApiResponseDto<>(
                    true,
                    "Users retrieved successfully",
                    userDtos)
            );
        }   else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(
                            false,
                            "Error retrieving users",
                            null)
                    );
        }
    }

    @PostMapping("/filtered-page")
    public ResponseEntity<ApiResponseDto<PaginationResponseDto<UserDto>>> getFilteredUsers(
            @RequestBody FilteredRequestDto filteredRequestDto) {
        Page<UserDto> usersDto = userService.getFilteredUsers(filteredRequestDto);
        PaginationResponseDto<UserDto> paginationResponseDto = new PaginationResponseDto<>(
                usersDto.getContent(),
                usersDto.getTotalElements(),
                usersDto.getNumber(),
                usersDto.getSize(),
                usersDto.getTotalPages()
        );
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "Users retrieved successfully",
                        paginationResponseDto)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserById(
            @PathVariable UUID id) {
        Optional<UserResponseDto> user = userService.getUserById(id);
        return user.map(value -> ResponseEntity.ok(
                        new ApiResponseDto<>(
                                true,
                                "User retrieved successfully",
                                modelMapper.map(value, UserDto.class))))
                .orElseGet(() -> ResponseEntity.status(
                                HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>(
                                false,
                                "Error retrieving user",
                                null)
                        )
                );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @PathVariable UUID id) {
        DataSessionDto dataSession = SecurityUtil.getDataSession();
        userService.deleteUser(id, UUID.fromString(dataSession.getUserId()));
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "User deleted successfully",
                        null));
    }

}