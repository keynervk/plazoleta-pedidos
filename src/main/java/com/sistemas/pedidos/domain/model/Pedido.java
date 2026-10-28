package com.sistemas.pedidos.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private Long id;
    private Long idCliente;
    private Long idRestaurante;
    private EstadoPedido estado;
    private Long idEmpleadoAsignado;
    private String pin;
    private LocalDateTime fechaCreacion;
    private List<PedidoPlato> platos = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Long id, Long idCliente, Long idRestaurante, EstadoPedido estado, Long idEmpleadoAsignado,
                  String pin, LocalDateTime fechaCreacion, List<PedidoPlato> platos) {
        this.id = id;
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.estado = estado;
        this.idEmpleadoAsignado = idEmpleadoAsignado;
        this.pin = pin;
        this.fechaCreacion = fechaCreacion;
        this.platos = platos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Long getIdEmpleadoAsignado() { return idEmpleadoAsignado; }
    public void setIdEmpleadoAsignado(Long idEmpleadoAsignado) { this.idEmpleadoAsignado = idEmpleadoAsignado; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<PedidoPlato> getPlatos() { return platos; }
    public void setPlatos(List<PedidoPlato> platos) { this.platos = platos; }
}