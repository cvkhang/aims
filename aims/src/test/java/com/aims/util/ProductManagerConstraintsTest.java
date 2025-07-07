package com.aims.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductManagerConstraintsTest {
    
    @Test
    public void testCanAddProduct() {
        // Product managers can add unlimited products
        assertTrue(ProductManagerConstraints.canAddProduct(1));
        assertTrue(ProductManagerConstraints.canAddProduct(999));
    }
    
    @Test
    public void testCanEditProduct() {
        // Product managers can edit one product at a time
        assertTrue(ProductManagerConstraints.canEditProduct(1));
        assertTrue(ProductManagerConstraints.canEditProduct(999));
    }
    
    @Test
    public void testValidatePrice() {
        // Test price validation (30% to 150% of value)
        double value = 100.0;
        
        // Valid prices
        assertNull(ProductManagerConstraints.validatePrice(50.0, value));  // 50%
        assertNull(ProductManagerConstraints.validatePrice(100.0, value)); // 100%
        assertNull(ProductManagerConstraints.validatePrice(150.0, value)); // 150%
        
        // Invalid prices
        assertNotNull(ProductManagerConstraints.validatePrice(29.0, value)); // 29% - too low
        assertNotNull(ProductManagerConstraints.validatePrice(151.0, value)); // 151% - too high
    }
    
    @Test
    public void testCanDeleteProducts() {
        // Test bulk delete limits
        String error = ProductManagerConstraints.canDeleteProducts(1, 11);
        assertNotNull(error);
        assertTrue(error.contains("Cannot delete more than 10 products at once"));
        
        // Test daily limits (this would require database setup)
        // For now, just test the method doesn't throw exceptions
        assertDoesNotThrow(() -> ProductManagerConstraints.canDeleteProducts(1, 5));
    }
    
    @Test
    public void testCanUpdateProducts() {
        // Test daily update limits (this would require database setup)
        // For now, just test the method doesn't throw exceptions
        assertDoesNotThrow(() -> ProductManagerConstraints.canUpdateProducts(1, 5));
    }
    
    @Test
    public void testCanUpdatePrice() {
        // Test price update limits (this would require database setup)
        // For now, just test the method doesn't throw exceptions
        assertDoesNotThrow(() -> ProductManagerConstraints.canUpdatePrice(1, 1));
    }
    
    @Test
    public void testGetTimeUntilReset() {
        String timeUntilReset = ProductManagerConstraints.getTimeUntilReset();
        assertNotNull(timeUntilReset);
        assertTrue(timeUntilReset.matches("\\d{2}:\\d{2}")); // Format: HH:MM
    }
} 