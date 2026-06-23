package com.ecomarket.inventario.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecepcionMercanciaTest {

    @Test
    void prePersist_conCantidadDanadaNula_asignaCero() {
        RecepcionMercancia recepcion = new RecepcionMercancia();
        recepcion.setCantidadDanada(null);

        recepcion.prePersist();

        assertNotNull(recepcion.getFechaRecepcion());
        assertEquals(0, recepcion.getCantidadDanada());
    }

    @Test
    void prePersist_conCantidadDanadaExistente_mantieneValor() {
        RecepcionMercancia recepcion = new RecepcionMercancia();
        recepcion.setCantidadDanada(5);

        recepcion.prePersist();

        assertNotNull(recepcion.getFechaRecepcion());
        assertEquals(5, recepcion.getCantidadDanada());
    }
}
