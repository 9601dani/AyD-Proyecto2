package com.bugtrackers.ms_img;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class MsImgApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testMain() {
		MsImgApplication.main(new String[] {});
	}

}
