package com.electromart.controller;

import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.service.AddressService;
import com.electromart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AddressService addressService;
    @Value("${app.google.maps.api-key:}")
    private String googleMapsApiKey;

    @GetMapping("/cart")
    public String viewCart(Model model) {
        log.info("Rendering cart page");
        model.addAttribute("cart", cartService.getCartForCurrentUser());
        model.addAttribute("addresses", addressService.getAddressesForCurrentUser());
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            Authentication authentication,
                            @RequestHeader(value = "Referer", required = false) String referer,
                            RedirectAttributes redirectAttributes) {
        log.info("Add to cart requested for product {} by user {}", productId, authentication != null ? authentication.getName() : "anonymous");
        try {
            cartService.addProduct(productId, 1);
            redirectAttributes.addFlashAttribute("success", "Added to cart");
            log.info("Product {} added to cart successfully", productId);
        } catch (AppException | ResourceNotFoundException | IllegalArgumentException exception) {
            log.warn("Add to cart failed for product {}: {}", productId, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not add item");
        }
        return "redirect:" + resolveRedirectTarget(referer);
    }

    @GetMapping("/addToCart/{productId}")
    public String addToCartShortcut(@PathVariable Long productId,
                                    Authentication authentication,
                                    @RequestHeader(value = "Referer", required = false) String referer,
                                    RedirectAttributes redirectAttributes) {
        return addToCart(productId, authentication, referer, redirectAttributes);
    }

    @PostMapping("/cart/increase/{cartItemId}")
    public String increaseQuantity(@PathVariable Long cartItemId, RedirectAttributes redirectAttributes) {
        log.info("Increase cart quantity requested for cartItemId={}", cartItemId);
        try {
            cartService.increaseQuantity(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Cart updated");
            log.info("Increase cart quantity completed for cartItemId={}", cartItemId);
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Increase cart quantity failed for cartItemId={}: {}", cartItemId, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not update quantity");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/decrease/{cartItemId}")
    public String decreaseQuantity(@PathVariable Long cartItemId, RedirectAttributes redirectAttributes) {
        log.info("Decrease cart quantity requested for cartItemId={}", cartItemId);
        try {
            cartService.decreaseQuantity(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Cart updated");
            log.info("Decrease cart quantity completed for cartItemId={}", cartItemId);
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Decrease cart quantity failed for cartItemId={}: {}", cartItemId, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not update quantity");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{cartItemId}")
    public String removeItem(@PathVariable Long cartItemId, RedirectAttributes redirectAttributes) {
        log.info("Remove cart item requested for cartItemId={}", cartItemId);
        try {
            cartService.removeItem(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart");
            log.info("Remove cart item completed for cartItemId={}", cartItemId);
        } catch (ResourceNotFoundException exception) {
            log.warn("Remove cart item failed for cartItemId={}: {}", cartItemId, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not remove item");
        }
        return "redirect:/cart";
    }

    private String resolveRedirectTarget(String referer) {
        if (referer == null || referer.isBlank()) {
            return "/cart";
        }
        if (referer.contains("/products") || referer.endsWith("/")) {
            return referer;
        }
        return "/cart";
    }
}
