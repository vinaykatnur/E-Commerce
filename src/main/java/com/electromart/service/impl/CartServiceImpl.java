package com.electromart.service.impl;

import com.electromart.dto.CartDto;
import com.electromart.dto.CartItemDto;
import com.electromart.entity.Cart;
import com.electromart.entity.CartItem;
import com.electromart.entity.Product;
import com.electromart.entity.User;
import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.repository.CartItemRepository;
import com.electromart.repository.CartRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.CartService;
import com.electromart.service.CurrentUserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartForCurrentUser() {
        return toDto(getCurrentUserCartEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartCountForCurrentUser() {
        try {
            return getCurrentUserCartEntity().getItems().stream().mapToInt(CartItem::getQuantity).sum();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    @Transactional
    public void addProduct(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        Cart cart = getCurrentUserCartEntity();
        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).orElse(null);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(0);
            cart.getItems().add(cartItem);
        }

        int updatedQuantity = cartItem.getQuantity() + quantity;
        if (updatedQuantity > product.getStock()) {
            throw new AppException("Only " + product.getStock() + " units are available for " + product.getName() + ".");
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void increaseQuantity(Long cartItemId) {
        CartItem item = getOwnedCartItem(cartItemId);
        if (item.getQuantity() + 1 > item.getProduct().getStock()) {
            throw new AppException("No more stock available for this product.");
        }
        item.setQuantity(item.getQuantity() + 1);
    }

    @Override
    @Transactional
    public void decreaseQuantity(Long cartItemId) {
        CartItem item = getOwnedCartItem(cartItemId);
        if (item.getQuantity() <= 1) {
            item.getCart().getItems().remove(item);
            cartItemRepository.delete(item);
            return;
        }
        item.setQuantity(item.getQuantity() - 1);
    }

    @Override
    @Transactional
    public void removeItem(Long cartItemId) {
        CartItem item = getOwnedCartItem(cartItemId);
        item.getCart().getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCurrentUserCartEntity() {
        User user = currentUserService.getCurrentUser();
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found."));
    }

    @Override
    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartItem getOwnedCartItem(Long cartItemId) {
        Cart cart = getCurrentUserCartEntity();
        return cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found."));
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            BigDecimal totalPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(totalPrice);
            totalItems += item.getQuantity();
            items.add(CartItemDto.builder()
                    .cartItemId(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .brand(item.getProduct().getBrand())
                    .imageUrl(item.getProduct().getImageUrl())
                    .unitPrice(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(totalPrice)
                    .build());
        }

        return CartDto.builder()
                .cartId(cart.getId())
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
    }
}
