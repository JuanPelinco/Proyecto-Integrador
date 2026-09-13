package pe.edu.upeu.bomerp.compras.ordencompra.service;

import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraRequest;
import pe.edu.upeu.bomerp.compras.ordencompra.dto.OrdenCompraResponse;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.EstadoOrdenCompra;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompraResponse> listarOrdenes(EstadoOrdenCompra estado, Long proveedorId, LocalDateTime inicio, LocalDateTime fin);
    OrdenCompraResponse obtenerOrden(Long id);
    OrdenCompraResponse crearOrden(OrdenCompraRequest request);
}
