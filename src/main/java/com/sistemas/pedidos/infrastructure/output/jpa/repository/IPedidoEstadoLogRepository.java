package com.sistemas.pedidos.infrastructure.output.jpa.repository;

import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEstadoLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPedidoEstadoLogRepository extends JpaRepository<PedidoEstadoLogEntity, Long> {

    List<PedidoEstadoLogEntity> findByIdPedidoOrderByFechaCambioAsc(Long idPedido);
}