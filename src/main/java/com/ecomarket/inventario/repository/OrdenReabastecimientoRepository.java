package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.model.OrdenReabastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenReabastecimientoRepository extends JpaRepository<OrdenReabastecimiento, Long> {
}