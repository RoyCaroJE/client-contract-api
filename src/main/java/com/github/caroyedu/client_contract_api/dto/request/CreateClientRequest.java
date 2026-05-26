package com.github.caroyedu.client_contract_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record CreateClientRequest (
    @NotBlank
    String type, // "person" or "company"

    @NotBlank
    String name,

    @Email
    String email,

    @Pattern(regexp = "\\+?[0-9\\- ]{7,15}")
    String phone,

    LocalDate birthdate,
    String companyIdentifier
) {}
