package com.tata.account.controller;

import com.tata.account.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> checkHealth() {
        Map<String, String> data = new HashMap<>();
        data.put("Service", "tata-account-service");
        data.put("Version", "2024102000");
        ApiResponseDto<Map<String, String>> response =
                new ApiResponseDto<>(
                        true,
                        "System Online", data);
        return ResponseEntity.ok(response);
    }
}
