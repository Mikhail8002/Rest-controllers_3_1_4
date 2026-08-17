package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.entity.UserDTO;
import ru.kata.spring.boot_security.demo.service.UserService;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", userService.findAll());
        return "admin/index";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("userForm", new UserDTO());
        model.addAttribute("allRoles", userService.findAllRoles());
        return "admin/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("userForm") UserDTO user) {
        if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            Set<Long> defaultRoles = new HashSet<>();
            defaultRoles.add(1L); //

            user.setRoleIds(defaultRoles);
        }
        userService.createUserFromDTO(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);

        UserDTO userFormDTO = new UserDTO();
        userFormDTO.setId(user.getId());
        userFormDTO.setUsername(user.getUsername());
        userFormDTO.setFirstName(user.getFirstName());
        userFormDTO.setLastName(user.getLastName());
        userFormDTO.setAge(user.getAge());
        userFormDTO.setEmail(user.getEmail());

        Set<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        userFormDTO.setRoleIds(roleIds);

        model.addAttribute("userForm", userFormDTO);
        model.addAttribute("allRoles", userService.findAllRoles());
        return "admin/edit";
    }


    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("userForm") UserDTO  userDTO) {
        userDTO.setId(id);
        userService.updateUserFromDTO(id, userDTO);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}


