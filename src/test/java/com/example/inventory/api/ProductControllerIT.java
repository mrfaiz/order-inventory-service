package com.example.inventory.api;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@Testcontainers
@SpringBootTest(properties = "spring.flyway.clean-disabled=false")
@AutoConfigureMockMvc
public class ProductControllerIT {

    @Autowired
    Flyway flyway;

    @BeforeEach
    void resetDb() {
        flyway.clean();
        flyway.migrate();
    }


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("order_inventory")
            .withUsername("app")
            .withPassword("app");
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

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

    @Test
    void product_getAll() throws Exception{

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<ProductResponse> products = objectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {
        });
        assertEquals(3,products.size());
    }

    @Test
    void product_addNewProduct() throws Exception {
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<ProductResponse> products = objectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {
        });

        assertEquals(3,products.size());
        UUID uuid= UUID.randomUUID();
        AddProductRequest req = new AddProductRequest(
                List.of(new ProductResponse(uuid, "sku"+uuid, "test", 100))
        );
        mockMvc.perform(post("/products/add")
                        .with(user("user").password("pass123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        org.springframework.test.web.servlet.MvcResult result2 = mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andReturn();
        String json2 = result2.getResponse().getContentAsString();
        List<ProductResponse> products2 = objectMapper.readValue(json2, new TypeReference<List<ProductResponse>>() {
        });

        assertEquals(4,products2.size());
    }
    @Test
    void product_updateProduct() throws Exception {

        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Product p = productRepository.findById(id).orElseThrow();
        assertEquals("Apple", p.getName());

        AddProductRequest req = new AddProductRequest(List.of(
                new ProductResponse(id, "NEW-SKU", "New Name", 4000)
        ));

        mockMvc.perform(put("/products").with(user("user").password("pass123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        Product product = productRepository.findById(id).orElseThrow();
        assertEquals("NEW-SKU", product.getSku());
        assertEquals("New Name", product.getName());
    }

    @Test
    void product_delete() throws Exception {

        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Product p = productRepository.findById(id).orElseThrow();
        assertEquals("Apple", p.getName());


        mockMvc.perform(delete("/products/{id}", id).with(user("user").password("pass123")))
                .andExpect(status().isConflict());

        UUID id2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Product p2 = productRepository.save(new Product(id2, "sku-new", "ddd", 3333, Instant.now()));


        mockMvc.perform(delete("/products/{id}", id2).with(user("user").password("pass123")))
                .andExpect(status().isNoContent());

    }
    }
