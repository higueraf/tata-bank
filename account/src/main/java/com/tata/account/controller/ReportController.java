package com.tata.account.controller;

import com.tata.account.dto.ApiResponseDto;
import com.tata.account.dto.ReportResponseDto;
import com.tata.account.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ReportResponseDto>>> getAccountStatement(
            @RequestParam("initialDate") String initialDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "accountId", required = false) String accountId,
            @RequestParam(value = "customerId", required = false) String customerId) {

        List<ReportResponseDto> report = reportService.getAccountStatement(
                LocalDate.parse(initialDate),
                LocalDate.parse(endDate),
                accountId != null ? UUID.fromString(accountId) : null,
                customerId != null ? UUID.fromString(customerId) : null);

        return ResponseEntity.ok(new ApiResponseDto<>(true, "Report generated successfully", report));
    }
}
