package ru.kata.spring.boot_security.demo.entity;

import javax.validation.constraints.*;
import java.util.Set;

public class UserDTO {
    private Long id;

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 50, message = "Username должен быть от 3 до 50 символов")
    private String username;

    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    private String firstName;
    private String lastName;

    @Min(value = 0, message = "Возраст должен быть положительным")
    @Max(value = 150, message = "Возраст должен быть не более 150")
    private int age;

    @Email(message = "Некорректный email")
    private String email;

    @NotNull(message = "Роли обязательны")
    private Set<Long> roleIds; // ID выбранных ролей

    public UserDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(Set<Long> roleIds) { this.roleIds = roleIds; }
}

