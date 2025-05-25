package com.animate.backend.dto;

import jakarta.annotation.Nonnull;
import lombok.*;
import lombok.experimental.Accessors;


import java.io.Serializable;

/**
 * DTO for {@link com.animate.backend.model.LoggedUser}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class RegisterDTO implements Serializable {

    private String username;
    private String email;
    private String password;
    private String rePassword;

}