package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entity.User;



public interface UserService extends UserDetailsService {
    User findById(Long id);
    User findByUsername(String username);
    void save(User user);
    void update(User user);
    void delete(Long id);
    Iterable<User> findAll();



}

