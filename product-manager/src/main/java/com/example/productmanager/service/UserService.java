package com.example.productmanager.service;

import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<User> createUser(UserDTO userDTO);
}
