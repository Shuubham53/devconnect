package com.Shubham.devconnect.config;


import com.Shubham.devconnect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.Shubham.devconnect.security.UserDetailsServiceImpl;

import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationConfig
        implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message,
                                      MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(
                        accessor.getCommand())) {
                    List<String> authorization =
                            accessor.getNativeHeader("Authorization");

                    if (authorization != null &&
                            !authorization.isEmpty()) {
                        String token = authorization.get(0)
                                .replace("Bearer ", "");
                        String email = jwtUtil.extractUsername(token);

                        if (email != null &&
                                jwtUtil.validateToken(token, email)) {
                            UserDetails userDetails =
                                    userDetailsService
                                            .loadUserByUsername(email);
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null,
                                            userDetails.getAuthorities());
                            accessor.setUser(auth);
                        }
                    }
                }
                return message;
            }
        });
    }
}