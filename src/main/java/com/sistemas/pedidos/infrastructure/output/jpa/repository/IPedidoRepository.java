package com.sistemas.pedidos.infrastructure.output.jpa.repository;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IPedidoRepository extends JpaRepository<PedidoEntity, Long> {

    boolean existsByIdClienteAndEstadoIn(Long idCliente, Collection<EstadoPedido> estados);

    Page<PedidoEntity> findByIdRestauranteAndEstado(Long idRestaurante, EstadoPedido estado, Pageable pageable);

    List<PedidoEntity> findAllByIdRestauranteAndEstado(Long idRestaurante, EstadoPedido estado);
}