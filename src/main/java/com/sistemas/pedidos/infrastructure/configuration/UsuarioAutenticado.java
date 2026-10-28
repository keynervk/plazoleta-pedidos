package com.sistemas.pedidos.infrastructure.configuration;

/**
 * Datos del usuario extraidos del JWT emitido por el microservicio Usuarios.
 */
public record UsuarioAutenticado(Long id, String correo, String rol) {
}