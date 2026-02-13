package com.github.leonardra.data_encryption_at_rest.security;


import com.github.leonardra.data_encryption_at_rest.enums.AuthTokenType;
import com.github.leonardra.data_encryption_at_rest.exceptions.SecurityErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.github.leonardra.data_encryption_at_rest.security.WhitelistUtil.WHITE_LIST_URL;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SecurityErrorHandler securityErrorHandler;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   SecurityErrorHandler securityErrorHandler) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.securityErrorHandler = securityErrorHandler;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isWhitelisted(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            username = jwtService.extractSubject(jwt, AuthTokenType.BEARER);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails, AuthTokenType.BEARER)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    throw new AuthenticationServiceException("Login session expired");
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            securityErrorHandler.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private boolean isWhitelisted(String requestURI) {
        for (String path : WHITE_LIST_URL) {
            if (path.endsWith("/**")) {
                String basePath = path.substring(0, path.length() - 3);
                if (requestURI.startsWith(basePath)) {
                    return true;
                }
            } else if (path.endsWith("/*")) {
                String basePath = path.substring(0, path.length() - 2);
                if (requestURI.startsWith(basePath)) {
                    return true;
                }
            } else if (requestURI.equals(path)) {
                return true;
            }
        }
        return false;
    }
}