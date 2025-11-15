package com.example.perkmanager.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command: Create a new user
 * Represents the intention to register a new user in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
