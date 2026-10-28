package com.sistemas.pedidos.infrastructure.output.jpa.repository;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface IPedidoRepository extends JpaRepository<PedidoEntity, Long> {

    boolean existsByIdClienteAndEstadoIn(Long idCliente, Collection<EstadoPedido> estados);
}