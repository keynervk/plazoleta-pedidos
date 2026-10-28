package com.sistemas.pedidos.infrastructure.output.jpa.entity;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos", indexes = {
        @Index(name = "idx_pedidos_cliente_estado", columnList = "id_cliente, estado"),
        @Index(name = "idx_pedidos_restaurante_estado", columnList = "id_restaurante, estado")})
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_restaurante", nullable = false)
    private Long idRestaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "id_empleado_asignado")
    private Long idEmpleadoAsignado;

    @Column(length = 6)
    private String pin;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_platos", joinColumns = @JoinColumn(name = "id_pedido"))
    private List<PedidoPlatoEmbeddable> platos = new ArrayList<>();

    public PedidoEntity() {
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

    public List<PedidoPlatoEmbeddable> getPlatos() { return platos; }
    public void setPlatos(List<PedidoPlatoEmbeddable> platos) { this.platos = platos; }
}