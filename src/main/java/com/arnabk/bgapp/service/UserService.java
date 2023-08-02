package com.arnabk.bgapp.service;

import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.entity.VerificationToken;
import com.arnabk.bgapp.model.AuthenticationRequest;
import com.arnabk.bgapp.model.RegisterRequest;
import com.arnabk.bgapp.model.AuthenticationResponse;

import java.util.Optional;

public interface UserService {

    User registerUser(RegisterRequest request);

    void saveVerificationTokenForUser(String token, User user);

    String validateRegistrationToken(String token);

    User findUserByToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    User verifyExistingUser(String email);
}
