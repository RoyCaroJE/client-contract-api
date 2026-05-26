package com.github.caroyedu.client_contract_api.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateContractRequest (
    @NotBlank
    UUID clientPublicId,
    
    LocalDate startDate,
    
    LocalDate endDate,
    
    @NotBlank
    BigDecimal costAmount
) {}
