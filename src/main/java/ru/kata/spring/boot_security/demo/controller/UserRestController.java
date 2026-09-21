package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.entity.UserDTO;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = (List<User>) userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.findAllRoles());
    }

    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            if (userService.findByUsername(userDTO.getUsername()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Пользователь с таким именем уже существует"));
            }

            User created = userService.createUserFromDTO(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            if (!existingUser.getUsername().equals(userDTO.getUsername())) {
                User userWithSameUsername = userService.findByUsername(userDTO.getUsername());
                if (userWithSameUsername != null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Пользователь с таким именем уже существует"));
                }
            }

            User updated = userService.updateUserFromDTO(id, userDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName().equals(user.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Нельзя удалить самого себя"));
            }

            userService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Пользователь успешно удален"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
