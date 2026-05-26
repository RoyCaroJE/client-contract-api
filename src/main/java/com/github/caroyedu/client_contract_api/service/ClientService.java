package com.github.caroyedu.client_contract_api.service;

import com.github.caroyedu.client_contract_api.dto.ClientDTO;
import com.github.caroyedu.client_contract_api.dto.CompanyClientDTO;
import com.github.caroyedu.client_contract_api.dto.PersonClientDTO;
import com.github.caroyedu.client_contract_api.dto.request.CreateClientRequest;
import com.github.caroyedu.client_contract_api.dto.request.UpdateClientRequest;
import com.github.caroyedu.client_contract_api.model.Client;
import com.github.caroyedu.client_contract_api.model.CompanyClient;
import com.github.caroyedu.client_contract_api.model.Contract;
import com.github.caroyedu.client_contract_api.model.PersonClient;
import com.github.caroyedu.client_contract_api.repository.ClientRepository;
import com.github.caroyedu.client_contract_api.repository.CompanyClientRepository;
import com.github.caroyedu.client_contract_api.repository.ContractRepository;
import com.github.caroyedu.client_contract_api.repository.PersonClientRepository;
import com.github.caroyedu.client_contract_api.transformer.ClientTransformer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientService {

    private final PersonClientRepository personClientRepository;
    private final CompanyClientRepository companyClientRepository;
    private final ClientRepository clientRepository;
    private final ClientTransformer clientTransformer;
    private final ContractRepository contractRepository;
    private static final String PERSON = "person";
    private static final String COMPANY = "company";

    @Transactional
    public ClientDTO createClient(CreateClientRequest createClientRequest){
        String email = createClientRequest.email();
        Optional<Client> optionalClient = clientRepository.findClientByEmail(email);
        if(optionalClient.isPresent()) {
            throw new IllegalArgumentException("Client creation failed as a client already exists with this email: " + email);
        }
        String type = createClientRequest.type();
        if(type.equals(PERSON)){
            PersonClient personClient = new PersonClient();
            clientTransformer.mapDtoToPersonModel(createClientRequest, personClient);
            personClient = personClientRepository.save(personClient);
            PersonClientDTO dto = new PersonClientDTO();
            clientTransformer.mapPersonClientToDto(personClient, dto);
            return dto;
        } else if(type.equals(COMPANY)){
            CompanyClient companyClient = new CompanyClient();
            clientTransformer.mapDtoToCompanyModel(createClientRequest, companyClient);
            companyClient = companyClientRepository.save(companyClient);
            CompanyClientDTO dto = new CompanyClientDTO();
            clientTransformer.mapCompanyClientToDto(companyClient, dto);
            return dto;
        } else {
            throw new IllegalArgumentException("The client has not been created as the requested received an unknown type: " + type);
        }
    }

    @Transactional
    public ClientDTO updateClient(UUID publicId, UpdateClientRequest updateClientRequest){
        Optional<Client> optionalClient = clientRepository.findClientByPublicId(publicId);
        if(optionalClient.isEmpty()){
            throw new IllegalArgumentException("Client not found with id: " + publicId);
        }

        Client client = optionalClient.get();
        clientTransformer.updateModelFromDto(client, updateClientRequest);
        client = clientRepository.save(client);
        ClientDTO dto = new ClientDTO();
        clientTransformer.mapClientDTOFromModel(dto, client);
        return dto;
    }

    public Optional<ClientDTO> getClient(UUID publicId) {
        Optional<Client> clientOpt = clientRepository.findClientByPublicId(publicId);

        if (clientOpt.isEmpty()) {
            return Optional.empty();
        }

        Client client = clientOpt.get();

        if (client instanceof PersonClient personClient) {
            PersonClientDTO dto = new PersonClientDTO();
            clientTransformer.mapPersonClientToDto(personClient, dto);
            return Optional.of(dto);
        } else if (client instanceof CompanyClient companyClient) {
            CompanyClientDTO dto = new CompanyClientDTO();
            clientTransformer.mapCompanyClientToDto(companyClient, dto);
            return Optional.of(dto);
        } else {
            throw new IllegalStateException("Unknown client type found for publicId: " + publicId);
        }
    }

    @Transactional
    public boolean deleteClient(UUID publicId){
        Optional<Client> optionalClient = clientRepository.findClientByPublicId(publicId);
        if(optionalClient.isEmpty()){
            throw new IllegalArgumentException("Client not found with id: " + publicId);
        }

        Client client = optionalClient.get();
        client.setDeleted(true); // I soft-delete the client to have a registry of it, but it could be replaced by clientRepository.delete(client)
        client = clientRepository.save(client);

        for(Contract contract : client.getContracts()){
            contract.setDeleted(true);
            contract.setEndDate(LocalDate.now());
        }
        contractRepository.saveAll(client.getContracts());

        return true;
    }
}
