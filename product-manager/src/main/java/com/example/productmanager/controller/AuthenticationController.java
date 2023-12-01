package com.example.productmanager.controller;

import com.example.productmanager.dto.AuthenticationResponse;
import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody UserDTO request, HttpServletResponse response) throws IOException {
        return authenticationService.createAuthenticationToken(request, response);
    }

}
