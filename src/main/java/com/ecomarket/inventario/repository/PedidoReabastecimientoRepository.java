package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.entity.PedidoReabastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoReabastecimientoRepository extends JpaRepository<PedidoReabastecimiento, Long> {
    List<PedidoReabastecimiento> findByProductoId(Long productoId);
    List<PedidoReabastecimiento> findByEstado(PedidoReabastecimiento.Estado estado);
}