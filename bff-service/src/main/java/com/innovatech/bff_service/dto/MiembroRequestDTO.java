package com.innovatech.bff_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroRequestDTO {

    @NotBlank(message = "El identificador es obligatorio")
    @Size(max = 50, message = "El identificador no puede superar los 50 caracteres")
    private String identificador;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 80, message = "El rol no puede superar los 80 caracteres")
    private String rol;

    @Email(message = "El email debe tener un formato valido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    private Boolean activo;
}
