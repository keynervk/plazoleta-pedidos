package com.sistemas.pedidos.domain.model;

public class PedidoPlato {

    private Long idPlato;
    private Integer cantidad;
    private Integer precioUnitario;

    public PedidoPlato() {
    }

    public PedidoPlato(Long idPlato, Integer cantidad, Integer precioUnitario) {
        this.idPlato = idPlato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Integer precioUnitario) { this.precioUnitario = precioUnitario; }
}