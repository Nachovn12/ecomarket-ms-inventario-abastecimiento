package com.ecomarket.inventario.exception;

public class SkuDuplicadoException extends RuntimeException {
    public SkuDuplicadoException(String sku) {
        super("El SKU '" + sku + "' ya existe en el sistema.");
    }
}