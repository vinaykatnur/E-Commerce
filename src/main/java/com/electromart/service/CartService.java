package com.electromart.service;

import com.electromart.dto.CartDto;
import com.electromart.entity.Cart;

public interface CartService {

    CartDto getCartForCurrentUser();

    int getCartCountForCurrentUser();

    void addProduct(Long productId, int quantity);

    void increaseQuantity(Long cartItemId);

    void decreaseQuantity(Long cartItemId);

    void removeItem(Long cartItemId);

    Cart getCurrentUserCartEntity();

    void clearCart(Cart cart);
}
