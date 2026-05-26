package com.github.caroyedu.client_contract_api.contract;

import com.github.caroyedu.client_contract_api.dto.ContractDTO;
import com.github.caroyedu.client_contract_api.dto.ContractCostAmountDTO;
import com.github.caroyedu.client_contract_api.dto.request.CreateContractRequest;
import com.github.caroyedu.client_contract_api.dto.request.PatchContractCostAmount;
import com.github.caroyedu.client_contract_api.model.Contract;
import com.github.caroyedu.client_contract_api.model.PersonClient;
import com.github.caroyedu.client_contract_api.repository.ClientRepository;
import com.github.caroyedu.client_contract_api.repository.ContractRepository;
import com.github.caroyedu.client_contract_api.repository.PersonClientRepository;
import com.github.caroyedu.client_contract_api.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ContractServiceIntegrationTest {

    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PersonClientRepository personClientRepository;

    private PersonClient testClient;

    private final static BigDecimal CONTRACT_COST_AMOUNT = BigDecimal.valueOf(1000.0);
    private final static LocalDate CONTRACT_START_DATE = LocalDate.now();
    private final static LocalDate CONTRACT_END_DATE = LocalDate.now().plusMonths(6);

    @BeforeEach
    void setup() {
        contractRepository.deleteAll();
        clientRepository.deleteAll();

        PersonClient personClient = new PersonClient();
        personClient.setPublicId(UUID.randomUUID());
        personClient.setPhone("+34685967843");
        personClient.setName("Person");
        personClient.setEmail("person@example.com");
        personClient.setBirthdate(LocalDate.parse("1967-02-03"));
        testClient = personClientRepository.save(personClient);
    }

    @Test
    void shouldCreateContract() {
        CreateContractRequest request = createContractRequest();
        ContractDTO created = contractService.createContract(request);

        assertNotNull(created);
        assertEquals(CONTRACT_COST_AMOUNT, created.getCostAmount());
        assertEquals(testClient.getPublicId(), created.getClientPublicId());

        Optional<Contract> optionalContract =  contractRepository.findByPublicId(created.getPublicId());
        assertTrue(optionalContract.isPresent());
    }

    @Test
    void shouldPatchContractCostAmount() {
        CreateContractRequest request = createContractRequest();
        ContractDTO created = contractService.createContract(request);

        PatchContractCostAmount patchRequest = new PatchContractCostAmount();
        BigDecimal newCost = BigDecimal.valueOf(5000);
        patchRequest.setCostAmount(newCost);

        ContractDTO patched = contractService.patchContractCostAmount(created.getPublicId(), patchRequest);

        assertEquals(newCost, patched.getCostAmount());
    }

    @Test
    void shouldGetContractsByClientPublicId() {
        CreateContractRequest request1 = createContractRequest();
        CreateContractRequest request2 = createContractRequest();
        contractService.createContract(request1);
        contractService.createContract(request2);

        List<ContractDTO> contracts = contractService.getContractsByClientPublicId(testClient.getPublicId(), null);
        assertEquals(2, contracts.size());
    }

    @Test
    void shouldGetContractCostAmountByClientPublicId() {
        CreateContractRequest request1 = createContractRequest();
        CreateContractRequest request2 = createContractRequest();
        contractService.createContract(request1);
        contractService.createContract(request2);

        ContractCostAmountDTO total = contractService.getContractCostAmountByClientPublicId(testClient.getPublicId());
        assertEquals(0, total.getTotalCostAmount().compareTo(CONTRACT_COST_AMOUNT.multiply(BigDecimal.valueOf(2))));
    }

    private CreateContractRequest createContractRequest() {
        return new CreateContractRequest(testClient.getPublicId(), CONTRACT_START_DATE, CONTRACT_END_DATE, CONTRACT_COST_AMOUNT);
    }
}
