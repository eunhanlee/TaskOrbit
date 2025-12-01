package com.eunhanlee.taskorbit.service;

import com.eunhanlee.taskorbit.dto.AuthRequest;
import com.eunhanlee.taskorbit.dto.AuthResponse;
import com.eunhanlee.taskorbit.entity.User;
import com.eunhanlee.taskorbit.repository.UserRepository;
import com.eunhanlee.taskorbit.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(AuthRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for username/email: {}", request.getUsername());
        
        // Find user by username or email
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(userRepository.findByEmail(request.getUsername())
                        .orElse(null));

        if (user == null) {
            log.warn("User not found: {}", request.getUsername());
            throw new RuntimeException("Invalid username/email or password");
        }

        log.info("User found: {}", user.getUsername());
        
        // Verify password
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info("Password match: {}", passwordMatches);
        
        if (!passwordMatches) {
            log.warn("Password mismatch for user: {}", user.getUsername());
            throw new RuntimeException("Invalid username/email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        log.info("Login successful for user: {}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .message("Login successful")
                .build();
    }
}

