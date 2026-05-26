package com.github.caroyedu.client_contract_api.exception.dto;

import java.time.Instant;

public record ErrorResponseDTO(Instant timestamp, int status, String message, String detailedMessage) {}
