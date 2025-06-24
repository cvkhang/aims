package com.aims.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    @Test
    void testRushOrderSupport() {
        Product product1 = new Product();
        product1.setRushEligible(true);
        assertTrue(product1.isRushEligible());

        Product product2 = new Product();
        product2.setRushEligible(false);
        assertFalse(product2.isRushEligible());
    }
}
