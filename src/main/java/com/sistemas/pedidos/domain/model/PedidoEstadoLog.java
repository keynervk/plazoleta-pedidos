package com.sistemas.pedidos.domain.model;

import java.time.LocalDateTime;

public class PedidoEstadoLog {

    private Long id;
    private Long idPedido;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private LocalDateTime fechaCambio;

    public PedidoEstadoLog() {
    }

    public PedidoEstadoLog(Long id, Long idPedido, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo,
                           LocalDateTime fechaCambio) {
        this.id = id;
        this.idPedido = idPedido;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public EstadoPedido getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoPedido estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public EstadoPedido getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoPedido estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
}