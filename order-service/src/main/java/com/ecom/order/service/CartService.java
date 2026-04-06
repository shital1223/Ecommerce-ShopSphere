package com.ecom.order.service;

import com.ecom.order.clients.ProductServiceClient;
import com.ecom.order.clients.UserServiceClient;
import com.ecom.order.dto.CartItemRequest;
import com.ecom.order.dto.ProductResponse;
import com.ecom.order.dto.UserResponse;
import com.ecom.order.model.CartItem;
import com.ecom.order.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public boolean addToCart(Long userId, CartItemRequest cartReq) {
        // validation
        // 1. check if product is valid and have enough stock

        ProductResponse productResponse = productServiceClient.getProductDetails(cartReq.getProductId());
        if(productResponse == null || productResponse.getStockQuantity() < cartReq.getQuantity())
            return false;

        //2. check if current user is valid user

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null)
            return false;

        // if product is already in current user cart, increase the quantity of that product , else add product to cart.
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, cartReq.getProductId());
        if (existingCartItem != null) {
            // Update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartReq.getQuantity());
            existingCartItem.setPrice(existingCartItem.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartReq.getProductId());
            cartItem.setQuantity(cartReq.getQuantity());
            cartItem.setPrice(productResponse.getPrice());
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(Long userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
