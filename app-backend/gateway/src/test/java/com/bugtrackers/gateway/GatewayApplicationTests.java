package com.bugtrackers.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testRoute() {
		// webTestClient.get()
		// 		.uri("/auth")
		// 		.exchange()
		// 		.expectStatus().isOk()
		// 		.expectBody(String.class)
		// 		.isEqualTo("Hello World Auth Controller!");
	}

}
