package com.electromart.service.impl;

import com.electromart.dto.LoginRequest;
import com.electromart.dto.RegisterRequest;
import com.electromart.entity.Cart;
import com.electromart.entity.User;
import com.electromart.exception.AppException;
import com.electromart.repository.CartRepository;
import com.electromart.repository.UserRepository;
import com.electromart.security.CustomUserDetailsService;
import com.electromart.security.JwtTokenProvider;
import com.electromart.security.UserPrincipal;
import com.electromart.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new AppException("An account with this email already exists.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        Cart savedCart = cartRepository.save(cart);
        savedUser.setCart(savedCart);
        return savedUser;
    }

    @Override
    public String login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return jwtTokenProvider.generateToken(principal);
    }
}
