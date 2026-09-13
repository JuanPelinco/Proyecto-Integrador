package pe.edu.upeu.bomerp.compras.ordencompra.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.DetalleOrdenRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.DetalleOrdenResponse;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraResponse;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.EstadoOrdenCompra;
import pe.edu.upeu.bomerp.compras.ordencompra.service.OrdenCompraService;
import pe.edu.upeu.bomerp.exception.ProveedorInvalidoException;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenCompraController.class)
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdenCompraService ordenCompraService;

    @Test
    void crear_conDatosValidos_respondeCreated() throws Exception {
        DetalleOrdenRequest detalle = new DetalleOrdenRequest(10L, "Saco de maiz 50kg", new BigDecimal("50.00"), 2);
        OrdenCompraRequest request = new OrdenCompraRequest(1L, "Pago a 30 dias", List.of(detalle));

        when(ordenCompraService.crearOrden(any())).thenReturn(
                OrdenCompraResponse.builder()
                        .id(100L)
                        .fecha(LocalDateTime.now())
                        .estado(EstadoOrdenCompra.EMITIDA)
                        .total(new BigDecimal("100.00"))
                        .proveedorId(1L)
                        .nombreProveedor("Proveedor Test")
                        .numeroDocumentoProveedor("20123456789")
                        .condicionesPactadas("Pago a 30 dias")
                        .detalles(List.of(DetalleOrdenResponse.builder()
                                .id(1L).articuloId(10L).descripcionArticulo("Saco de maiz 50kg")
                                .precioUnitario(new BigDecimal("50.00")).cantidad(2)
                                .subtotal(new BigDecimal("100.00")).build()))
                        .build()
        );

        mockMvc.perform(post("/api/v1/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.proveedorId").value(1))
                .andExpect(jsonPath("$.estado").value("EMITIDA"));
    }

    @Test
    void crear_conListaDeDetallesVacia_respondeBadRequestSinLlegarAlService() throws Exception {
        OrdenCompraRequest request = new OrdenCompraRequest(1L, "Pago a 30 dias", List.of());

        mockMvc.perform(post("/api/v1/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_conProveedorInexistente_respondeNotFound() throws Exception {
        DetalleOrdenRequest detalle = new DetalleOrdenRequest(10L, "Saco de maiz 50kg", new BigDecimal("50.00"), 2);
        OrdenCompraRequest request = new OrdenCompraRequest(999L, "Pago a 30 dias", List.of(detalle));

        when(ordenCompraService.crearOrden(any()))
                .thenThrow(new ResourceNotFoundException("Persona no encontrada con id: 999"));

        mockMvc.perform(post("/api/v1/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_conProveedorSinRolProveedor_respondeBadRequest() throws Exception {
        DetalleOrdenRequest detalle = new DetalleOrdenRequest(10L, "Saco de maiz 50kg", new BigDecimal("50.00"), 2);
        OrdenCompraRequest request = new OrdenCompraRequest(2L, "Pago a 30 dias", List.of(detalle));

        when(ordenCompraService.crearOrden(any()))
                .thenThrow(new ProveedorInvalidoException("La persona con ID 2 no tiene el rol de PROVEEDOR"));

        mockMvc.perform(post("/api/v1/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_conIdInexistente_respondeNotFound() throws Exception {
        when(ordenCompraService.obtenerOrden(999L))
                .thenThrow(new ResourceNotFoundException("Orden de compra no encontrada con id: 999"));

        mockMvc.perform(get("/api/v1/ordenes-compra/999"))
                .andExpect(status().isNotFound());
    }
}
