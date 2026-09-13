package pe.edu.upeu.bomerp.personas.persona.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.bomerp.personas.persona.entity.TipoDocumento;
import pe.edu.upeu.bomerp.personas.persona.entity.RolPersona;

import java.util.Set;

public record PersonaRequest(
        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        @NotBlank(message = "El nombre o razón social es obligatorio")
        String nombreORazonSocial,

        String direccion,

        String telefono,

        @Email(message = "El formato del email no es válido")
        String email,

        @NotEmpty(message = "Debe asignar al menos un rol (CLIENTE o PROVEEDOR)")
        Set<RolPersona> roles,
        
        Boolean activo
) {
}
