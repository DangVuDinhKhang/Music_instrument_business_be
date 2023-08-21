package com.thesis.business.musicinstrument.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthService {

    public String generateJWTToken(String username, String role) {
        Long duration = System.currentTimeMillis() + 3600;
        String token = Jwt.issuer("jwt-token").upn(username).groups(role).expiresAt(duration).sign();
        return token;
    }
}
