package com.example.productmanager.service.impl;

import com.example.productmanager.dto.AuthenticationResponse;
import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.service.AuthenticationService;
import com.example.productmanager.untils.JwtUntil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUntil jwtUntil;

    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(UserDTO request, HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect User name or password");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not created");
            return null;
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUntil.generateToken(userDetails.getUsername());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwt);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}
