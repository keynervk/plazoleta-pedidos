package com.sistemas.pedidos.domain.model;

/**
 * Vista del restaurante tal como la expone el microservicio Plazoleta.
 */
public class Restaurante {

    private Long id;
    private String nombre;
    private Long idPropietario;

    public Restaurante() {
    }

    public Restaurante(Long id, String nombre, Long idPropietario) {
        this.id = id;
        this.nombre = nombre;
        this.idPropietario = idPropietario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Long idPropietario) { this.idPropietario = idPropietario; }
}