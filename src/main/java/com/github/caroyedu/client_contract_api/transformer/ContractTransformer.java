package com.github.caroyedu.client_contract_api.transformer;

import com.github.caroyedu.client_contract_api.dto.ContractDTO;
import com.github.caroyedu.client_contract_api.dto.request.CreateContractRequest;
import com.github.caroyedu.client_contract_api.model.Client;
import com.github.caroyedu.client_contract_api.model.Contract;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class ContractTransformer {

    public void mapDtoToModel(CreateContractRequest dto, Contract model, Client client){
        model.setClient(client);
        model.setStartDate(dto.startDate() == null ? LocalDate.now() : dto.startDate());
        model.setEndDate(dto.endDate());
        model.setCostAmount(dto.costAmount());
        model.setUpdated(OffsetDateTime.now()); // Business Requirement
    }

    public void mapDtoFromModel(ContractDTO dto, Contract model){
        dto.setPublicId(model.getPublicId());
        dto.setClientPublicId(model.getClient().getPublicId());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        dto.setCostAmount(model.getCostAmount());
    }
}
