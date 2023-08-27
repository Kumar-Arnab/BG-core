package com.arnabk.bgapp.controller;

import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.entity.VerificationToken;
import com.arnabk.bgapp.event.RegistrationCompleteEvent;
import com.arnabk.bgapp.impl.EmailSenderService;
import com.arnabk.bgapp.model.AuthenticationRequest;
import com.arnabk.bgapp.model.AuthenticationResponse;
import com.arnabk.bgapp.model.RegisterRequest;
import com.arnabk.bgapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ApplicationEventPublisher publisher;

    @Autowired
    private final EmailSenderService senderService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest,
                                           final HttpServletRequest request) {
        User user = userService.verifyExistingUser(registerRequest.getEmail());
        if (user == null) {
            user = userService.registerUser(registerRequest);

            publisher.publishEvent(new RegistrationCompleteEvent(
                    user,
                    applicationUrl(request)
            ));

            return ResponseEntity.ok("Registration Successful");
        } else {
            return ResponseEntity.badRequest().body("User already registered");
        }
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam(value = "token", required = true) String token) {
        String result = userService.validateRegistrationToken(token);

        if (result.equalsIgnoreCase("valid")) {
            return ResponseEntity.ok("User Verified successfully");
        }

        return ResponseEntity.badRequest().body("Bad User");
    }

    @GetMapping("/resendToken")
    public ResponseEntity<String> resendVerificationToken(@RequestParam(value = "token", required = true) String oldToken,
                                          HttpServletRequest request) {
        User user = userService.findUserByToken(oldToken);

        if (user != null && !user.isEnabled()) {
            VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
            user = verificationToken.getUser();

            resendVerificationTokenMail(user, applicationUrl(request), verificationToken);

            return ResponseEntity.ok("Verification link sent");
        } else if (user != null && user.isEnabled()) {
            return ResponseEntity.badRequest().body("User is already verified");
        } else {
            return ResponseEntity.badRequest().body("Please provide a valid token");
        }
    }

    private void resendVerificationTokenMail(User user, String applicationUrl,
                                             VerificationToken verificationToken) {
        // send mail to user
        String url = applicationUrl + "/api/auth/verifyRegistration?token=" + verificationToken.getToken();

        log.info("Resend verification email sent to your account: {} ", url);
        senderService.sendEmail(user.getEmail(), "New Email Verification Code",
                "Token: " + verificationToken.getToken());

    }

    private String applicationUrl(HttpServletRequest request) {
        log.info("1st " + request.getServerName() + " 2nd " + request.getServerPort() + " 3rd " + request.getContextPath());
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            User user = userService.verifyExistingUser(request.getEmail());
            if (user != null) {
                if (user.isEnabled()) {
                    return ResponseEntity.ok(userService.authenticate(request));
                } else {
                    return ResponseEntity.ok(new AuthenticationResponse("Please verify user email"));
                }
            } else {
                return ResponseEntity.badRequest().body("Username or password not valid");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Username or password not valid");
        }

    }

}
