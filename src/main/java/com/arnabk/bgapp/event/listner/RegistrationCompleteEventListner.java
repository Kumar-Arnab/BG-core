package com.arnabk.bgapp.event.listner;

import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.impl.EmailSenderService;
import com.arnabk.bgapp.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import com.arnabk.bgapp.event.RegistrationCompleteEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListner implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private final EmailSenderService senderService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        //Create the verification Token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        // send mail to user
        String url = event.getApplicationUrl() + "/api/auth/verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: {} ", url);
        senderService.sendEmail(user.getEmail(), "Email Verification Code", "Token: " + token);

    }
}
