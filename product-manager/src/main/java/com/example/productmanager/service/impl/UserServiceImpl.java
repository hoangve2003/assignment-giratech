package com.example.productmanager.service.impl;

import com.example.productmanager.dto.UserDTO;
import com.example.productmanager.model.User;
import com.example.productmanager.repository.UserRepository;
import com.example.productmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public ResponseEntity<User> createUser(UserDTO userDTO) {
        User user = convertToObject(userDTO);
        if (user == null) {
            throw new UsernameNotFoundException("User is not created");
        }
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    private User convertToObject(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .password(new BCryptPasswordEncoder().encode(userDTO.getPassword()))
                .name(userDTO.getName())
                .phone(userDTO.getPhone())
                .build();
    }
}
