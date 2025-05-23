package com.animate.backend.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

import java.io.Serializable;

/**
 * DTO for {@link com.animate.backend.model.LoggedUser}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterDTO implements Serializable {

    @NotBlank
    private String username;

    @Nonnull
    @Email
    private String email;

    @Nonnull
    private String password;

    @Nonnull
    private String rePassword;

}