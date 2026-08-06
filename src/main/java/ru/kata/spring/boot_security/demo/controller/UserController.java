package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;


@Controller
@RequestMapping("/user")
public class UserController {
    // на получение текущего  пользователя в системе из принципала


    @Autowired
    private UserService userService;

    @GetMapping
    public String userPage(Principal principal, Model model) {

        String username = principal.getName();

        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        return "user/index";
    }
}


