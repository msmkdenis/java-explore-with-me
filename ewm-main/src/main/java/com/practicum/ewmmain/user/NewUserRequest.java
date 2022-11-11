package com.practicum.ewmmain.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @Email
    private String email;
    @NotBlank
    @Size(min = 2)
    private String name;
}
