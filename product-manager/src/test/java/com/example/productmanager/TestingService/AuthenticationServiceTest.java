package com.example.productmanager.TestingService;

import com.example.productmanager.dto.AuthenticationResponse;
import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.service.AuthenticationService;
import com.example.productmanager.service.impl.AuthenticationServiceImpl;
import com.example.productmanager.untils.JwtUntil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUntil jwtUtil;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletResponse response;

    @Test
    void testCreateAuthenticationToken_ValidCredentials_ReturnsResponseWithToken() throws Exception {
        // Arrange
        UserDTO userDTO = UserDTO.builder().email("example@example.com").password("123").build();
        UserDetails userDetails = new User("example@example.com", "password", new ArrayList<>());
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        jwtUtil = Mockito.mock(JwtUntil.class);
        response = Mockito.mock(HttpServletResponse.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(userDTO.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails.getUsername())).thenReturn("jwt-token");

        authenticationService = new AuthenticationServiceImpl(authenticationManager, userDetailsService, jwtUtil);
        // Act
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationService.createAuthenticationToken(userDTO, response);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(userDTO.getEmail());
        verify(jwtUtil).generateToken(userDetails.getUsername());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("jwt-token", responseEntity.getBody().getJwt());
    }

    @Test
    void testCreateAuthenticationToken_InvalidCredentials_ThrowsBadCredentialsException() {
        // Arrange
        UserDTO userDTO = UserDTO.builder().email("example@example.com").password("invalid-password").build();
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        jwtUtil = Mockito.mock(JwtUntil.class);
        response = Mockito.mock(HttpServletResponse.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Incorrect User name or password"));
        authenticationService = new AuthenticationServiceImpl(authenticationManager, userDetailsService, jwtUtil);

        // Act & Assert
        Assertions.assertThrows(BadCredentialsException.class, () -> authenticationService.createAuthenticationToken(userDTO, response));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testCreateAuthenticationToken_DisabledUser_ReturnsResponseWithNotFoundStatus() throws Exception {
        // Arrange
        UserDTO userDTO = UserDTO.builder().email("disabled@example.com").password("123").build();
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        jwtUtil = Mockito.mock(JwtUntil.class);
        response = Mockito.mock(HttpServletResponse.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new DisabledException("User is not created"));
        authenticationService = new AuthenticationServiceImpl(authenticationManager, userDetailsService, jwtUtil);

        // Act
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationService.createAuthenticationToken(userDTO, response);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "User is not created");
        Assertions.assertNull(responseEntity);
    }
}