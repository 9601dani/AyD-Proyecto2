package com.bugtrackers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class AppTest {
    
    @Test
    void testApp() {
        App app = new App();
        assertNotNull(app);
    }

    @Test
    void testMain() {
        App.main(new String[] {});
    }
}
