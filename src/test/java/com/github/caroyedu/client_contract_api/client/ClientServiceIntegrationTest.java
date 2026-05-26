package com.github.caroyedu.client_contract_api.client;

import com.github.caroyedu.client_contract_api.dto.ClientDTO;
import com.github.caroyedu.client_contract_api.dto.request.CreateClientRequest;
import com.github.caroyedu.client_contract_api.dto.request.UpdateClientRequest;
import com.github.caroyedu.client_contract_api.model.Client;
import com.github.caroyedu.client_contract_api.model.PersonClient;
import com.github.caroyedu.client_contract_api.repository.ClientRepository;
import com.github.caroyedu.client_contract_api.repository.CompanyClientRepository;
import com.github.caroyedu.client_contract_api.repository.PersonClientRepository;
import com.github.caroyedu.client_contract_api.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PersonClientRepository personClientRepository;
    @Autowired
    private CompanyClientRepository companyClientRepository;

    private final static String PERSON_CLIENT_NAME = "Person Client";
    private final static String PERSON_CLIENT_BIRTHDATE = "1990-10-10";
    private final static String PERSON_CLIENT_EMAIL = "personclient@example.com";
    private final static String PERSON_CLIENT_PHONE = "+4054678543";

    private final static String COMPANY_CLIENT_NAME = "Company Client";
    private final static String COMPANY_CLIENT_IDENTIFIER = "111-aaa";
    private final static String COMPANY_CLIENT_EMAIL = "companyclient@example.com";
    private final static String COMPANY_CLIENT_PHONE = "+3398807642";

    @BeforeEach
    void setup() {
        // All tests have to be independent, that's why we always start from scratch
        clientRepository.deleteAll();
        personClientRepository.deleteAll();
    }

    @Test
    void shouldCreatePersonClient() {
        CreateClientRequest personRequest = createPersonClientRequest();
        ClientDTO created = clientService.createClient(personRequest);

        assertNotNull(created);
        assertEquals(PERSON_CLIENT_NAME, created.getName());
        assertEquals(PERSON_CLIENT_EMAIL, created.getEmail());
        assertEquals(PERSON_CLIENT_PHONE, created.getPhone());
        assertNotNull(created.getPublicId());

        Optional<Client> optionalClient = clientRepository.findClientByPublicId(created.getPublicId());
        assertTrue(optionalClient.isPresent());
        assertTrue(personClientRepository.existsById(optionalClient.get().getId()));
    }

    @Test
    void shouldDeleteClient() {
        PersonClient personClient = new PersonClient();
        personClient.setPublicId(UUID.randomUUID());
        personClient.setPhone("0102011");
        personClient.setName("DeleteMe");
        personClient.setEmail("delete@me.com");
        personClient.setBirthdate(LocalDate.parse("2000-01-01"));
        personClient = personClientRepository.save(personClient);

        clientService.deleteClient(personClient.getPublicId());

        Optional<PersonClient> clientOptional = personClientRepository.findById(personClient.getId());
        assertTrue(clientOptional.isPresent());
        assertTrue(clientOptional.get().isDeleted());
    }

    @Test
    void shouldCreateCompanyClient() {
        CreateClientRequest companyRequest = createCompanyClientRequest();
        ClientDTO created = clientService.createClient(companyRequest);

        assertNotNull(created);
        assertEquals(COMPANY_CLIENT_NAME, created.getName());
        assertEquals(COMPANY_CLIENT_EMAIL, created.getEmail());
        assertEquals(COMPANY_CLIENT_PHONE, created.getPhone());
        assertNotNull(created.getPublicId());

        Optional<Client> optionalClient = clientRepository.findClientByPublicId(created.getPublicId());
        assertTrue(optionalClient.isPresent());
        assertTrue(companyClientRepository.existsById(optionalClient.get().getId()));
    }

    @Test
    void shouldUpdatePersonClient() {
        CreateClientRequest personRequest = createPersonClientRequest();
        ClientDTO created = clientService.createClient(personRequest);

        UpdateClientRequest update = new UpdateClientRequest("Person Updated", "personupdated@example.com", "0101");

        ClientDTO updated = clientService.updateClient(created.getPublicId(), update);

        assertEquals("Person Updated", updated.getName());
        assertEquals("personupdated@example.com", updated.getEmail());
        assertEquals("0101", updated.getPhone());
    }

    /*
    Some private functions to help our tests being cleaner
     */

    private CreateClientRequest createPersonClientRequest(){
        return new CreateClientRequest("person", PERSON_CLIENT_NAME, PERSON_CLIENT_EMAIL, PERSON_CLIENT_PHONE, LocalDate.parse(PERSON_CLIENT_BIRTHDATE), null);
    }

    private CreateClientRequest createCompanyClientRequest(){
        return new CreateClientRequest("company", COMPANY_CLIENT_NAME, COMPANY_CLIENT_EMAIL, COMPANY_CLIENT_PHONE, null, COMPANY_CLIENT_IDENTIFIER);
    }
}
