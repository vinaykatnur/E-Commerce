package com.electromart.controller;

import com.electromart.config.JwtProperties;
import com.electromart.dto.LoginRequest;
import com.electromart.dto.RegisterRequest;
import com.electromart.exception.AppException;
import com.electromart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @GetMapping("/login")
    public String loginPage(Model model, Authentication authentication) {
        log.info("Rendering login page for user {}", authentication != null ? authentication.getName() : "anonymous");
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        log.info("Register request received for email {}", registerRequest.getEmail());
        if (bindingResult.hasErrors()) {
            log.warn("Register validation failed for email {} with {} errors", registerRequest.getEmail(), bindingResult.getErrorCount());
            model.addAttribute("registerRequest", registerRequest);
            return "register";
        }
        try {
            authService.register(registerRequest);
        } catch (AppException exception) {
            log.warn("Register failed for email {}: {}", registerRequest.getEmail(), exception.getMessage());
            model.addAttribute("error", "Username already taken");
            model.addAttribute("registerRequest", registerRequest);
            return "register";
        }
        log.info("Register succeeded for email {}", registerRequest.getEmail());
        redirectAttributes.addFlashAttribute("success", "Account created successfully. Please sign in.");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        log.info("Login request received for email {}", loginRequest.getEmail());
        if (bindingResult.hasErrors()) {
            log.warn("Login validation failed for email {} with {} errors", loginRequest.getEmail(), bindingResult.getErrorCount());
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }
        String token;
        try {
            token = authService.login(loginRequest);
        } catch (AuthenticationException exception) {
            log.warn("Login failed for email {}: {}", loginRequest.getEmail(), exception.getMessage());
            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        log.info("Login successful for user {}", loginRequest.getEmail());
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie(token, (int) (jwtProperties.expirationMs() / 1000)).toString());
        redirectAttributes.addFlashAttribute("success", "Welcome back. You are now signed in.");
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout requested");
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie("", 0).toString());
        return "redirect:/login?logout";
    }

    private ResponseCookie buildAuthCookie(String token, int maxAgeSeconds) {
        return ResponseCookie.from(jwtProperties.cookieName(), token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
