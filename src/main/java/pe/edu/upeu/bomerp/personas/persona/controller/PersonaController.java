package pe.edu.upeu.bomerp.personas.persona.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.personas.persona.dto.PersonaRequest;
import pe.edu.upeu.bomerp.personas.persona.dto.PersonaResponse;
import pe.edu.upeu.bomerp.personas.persona.entity.RolPersona;
import pe.edu.upeu.bomerp.personas.persona.service.PersonaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Tag(name = "Personas", description = "API para la gestión de Personas (Clientes y Proveedores)")
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    @Operation(summary = "Listar todas las personas o filtrar por rol")
    public ResponseEntity<List<PersonaResponse>> listarPersonas(
            @RequestParam(required = false) RolPersona rol) {
        return ResponseEntity.ok(personaService.listarPersonas(rol));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una persona por su ID")
    public ResponseEntity<PersonaResponse> obtenerPersona(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.obtenerPersona(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva persona")
    public ResponseEntity<PersonaResponse> crearPersona(@Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.crearPersona(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una persona existente")
    public ResponseEntity<PersonaResponse> actualizarPersona(
            @PathVariable Long id, @Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.ok(personaService.actualizarPersona(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una persona de forma lógica (baja)")
    public ResponseEntity<Void> eliminarPersona(@PathVariable Long id) {
        personaService.eliminarPersona(id);
        return ResponseEntity.noContent().build();
    }
}
