package com.arnabk.bgapp.filter;

import com.arnabk.bgapp.impl.JwtService;
import com.arnabk.bgapp.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization is the header for JWT Token and the token start as "Bearer " as standard
        final String authHeader = request.getHeader("Authorization");

        final String jwt;

        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // continue to next filter as no JWT token is provided
            filterChain.doFilter(request, response);
            return;
        }

        // at this point the authHeader has the JWT token so substring from 7 to end as "Bearer " length is 7 and
        // the Jwt token starts after 7th character
        jwt = authHeader.substring(7);

        //todo extract the userEmail from JWT Token
        userEmail = jwtService.extractUsername(jwt);

        // checking if user email exists and user is not already authenticated
        // SecurityContextHolder.getContext().getAuthentication() == null means user is not yet authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // fetching token from db and checking if token is not expired or revoked
            boolean isTokenValid = tokenRepository.findByToken(jwt)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                // token is valid
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // update the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // pass the flow to the next filter
        filterChain.doFilter(request, response);

    }
}
