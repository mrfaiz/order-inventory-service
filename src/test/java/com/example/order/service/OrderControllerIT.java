package com.example.order.service;

import com.example.inventory.api.AddProductRequest;
import com.example.inventory.api.ProductResponse;
import com.example.inventory.domain.Product;
import com.example.order.api.OrderLineRequest;
import com.example.order.api.OrderResponse;
import com.example.order.api.OrderSummaryResponse;
import com.example.order.api.PlaceOrderRequest;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.order.domain.Order;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
@Testcontainers
@SpringBootTest(properties = "spring.flyway.clean-disabled=false")
@AutoConfigureMockMvc
class OrderControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("order_inventory")
            .withUsername("app")
            .withPassword("app");
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        r.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    Flyway flyway;

    @BeforeEach
    void resetDb() {
        flyway.clean();
        flyway.migrate();
    }


    @Autowired
    MockMvc mockMvc;
    @Autowired InventoryItemRepository inventoryRepo;

    @Test
    void placeOrder_deductsInventory() throws Exception {
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        int beforeQty = inventoryRepo.findByProduct_Id(productId)
                .orElseThrow(() -> new IllegalStateException("Seed inventory missing for productId=" + productId))
                .getQuantity();

        String key = UUID.randomUUID().toString();
        PlaceOrderRequest req = new PlaceOrderRequest(
                List.of(new OrderLineRequest(productId, 2 ))
        );

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(responseBody.replace("\"", ""));
        assertNotNull(id);


        int afterQty = inventoryRepo.findByProduct_Id(productId)
                .orElseThrow()
                .getQuantity();
        assertEquals(beforeQty - 2, afterQty);
    }

    @Test
    void order_lines() throws Exception{
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        PlaceOrderRequest req = new PlaceOrderRequest(
                List.of(new OrderLineRequest(productId, 2))
        );
       String res = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        UUID id = UUID.fromString(res.replace("\"", ""));
        var result = mockMvc.perform(get("/orders/{orderId}", id))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        OrderResponse response = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(productId, response.lines().getFirst().productId());

    }

    @Test
    void orderSummary() throws Exception{
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        PlaceOrderRequest req = new PlaceOrderRequest(
                List.of(new OrderLineRequest(productId, 2))
        );
        String res = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        UUID id = UUID.fromString(res.replace("\"", ""));
        var result = mockMvc.perform(get("/orders/summary"))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        OrderSummaryResponse[] response = objectMapper.readValue(json, new TypeReference<>() {});
        assertEquals(1, response.length);
        assertEquals(id, response[0].id());
        assertEquals(1,response[0].lineCount());

    }

    @Test
    void placeOrder_idempotency() throws Exception{
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        String id = UUID.randomUUID().toString();
        PlaceOrderRequest req = new PlaceOrderRequest(
                List.of(new OrderLineRequest(productId, 2))
        );
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key",id)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var before = inventoryRepo.findByProduct_Id(productId).orElseThrow();
         assertEquals(48, before.getQuantity());

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var after = inventoryRepo.findByProduct_Id(productId).orElseThrow();
        assertEquals(48, after.getQuantity());
    }
}