package cyclechronicles;

import java.util.*;

/** A small bike shop. */
public class Shop {
    private final Queue<Order> pendingOrders = new LinkedList<>();
    private final Set<Order> completedOrders = new HashSet<>();

    /**
     * Accept a repair order.
     * ...
     */
    public boolean accept(Order o) {
        if (o.getBicycleType() == Type.GRAVEL) return false;
        if (o.getBicycleType() == Type.EBIKE) return false;
        if (pendingOrders.stream().anyMatch(x -> x.getCustomer().equals(o.getCustomer()))) return false;
        if (pendingOrders.size() > 4) return false;

        return pendingOrders.add(o);
    }

    /**
     * Take the oldest pending order and repair this bike.
     */
    public Optional<Order> repair() {
        // If there are no pending orders, return empty
        if (pendingOrders.isEmpty()) {
            return Optional.empty();
        }
        
        // Take the oldest order out of the queue and add it to completed
        Order finishedBike = pendingOrders.poll();
        completedOrders.add(finishedBike);
        
        return Optional.of(finishedBike);
    }

    /**
     * Deliver a repaired bike to a customer.
     */
    public Optional<Order> deliver(String c) {
        // Find a completed order that matches the customer name
        Optional<Order> orderToDeliver = completedOrders.stream()
                .filter(order -> order.getCustomer().equals(c))
                .findFirst();
        
        // If we found it, remove it from the shop's inventory
        orderToDeliver.ifPresent(order -> completedOrders.remove(order));
        
        return orderToDeliver;
    }
}