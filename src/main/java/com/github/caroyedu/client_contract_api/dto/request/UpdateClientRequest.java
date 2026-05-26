package com.github.caroyedu.client_contract_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateClientRequest (
    @NotBlank
    String name,

    @Email
    String email,

    @Pattern(regexp = "\\+?[0-9\\- ]{7,15}")
    String phone
) { }
