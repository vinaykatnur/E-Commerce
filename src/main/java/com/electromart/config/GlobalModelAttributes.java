package com.electromart.config;

import com.electromart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final CartService cartService;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        boolean authenticated = authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute("currentUserEmail", authenticated ? authentication.getName() : null);
        model.addAttribute("cartCount", authenticated ? cartService.getCartCountForCurrentUser() : 0);
    }
}
