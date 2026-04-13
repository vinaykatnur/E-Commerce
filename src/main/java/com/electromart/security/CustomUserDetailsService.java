package com.electromart.security;

import com.electromart.entity.User;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getFullName());
    }
}
