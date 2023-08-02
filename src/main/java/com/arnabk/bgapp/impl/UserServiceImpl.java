package com.arnabk.bgapp.impl;

import com.arnabk.bgapp.entity.PasswordResetToken;
import com.arnabk.bgapp.entity.Token;
import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.entity.VerificationToken;
import com.arnabk.bgapp.enums.Role;
import com.arnabk.bgapp.enums.TokenType;
import com.arnabk.bgapp.filter.auth.BGAuthenticationProvider;
import com.arnabk.bgapp.model.AuthenticationRequest;
import com.arnabk.bgapp.model.AuthenticationResponse;
import com.arnabk.bgapp.model.RegisterRequest;
import com.arnabk.bgapp.repository.PasswordResetTokenRepository;
import com.arnabk.bgapp.repository.TokenRepository;
import com.arnabk.bgapp.repository.UserRepository;
import com.arnabk.bgapp.repository.VerificationTokenRepository;
import com.arnabk.bgapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private final BGAuthenticationProvider bgAuthenticationProvider;

    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(new User());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private void revokeAllUserToken(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());

        // if user has no valid token
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = new Token();

        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);

        tokenRepository.save(token);
    }

    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    @Transactional
    public String validateRegistrationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) return "invalid";

        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        // verification token time is more the current time means token is expired
        if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        // at this point the token is valid and not expired
        user.setEnabled(true);
        userRepository.save(user);

        return "valid";
    }

    @Override
    public User findUserByToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        return verificationToken.getUser();
    }

    @Override
    @Transactional
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);

        verificationToken.setToken(UUID.randomUUID().toString());
        // updating verification token using new random token
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        bgAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // saving token while authentication after invalidating the existing tokens
        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);
        return new AuthenticationResponse(jwtToken);

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) return "invalid";

        Calendar calendar = Calendar.getInstance();

        // password verification token time is more the current time means token is expired
        if (passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        // at this point the token is valid and not expired
        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    @Override
    public User verifyExistingUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
