package pe.edu.upeu.bomerp.compras.ordencompra.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DetalleOrdenRequest(
        @NotNull(message = "El artículo es obligatorio")
        Long articuloId,

        @NotBlank(message = "La descripción del artículo es obligatoria")
        String descripcionArticulo,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precioUnitario,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer cantidad
) {
}
