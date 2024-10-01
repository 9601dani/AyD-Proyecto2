package com.bugtrackers.ms_auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MsAuthApplication.class)
@AutoConfigureMockMvc
public class MsAuthApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void shouldReturnIsOk() throws Exception {
		mockMvc.perform(get("/"))
		.andExpect(status().isOk());
	}

}
