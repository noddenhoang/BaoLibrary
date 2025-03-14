package com.thaihoangbao.BaoLibrary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @NotBlank(message = "Token cannot be blank")
    private String token;
    
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
} 