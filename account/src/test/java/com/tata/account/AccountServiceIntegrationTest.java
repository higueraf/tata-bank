package com.tata.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.account.dto.AccountRequestDto;
import com.tata.account.dto.AccountResponseDto;
import com.tata.account.dto.DataSessionDto;
import com.tata.account.entity.Account;
import com.tata.account.repository.AccountRepository;
import com.tata.account.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.MediaType.APPLICATION_JSON;

@SpringBootTest
@Transactional
class AccountServiceIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private ClientAndServer mockServer;
    private String authToken;

    @BeforeEach
    void setUp() {
        mockServer = startClientAndServer(8070);
        authToken = loginAndGetToken(); // Realizar login y obtener token
        setupAuthentication(authToken); // Configurar autenticación en contexto de seguridad
    }

    private String loginAndGetToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String loginUrl = "http://localhost:8080/api/auth/login";

            // Crear el cuerpo de la solicitud en formato JSON
            String requestBody = """
                    {
                      "username": "higueraf",
                      "password": "SecurePassword123"
                    }
                    """;

            // Configurar los encabezados de la solicitud
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear la solicitud HTTP
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Enviar la solicitud POST y recibir la respuesta
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

            // Parsear el JSON para extraer el token
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("data").path("token").asText();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo obtener el token de autenticación", e);
        }
    }

    private void setupAuthentication(String token) {
        DataSessionDto fakeUser = new DataSessionDto();
        fakeUser.setUserId("7cd409e9-1791-4cad-8866-1592aa12faa4");
        fakeUser.setRoles("ADMIN");
        fakeUser.setToken(token);

        TestingAuthenticationToken authToken = new TestingAuthenticationToken(fakeUser, null);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.stop();
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateAccountForCustomer() {
        UUID customerId = UUID.fromString("6fbcb8bd-2fad-487a-ab92-20a210dfe353");

        mockServer.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/customers/" + customerId)
        ).respond(
                HttpResponse.response()
                        .withBody("{ \"id\": \"" + customerId + "\", \"name\": \"Jose Lema\" }")
                        .withContentType(APPLICATION_JSON)
        );

        AccountRequestDto accountRequest = new AccountRequestDto();
        accountRequest.setAccountNumber("123456");
        accountRequest.setInitialBalance(1000);
        accountRequest.setCustomerId(customerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<AccountRequestDto> entity = new HttpEntity<>(accountRequest, headers);
        AccountResponseDto response = accountService.createAccount(accountRequest);

        Optional<Account> savedAccount = accountRepository.findById(response.getId());
        assertTrue(savedAccount.isPresent());
        assertEquals("123456", savedAccount.get().getAccountNumber());
    }
}
