package com.example.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

    @Size(max = 80, message = "Name is too long")
    private String fullName;

    /** Optional: leave blank to keep the current password. */
    @Size(max = 100, message = "Password is too long")
    private String newPassword;
}
