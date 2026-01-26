package com.example;

import com.example.inventory.repository.ProductRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@Testcontainers
class OrderInventoryServiceApplicationTests {
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

	@DynamicPropertySource
	static void props(DynamicPropertyRegistry r) {
		r.add("spring.datasource.url", postgres::getJdbcUrl);
		r.add("spring.datasource.username", postgres::getUsername);
		r.add("spring.datasource.password", postgres::getPassword);
		r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
		r.add("spring.flyway.enabled", () -> "true");
	}
	@Test
	void contextLoads() {
	}

}
