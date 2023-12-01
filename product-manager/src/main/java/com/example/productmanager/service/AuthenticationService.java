package com.example.productmanager.service;

import com.example.productmanager.dto.AuthenticationResponse;
import com.example.productmanager.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AuthenticationService {
    ResponseEntity<AuthenticationResponse> createAuthenticationToken(UserDTO request, HttpServletResponse response) throws IOException;
}
