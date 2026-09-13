package pe.edu.upeu.bomerp.personas.persona.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.personas.persona.dto.PersonaRequest;
import pe.edu.upeu.bomerp.personas.persona.dto.PersonaResponse;
import pe.edu.upeu.bomerp.personas.persona.entity.RolPersona;
import pe.edu.upeu.bomerp.personas.persona.entity.TipoDocumento;
import pe.edu.upeu.bomerp.personas.persona.service.PersonaService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonaController.class)
class PersonaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonaService personaService;

    @Test
    void listar_respondeOkConLasPersonasDelService() throws Exception {
        when(personaService.listarPersonas(null)).thenReturn(List.of(
                PersonaResponse.builder().id(1L).tipoDocumento(TipoDocumento.DNI)
                        .numeroDocumento("12345678").nombreORazonSocial("Juan Perez")
                        .roles(Set.of(RolPersona.CLIENTE)).activo(true).build()
        ));

        mockMvc.perform(get("/api/v1/personas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreORazonSocial").value("Juan Perez"));
    }

    @Test
    void crear_conDatosValidos_respondeCreated() throws Exception {
        PersonaRequest request = new PersonaRequest(
                TipoDocumento.DNI, "12345678", "Juan Perez",
                "Calle Falsa 123", "999888777", "juan@test.com",
                Set.of(RolPersona.CLIENTE), null);

        when(personaService.crearPersona(any())).thenReturn(
                PersonaResponse.builder().id(1L).tipoDocumento(TipoDocumento.DNI)
                        .numeroDocumento("12345678").nombreORazonSocial("Juan Perez")
                        .direccion("Calle Falsa 123").telefono("999888777").email("juan@test.com")
                        .roles(Set.of(RolPersona.CLIENTE)).activo(true).build()
        );

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroDocumento").value("12345678"));
    }

    @Test
    void crear_conProveedorYClienteALaVez_respondeCreated() throws Exception {
        PersonaRequest request = new PersonaRequest(
                TipoDocumento.RUC, "20123456789", "Distribuidora SAC",
                null, null, null, Set.of(RolPersona.CLIENTE, RolPersona.PROVEEDOR), null);

        when(personaService.crearPersona(any())).thenReturn(
                PersonaResponse.builder().id(2L).tipoDocumento(TipoDocumento.RUC)
                        .numeroDocumento("20123456789").nombreORazonSocial("Distribuidora SAC")
                        .roles(Set.of(RolPersona.CLIENTE, RolPersona.PROVEEDOR)).activo(true).build()
        );

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles.length()").value(2));
    }

    @Test
    void crear_conNumeroDocumentoVacio_respondeBadRequestSinLlegarAlService() throws Exception {
        PersonaRequest request = new PersonaRequest(
                TipoDocumento.DNI, "", "Juan Perez", null, null, null, Set.of(RolPersona.CLIENTE), null);

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_sinRoles_respondeBadRequestSinLlegarAlService() throws Exception {
        PersonaRequest request = new PersonaRequest(
                TipoDocumento.DNI, "12345678", "Juan Perez", null, null, null, Set.of(), null);

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_conIdInexistente_respondeNotFound() throws Exception {
        when(personaService.obtenerPersona(999L)).thenThrow(new ResourceNotFoundException("Persona no encontrada con id: 999"));

        mockMvc.perform(get("/api/v1/personas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_hazBajaLogicaYRespondeNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/personas/1"))
                .andExpect(status().isNoContent());
    }
}
