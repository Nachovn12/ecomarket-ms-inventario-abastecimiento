package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.model.AjusteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjusteStockRepository extends JpaRepository<AjusteStock, Long> {
    List<AjusteStock> findByProductoId(Long productoId);
}