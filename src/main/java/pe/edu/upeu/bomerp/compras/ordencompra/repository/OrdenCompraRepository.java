package pe.edu.upeu.bomerp.compras.ordencompra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.EstadoOrdenCompra;
import pe.edu.upeu.bomerp.compras.ordencompra.entity.OrdenCompra;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    List<OrdenCompra> findByEstado(EstadoOrdenCompra estado);
    List<OrdenCompra> findByProveedorId(Long proveedorId);
    List<OrdenCompra> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
