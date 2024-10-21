package com.tata.bank.controller;

import com.tata.bank.dto.*;
import com.tata.bank.entity.*;
import com.tata.bank.repository.UserRepository;
import com.tata.bank.security.JwtTokenUtil;
import com.tata.bank.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private Dotenv dotenv;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<ResponseLoginDto>> login(
            @RequestBody AuthRequestDto authRequestDto) {
        System.out.println("Debug");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDto.getUsername(), authRequestDto.getPassword())
            );
            User userDetails = userService.loadUserByUsernameAndDeletedByIsNull(authRequestDto.getUsername());
            if (userDetails.getUserStatus() != EnumUserStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(
                                false,
                                "Account not activated. Please check your email for activation.",
                                null));
            }
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            ResponseLoginDto responseLoginDto = new ResponseLoginDto();
            responseLoginDto.setToken(accessToken);
            return ResponseEntity.ok(new ApiResponseDto<>(
                    true,
                    "Login successful",
                    responseLoginDto));
        } catch (BadCredentialsException bce) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Invalid username or password", null));
        }
    }
}