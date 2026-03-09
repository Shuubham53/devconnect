package com.Shubham.devconnect.security;

import com.Shubham.devconnect.dto.request.LoginRequest;
import com.Shubham.devconnect.dto.request.RegisterRequest;
import com.Shubham.devconnect.dto.response.AuthResponse;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.Role;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("User already exist with email "+request.getEmail());
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("User already exist with username "+request.getUsername());
        }
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
         user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .email(user.getEmail())
                .username(user.getActualUsername())
                .role(String.valueOf(user.getRole()))
                .token(token)
                .build();

    }
    public AuthResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->
                new UsernameNotFoundException("user not found with email "+request.getEmail()));
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .email(user.getEmail())
                .username(user.getActualUsername())
                .role(String.valueOf(user.getRole()))
                .token(token)
                .build();
    }

}
