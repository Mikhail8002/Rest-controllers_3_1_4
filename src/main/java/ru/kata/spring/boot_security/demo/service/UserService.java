package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.entity.UserDTO;

import java.util.List;


public interface UserService extends UserDetailsService {
    User findById(Long id);

    User findByUsername(String username);

    void delete(Long id);

    Iterable<User> findAll();

    List<Role> findAllRoles();

    User createUserFromDTO(UserDTO userDTO);

    User updateUserFromDTO(Long id, UserDTO userDTO);

}

