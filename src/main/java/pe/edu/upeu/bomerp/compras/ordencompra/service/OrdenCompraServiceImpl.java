package pe.edu.upeu.bomerp.compras.ordencompra.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.DetalleOrdenRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraResponse;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.DetalleOrden;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.EstadoOrdenCompra;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.OrdenCompra;
import pe.edu.upeu.bomerp.compras.ordencompra.event.OrdenCompraEmitidaEvent;
import pe.edu.upeu.bomerp.compras.ordencompra.mapper.OrdenCompraMapper;
import pe.edu.upeu.bomerp.compras.ordencompra.repository.OrdenCompraRepository;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.personas.persona.dto.PersonaResponse;
import pe.edu.upeu.bomerp.personas.persona.service.PersonaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final PersonaService personaService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarOrdenes(EstadoOrdenCompra estado, Long proveedorId, LocalDateTime inicio, LocalDateTime fin) {
        List<OrdenCompra> ordenes;
        // Lógica de filtrado simple (para casos más complejos usar Specifications)
        if (estado != null) {
            ordenes = ordenCompraRepository.findByEstado(estado);
        } else if (proveedorId != null) {
            ordenes = ordenCompraRepository.findByProveedorId(proveedorId);
        } else if (inicio != null && fin != null) {
            ordenes = ordenCompraRepository.findByFechaBetween(inicio, fin);
        } else {
            ordenes = ordenCompraRepository.findAll();
        }

        return ordenes.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraResponse obtenerOrden(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con id: " + id));
        return mapearAResponse(orden);
    }

    @Override
    @Transactional
    public OrdenCompraResponse crearOrden(OrdenCompraRequest request) {
        // Validar que el proveedor existe, está activo y tiene rol PROVEEDOR
        PersonaResponse proveedor = personaService.obtenerProveedorActivo(request.proveedorId());

        OrdenCompra orden = ordenCompraMapper.toEntity(request);
        orden.setFecha(LocalDateTime.now());
        orden.setEstado(EstadoOrdenCompra.EMITIDA);
        
        // Snapshot del proveedor
        orden.setProveedorId(proveedor.getId());
        orden.setNombreProveedor(proveedor.getNombreORazonSocial());
        orden.setNumeroDocumentoProveedor(proveedor.getNumeroDocumento());

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleOrdenRequest detRequest : request.detalles()) {
            DetalleOrden detalle = ordenCompraMapper.toEntity(detRequest);
            BigDecimal subtotal = detRequest.precioUnitario().multiply(BigDecimal.valueOf(detRequest.cantidad()));
            detalle.setSubtotal(subtotal);
            orden.addDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total);

        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);
        OrdenCompraResponse response = mapearAResponse(ordenGuardada);

        // Publicar evento de dominio para que Inventario (o cualquier otro) lo escuche
        eventPublisher.publishEvent(new OrdenCompraEmitidaEvent(
                response.getId(),
                response.getProveedorId(),
                response.getTotal(),
                response.getDetalles()
        ));

        return response;
    }

    private OrdenCompraResponse mapearAResponse(OrdenCompra orden) {
        OrdenCompraResponse response = ordenCompraMapper.toResponse(orden);
        response.setDetalles(ordenCompraMapper.toResponse(orden.getDetalles()));
        return response;
    }
}
