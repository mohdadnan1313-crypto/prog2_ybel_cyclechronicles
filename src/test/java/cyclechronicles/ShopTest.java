package cyclechronicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopTest {

    private Shop shop;

    @BeforeEach
    public void setUp() {
        // Create a fresh, empty shop before every single test
        shop = new Shop();
    }

    // Helper method to quickly create a mocked order
    private Order createMockOrder(Type bikeType, String customerName) {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(bikeType);
        when(mockOrder.getCustomer()).thenReturn(customerName);
        return mockOrder;
    }

    // ==========================================
    // TASK 2.2: ACCEPTANCE TESTS
    // ==========================================

    // --- TEST 1: The Happy Path (Valid Equivalence Class) ---
    @Test
    public void testAccept_ValidOrder_ReturnsTrue() {
        Order order = createMockOrder(Type.RACE, "Alice");
        assertTrue(shop.accept(order), "A standard bike order from a new customer should be accepted.");
    }

    // --- TEST 2: Invalid Equivalence Class 1 (E-Bike) ---
    @Test
    public void testAccept_EBike_ReturnsFalse() {
        Order order = createMockOrder(Type.EBIKE, "Bob");
        assertFalse(shop.accept(order), "E-Bikes should be rejected.");
    }

    // --- TEST 3: Invalid Equivalence Class 2 (Gravel Bike) ---
    @Test
    public void testAccept_GravelBike_ReturnsFalse() {
        Order order = createMockOrder(Type.GRAVEL, "Charlie");
        assertFalse(shop.accept(order), "Gravel-Bikes should be rejected.");
    }

    // --- TEST 4: Invalid Boundary Value (Customer Limit = 1) ---
    @Test
    public void testAccept_CustomerAlreadyHasOpenOrder_ReturnsFalse() {
        // First order is accepted
        Order order1 = createMockOrder(Type.RACE, "David");
        shop.accept(order1);

        // Second order from the SAME customer should be rejected
        Order order2 = createMockOrder(Type.FIXIE, "David");
        assertFalse(shop.accept(order2), "A customer cannot have more than one open order.");
    }

    // --- TEST 5: Invalid Boundary Value (Shop Limit = 5) ---
    @Test
    public void testAccept_ShopIsFull_ReturnsFalse() {
        // Fill the shop with 5 valid orders from 5 different customers
        shop.accept(createMockOrder(Type.RACE, "Customer1"));
        shop.accept(createMockOrder(Type.RACE, "Customer2"));
        shop.accept(createMockOrder(Type.RACE, "Customer3"));
        shop.accept(createMockOrder(Type.RACE, "Customer4"));
        shop.accept(createMockOrder(Type.RACE, "Customer5"));

        // The 6th order should be rejected
        Order order6 = createMockOrder(Type.RACE, "Customer6");
        assertFalse(shop.accept(order6), "The shop cannot accept more than 5 orders.");
    }

    // ==========================================
    // BONUS TASK 2.3: REPAIR & DELIVER TESTS
    // ==========================================

    @Test
    public void testRepair_WithPendingOrders_ReturnsOldestOrder() {
        Order firstOrder = createMockOrder(Type.RACE, "Alice");
        Order secondOrder = createMockOrder(Type.RACE, "Bob");
        
        shop.accept(firstOrder);
        shop.accept(secondOrder);
        
        // Repair should process Alice first (FIFO)
        Optional<Order> repaired = shop.repair();
        assertTrue(repaired.isPresent(), "Repair should return an order if pending orders exist.");
        assertEquals("Alice", repaired.get().getCustomer(), "Repair should process the oldest order first.");
    }

    @Test
    public void testRepair_EmptyShop_ReturnsEmpty() {
        Optional<Order> repaired = shop.repair();
        assertTrue(repaired.isEmpty(), "Repair should return empty if there are no pending orders.");
    }

    @Test
    public void testDeliver_WithCompletedOrder_ReturnsOrder() {
        Order order = createMockOrder(Type.RACE, "Charlie");
        shop.accept(order);
        shop.repair(); // Move from pending to completed
        
        Optional<Order> delivered = shop.deliver("Charlie");
        assertTrue(delivered.isPresent(), "Deliver should return the completed order.");
        assertEquals("Charlie", delivered.get().getCustomer());
        
        // Delivering again should return empty (it was removed from the shop)
        assertTrue(shop.deliver("Charlie").isEmpty(), "Order should be removed after delivery.");
    }

    @Test
    public void testDeliver_OrderNotRepairedYet_ReturnsEmpty() {
        Order order = createMockOrder(Type.RACE, "David");
        shop.accept(order);
        // We forgot to call repair()!
        
        Optional<Order> delivered = shop.deliver("David");
        assertTrue(delivered.isEmpty(), "Deliver should return empty if the order is still pending.");
    }
}