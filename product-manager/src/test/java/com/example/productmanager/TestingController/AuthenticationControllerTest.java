package com.example.productmanager.TestingController;

import com.example.productmanager.controller.AuthenticationController;
import com.example.productmanager.dto.AuthenticationResponse;
import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.filters.JwtRequestFilter;
import com.example.productmanager.service.AuthenticationService;
import com.example.productmanager.service.impl.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = AuthenticationController.class)
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private UserDetailServiceImpl userDetailService;

    @Test
    void testCreateAuthenticationToken() throws Exception {
        // Tạo một đối tượng UserDTO giả định
        UserDTO userDTO = UserDTO.builder().email("example@example.com").password("123").build();
        // Tạo một đối tượng HttpServletResponse giả định
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        // Mock đối tượng AuthenticationService
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("jwt-token");
        ResponseEntity<AuthenticationResponse> responseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        Mockito.when(authenticationService.createAuthenticationToken(userDTO, response)).thenReturn(responseEntity);

        // Gọi phương thức createAuthenticationToken() trên đối tượng AuthenticationController
        mockMvc.perform(MockMvcRequestBuilders.post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
