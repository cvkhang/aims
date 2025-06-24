package com.aims.model;

import com.aims.entity.Order;
import com.aims.entity.OrderItem;
import com.aims.entity.Product;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ShippingFeeCalculatorTest {
    @Test
    void testCalculateShippingFee() {
        // OrderMedia 1
        Order order1 = new Order();
        order1.setProvinceCity("Hanoi");
        order1.setRushDelivery(true);
        OrderItem item1 = createItem(2, 10000);
        double fee1 = ShippingFeeCalculator.calculateShippingFee(order1, Collections.singletonList(item1));
        assertEquals(32000, fee1);

        // OrderMedia 2
        Order order2 = new Order();
        order2.setProvinceCity("ThanhHoa");
        order2.setRushDelivery(false);
        OrderItem item2 = createItem(2, 10000);
        double fee2 = ShippingFeeCalculator.calculateShippingFee(order2, Collections.singletonList(item2));
        assertEquals(37500, fee2);

        // OrderMedia 3
        Order order3 = new Order();
        order3.setProvinceCity("Hanoi");
        order3.setRushDelivery(false);
        OrderItem item3 = createItem(2, 10000);
        double fee3 = ShippingFeeCalculator.calculateShippingFee(order3, Collections.singletonList(item3));
        assertEquals(22000, fee3);
    }

    private OrderItem createItem(double weight, double price) {
        Product p = new Product();
        p.setWeight(weight);
        OrderItem item = new OrderItem();
        item.setProduct(p);
        item.setQuantity(1);
        item.setPrice(price);
        return item;
    }
}
