package com.example.auth.api;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.TokenResponse;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import tools.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@Testcontainers
@SpringBootTest(properties = "spring.flyway.clean-disabled=false")
@AutoConfigureMockMvc
public class AuthControllerIT {

    @Autowired
    Flyway flyway;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetDd(){
        flyway.clean();
        flyway.migrate();
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("order_inventory")
            .withUsername("app")
            .withPassword("app");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        r.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    MockMvc mockMvc;
    // ---------- helper ----------
    private String loginAndGetToken() throws Exception {
        var login = new LoginRequest("admin", "password");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, TokenResponse.class).token();
    }
    @Test
    void login_successful() throws Exception{
        var request = new LoginRequest("admin", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").value(containsString(".")));
    }

    @Test
    void securedEndpoint_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void securedEndpoint_withJwt_succeeds() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/health")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}
