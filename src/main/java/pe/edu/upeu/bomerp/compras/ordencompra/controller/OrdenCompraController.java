package pe.edu.upeu.bomerp.compras.ordencompra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraResponse;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.EstadoOrdenCompra;
import pe.edu.upeu.bomerp.compras.ordencompra.service.OrdenCompraService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes-compra")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Compra", description = "API para la gestión de Órdenes de Compra a Proveedores")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @GetMapping
    @Operation(summary = "Listar órdenes de compra, con filtros opcionales")
    public ResponseEntity<List<OrdenCompraResponse>> listarOrdenes(
            @RequestParam(required = false) EstadoOrdenCompra estado,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(ordenCompraService.listarOrdenes(estado, proveedorId, inicio, fin));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden de compra por su ID")
    public ResponseEntity<OrdenCompraResponse> obtenerOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.obtenerOrden(id));
    }

    @PostMapping
    @Operation(summary = "Crear (emitir) una nueva orden de compra")
    public ResponseEntity<OrdenCompraResponse> crearOrden(@Valid @RequestBody OrdenCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraService.crearOrden(request));
    }
}
