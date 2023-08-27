package com.arnabk.bgapp.controller;

import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.impl.EmailSenderService;
import com.arnabk.bgapp.model.PasswordModel;
import com.arnabk.bgapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final EmailSenderService senderService;

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordModel passwordModel,
                                                HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();

            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return ResponseEntity.ok("Password reset mail sent successfully");
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {

        String result = userService.validatePasswordResetToken(token);

        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        }

        // here the password reset token is valid
        Optional<User> user = userService.getUserByPasswordResetToken(token);

        if (user.isPresent()) {
            // change password
            userService.changePassword(user.get(), passwordModel.getNewPassword());

            return "Password Reset Successful";
        } else {
            // invalidate token, please implement logic

            return "Password Reset Token Invalidated";
        }

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/api/password/savePassword?token=" + token;

        log.info("Click link to reset your password: {} ", url);
        senderService.sendEmail(user.getEmail(), "Password Reset Code", "Token: " + token);

        return url;
    }

    private String applicationUrl(HttpServletRequest request) {
        log.info("1st " + request.getServerName() + " 2nd " + request.getServerPort() + " 3rd " + request.getContextPath());
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

}
