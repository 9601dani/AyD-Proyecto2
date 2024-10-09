package com.bugtrackers.ms_auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
public class MsAuthApplicationTest {
    

    @Test
    void contextLoads() {
    }

    @Test
    void testMain() {
        MsAuthApplication.main(new String[] {});
    }
}
