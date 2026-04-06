package com.ecom.order.service;

import com.ecom.order.dto.OrderCreatedEvent;
import com.ecom.order.dto.OrderItemDTO;
import com.ecom.order.dto.OrderResponse;
import com.ecom.order.model.CartItem;
import com.ecom.order.model.Order;
import com.ecom.order.model.OrderItem;
import com.ecom.order.model.OrderStatus;
import com.ecom.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(Long userId) {
            List<CartItem> cartItems = cartService.getCart(userId);
            if(cartItems.isEmpty()){
                return Optional.empty();
            }

            //calculate  total price
            BigDecimal totalPrice = cartItems.stream().map(CartItem::getPrice).reduce(BigDecimal.ZERO,BigDecimal::add);

            // create new order
            Order order = new Order();
            order.setUserId(userId);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTotalAmount(totalPrice);

            //convert cartitems to orderitems and add it to order object
            List<OrderItem> orderItems = cartItems.stream()
                    .map(item -> new OrderItem(
                            null,
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice(),
                            order
                    )).toList();
            order.setItems(orderItems);
            Order savedOrder = orderRepository.save(order);


            // clear the cart
            cartService.clearCart(userId);

            OrderCreatedEvent event =  new OrderCreatedEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    savedOrder.getStatus(),
                    mapToOrderItemDTOs(savedOrder.getItems()),
                    savedOrder.getTotalAmount()
            );

            return Optional.of(mapToOrderResponse(savedOrder));
            /*
            Fetch CartItems
            Convert → OrderItems
            Calculate total
            Save Order
            Clear Cart
            Publish Order created event

             */
    }

    private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                )).collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList());
    }

    public Order getOrderById(Long orderId) {
       return orderRepository.getByOrderId(orderId);
    }

    /* TODO
     // ✅ 3. Get All Orders for User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // ✅ 4. Get All Orders (Admin use)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ 5. Update Order Status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    // ✅ 6. Cancel Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully");
    }
     */

}
