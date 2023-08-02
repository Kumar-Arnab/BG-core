package com.arnabk.bgapp.filter.auth;

import com.arnabk.bgapp.entity.User;
import com.arnabk.bgapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BGAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        User user = userRepository.findByEmail(username).orElse(null);

        if (user != null && user.isEnabled()) {
            if (passwordEncoder.matches(pwd, user.getPassword())) {

                return new UsernamePasswordAuthenticationToken(username, pwd);
            } else {
                throw new BadCredentialsException("Invalid Password!");
            }
        } else {
            throw new BadCredentialsException("No user registered with this email");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
