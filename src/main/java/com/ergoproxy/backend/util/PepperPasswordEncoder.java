package com.ergoproxy.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PepperPasswordEncoder implements PasswordEncoder {

    @Value("${ergoproxy.pepper.secret}")
    private String pepper;

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        String combined = pepper + rawPassword.toString();
        String hashedCombined = hashWithSha256(combined);
        return bcrypt.encode(hashedCombined);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String combined = pepper + rawPassword.toString();
        String hashedCombined = hashWithSha256(combined);
        return bcrypt.matches(hashedCombined, encodedPassword);
    }

    private String hashWithSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing with SHA-256", e);
        }
    }
}
