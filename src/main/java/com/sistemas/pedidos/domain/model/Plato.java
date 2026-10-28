package com.sistemas.pedidos.domain.model;

/**
 * Vista del plato tal como la expone el microservicio Plazoleta.
 */
public class Plato {

    private Long id;
    private String nombre;
    private Integer precio;
    private boolean activo;
    private Long idRestaurante;

    public Plato() {
    }

    public Plato(Long id, String nombre, Integer precio, boolean activo, Long idRestaurante) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.activo = activo;
        this.idRestaurante = idRestaurante;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }
}