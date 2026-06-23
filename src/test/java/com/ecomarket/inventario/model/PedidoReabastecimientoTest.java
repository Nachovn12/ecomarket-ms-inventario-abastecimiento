package com.ecomarket.inventario.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoReabastecimientoTest {

    @Test
    void prePersist_conEstadoNulo_asignaPENDIENTE() {
        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setEstado(null);

        pedido.prePersist();

        assertNotNull(pedido.getFechaCreacion());
        assertEquals(PedidoReabastecimiento.Estado.PENDIENTE, pedido.getEstado());
    }

    @Test
    void prePersist_conEstadoExistente_mantieneEstado() {
        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setEstado(PedidoReabastecimiento.Estado.APROBADO);

        pedido.prePersist();

        assertNotNull(pedido.getFechaCreacion());
        assertEquals(PedidoReabastecimiento.Estado.APROBADO, pedido.getEstado());
    }
}
