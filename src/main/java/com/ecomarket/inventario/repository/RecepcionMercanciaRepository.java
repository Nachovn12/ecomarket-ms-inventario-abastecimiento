package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.model.RecepcionMercancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecepcionMercanciaRepository extends JpaRepository<RecepcionMercancia, Long> {
    List<RecepcionMercancia> findByPedidoId(Long pedidoId);
}