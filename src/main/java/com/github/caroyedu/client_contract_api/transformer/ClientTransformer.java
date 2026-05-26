package com.github.caroyedu.client_contract_api.transformer;

import com.github.caroyedu.client_contract_api.dto.ClientDTO;
import com.github.caroyedu.client_contract_api.dto.CompanyClientDTO;
import com.github.caroyedu.client_contract_api.dto.ContractDTO;
import com.github.caroyedu.client_contract_api.dto.PersonClientDTO;
import com.github.caroyedu.client_contract_api.dto.request.CreateClientRequest;
import com.github.caroyedu.client_contract_api.dto.request.UpdateClientRequest;
import com.github.caroyedu.client_contract_api.model.Client;
import com.github.caroyedu.client_contract_api.model.CompanyClient;
import com.github.caroyedu.client_contract_api.model.Contract;
import com.github.caroyedu.client_contract_api.model.PersonClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ClientTransformer {

    private final ContractTransformer contractTransformer;

    public void mapDtoToPersonModel(CreateClientRequest dto, PersonClient model){
        mapDtoToModel(dto, model);
        model.setBirthdate(dto.birthdate());
    }

    public void mapDtoToCompanyModel(CreateClientRequest dto, CompanyClient model){
        mapDtoToModel(dto, model);
        model.setCompanyIdentifier(dto.companyIdentifier());
    }

    private void mapDtoToModel(CreateClientRequest dto, Client model){
        model.setName(dto.name());
        model.setEmail(dto.email());
        model.setPhone(dto.phone());
    }

    public void updateModelFromDto(Client model, UpdateClientRequest dto){
        model.setName(dto.name());
        model.setEmail(dto.email());
        model.setPhone(dto.phone());
    }

    public void mapPersonClientToDto(PersonClient model, PersonClientDTO dto){
        List<ContractDTO> contractDTOList = getContractsDTOFromModel(model);
        dto.setContracts(contractDTOList);
        dto.setPhone(model.getPhone());
        dto.setBirthdate(model.getBirthdate());
        dto.setPublicId(model.getPublicId());
        dto.setEmail(model.getEmail());
        dto.setName(model.getName());
    }

    public void mapCompanyClientToDto(CompanyClient model, CompanyClientDTO dto){
        List<ContractDTO> contractDTOList = getContractsDTOFromModel(model);
        dto.setContracts(contractDTOList);
        dto.setPhone(model.getPhone());
        dto.setCompanyIdentifier(model.getCompanyIdentifier());
        dto.setPublicId(model.getPublicId());
        dto.setEmail(model.getEmail());
        dto.setName(model.getName());
    }

    public void mapClientDTOFromModel(ClientDTO dto, Client model){
        List<ContractDTO> contractDTOList = getContractsDTOFromModel(model);
        dto.setContracts(contractDTOList);
        dto.setPhone(model.getPhone());
        dto.setPublicId(model.getPublicId());
        dto.setEmail(model.getEmail());
        dto.setName(model.getName());
    }

    private List<ContractDTO> getContractsDTOFromModel(Client model){
        List<ContractDTO> contractDTOList = new ArrayList<>();
        for(Contract contract : model.getContracts()){
            ContractDTO contractDTO = new ContractDTO();
            contractTransformer.mapDtoFromModel(contractDTO, contract);

        }
        return contractDTOList;
    }
}
