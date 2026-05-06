package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsBySku(String sku);
    Optional<Producto> findBySku(String sku);
}