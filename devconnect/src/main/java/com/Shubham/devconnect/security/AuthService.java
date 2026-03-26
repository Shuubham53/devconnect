package com.Shubham.devconnect.security;

import com.Shubham.devconnect.dto.request.LoginRequest;
import com.Shubham.devconnect.dto.request.RegisterRequest;
import com.Shubham.devconnect.dto.request.VerifyOtpRequest;
import com.Shubham.devconnect.dto.response.AuthResponse;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.Role;
import com.Shubham.devconnect.repository.UserRepository;
import com.Shubham.devconnect.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public String register(RegisterRequest request){
        // 6 digit random OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(10);
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
                .otp(otp)
                .isActive(false)
                .otpExpiry(otpExpiry)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
         user = userRepository.save(user);
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
        return          "Registration successful , Please verify your email with OTP sent to " + user.getEmail();

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
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->
                new UsernameNotFoundException("User not found"));
        if(!user.getOtp().equals(request.getOtp())){
            throw new  RuntimeException("Invalid OTP");
        }
        if(user.getOtpExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP expired. Please register again");
        }
        user.setIsActive(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .username(user.getActualUsername())
                .email(user.getEmail())
                .token(token)
                .role(user.getRole().toString())
                .build();

    }

}
