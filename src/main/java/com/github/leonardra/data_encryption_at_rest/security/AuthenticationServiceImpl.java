package com.github.leonardra.data_encryption_at_rest.security;


import com.github.leonardra.data_encryption_at_rest.dtos.requests.AuthRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.AuthResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.AuthUserResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.JWTResponse;
import com.github.leonardra.data_encryption_at_rest.enums.AuthTokenType;
import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class  AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;


    @Transactional
    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<User> optionalUser = userService.findByUsername(authRequest.getUsername());

        if (optionalUser.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Invalid username or password");
        }

        if (!optionalUser.get().isEnabled()) {
            throw new AuthorizationServiceException("User is not enabled");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        User user = optionalUser.get();
        UserAuthResponse authToken = generateAuthTokensForUser(user);

        return AuthResponse.builder()
                .accessToken(authToken.getAccessToken())
                .expiredAt(authToken.getExpiredAt())
                .user(new AuthUserResponse(user.getUsername()))
                .build();
    }


    private UserAuthResponse generateAuthTokensForUser(User user) {
        JWTResponse jwtAccessTokenObject = jwtService.generateToken(new CustomUserDetails(user), AuthTokenType.BEARER);

        return UserAuthResponse.builder()
                .accessToken(jwtAccessTokenObject.getJwt())
                .expiredAt(jwtAccessTokenObject.getExpiredAt())
                .build();
    }

    @Override
    public User getCurrentlyLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication) {
            throw new AuthorizationServiceException("Access denied");
        }


        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        return userService.findByUsername(username)
                .orElseThrow(() -> new AuthorizationServiceException("Access denied"));
    }

}
