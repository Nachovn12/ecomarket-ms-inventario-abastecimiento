package com.ecomarket.inventario;

import com.ecomarket.inventario.entity.AjusteStock;
import com.ecomarket.inventario.entity.Producto;
import com.ecomarket.inventario.repository.AjusteStockRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MsInventarioAbastecimientoApplicationTests {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AjusteStockRepository ajusteStockRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testConsultaStock() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setSku("SKU-TEST-001");
        producto.setPrecio(100.0);
        producto.setStock(50);
        producto.setCategoria("Test");
        productoRepository.save(producto);

        Producto encontrado = productoRepository.findBySku("SKU-TEST-001").orElse(null);
        assertNotNull(encontrado);
        assertEquals(50, encontrado.getStock());
    }

    @Test
    void testAjusteManualStock() {
        Producto producto = new Producto();
        producto.setNombre("Producto Ajuste");
        producto.setSku("SKU-TEST-002");
        producto.setPrecio(200.0);
        producto.setStock(30);
        producto.setCategoria("Test");
        productoRepository.save(producto);

        AjusteStock ajuste = new AjusteStock();
        ajuste.setProducto(producto);
        ajuste.setCantidadAnterior(30);
        ajuste.setCantidadNueva(50);
        ajuste.setMotivo("Conteo físico");
        ajuste.setUsuarioResponsable("gerente@ecomarket.cl");
        ajusteStockRepository.save(ajuste);

        producto.setStock(50);
        productoRepository.save(producto);

        Producto actualizado = productoRepository.findById(producto.getId()).orElse(null);
        assertNotNull(actualizado);
        assertEquals(50, actualizado.getStock());
    }

    @Test
    void testSkuUnico() {
        Producto p1 = new Producto();
        p1.setNombre("Producto SKU");
        p1.setSku("SKU-UNICO-003");
        p1.setPrecio(150.0);
        p1.setStock(10);
        p1.setCategoria("Test");
        productoRepository.save(p1);

        boolean existe = productoRepository.existsBySku("SKU-UNICO-003");
        assertTrue(existe);
    }

    @Test
    void testDisponibilidadProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto Sin Stock");
        producto.setSku("SKU-TEST-004");
        producto.setPrecio(50.0);
        producto.setStock(0);
        producto.setCategoria("Test");
        productoRepository.save(producto);

        Producto encontrado = productoRepository.findById(producto.getId()).orElse(null);
        assertNotNull(encontrado);
        assertEquals(0, encontrado.getStock());
    }
}