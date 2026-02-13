package com.github.leonardra.data_encryption_at_rest.filters;

import com.github.leonardra.data_encryption_at_rest.exceptions.SecurityErrorHandler;
import com.github.leonardra.data_encryption_at_rest.models.Key;
import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.security.AuthenticationService;
import com.github.leonardra.data_encryption_at_rest.utils.EncryptionContext;
import com.github.leonardra.data_encryption_at_rest.utils.EncryptionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@RequiredArgsConstructor
public class EncryptionSecurityFilter extends OncePerRequestFilter {
    private static final Logger LOG = getLogger(EncryptionSecurityFilter.class);
    private final EncryptionContext encryptionContext;
    private final AuthenticationService authenticationService;
    private final SecurityErrorHandler securityErrorHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String password = request.getHeader("X-Vault-PWD");

        if (password != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            try {
                User user = authenticationService.getCurrentlyLoggedInUser();
                Key key = user.getKey();
                encryptionContext.init(
                        key.getUserHalf(),
                        password,
                        key.getUserHalfIv(),
                        key.getSystemSalt()
                );

            } catch (Exception e) {
                LOG.error("Failed to initialize encryption context", e);
                securityErrorHandler.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Encryption Init Failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
