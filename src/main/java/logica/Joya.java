package logica;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Joya {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nombre;

    @Column
    private String precio;

    @Column
    private double peso;

    @Column
    private String categoria;

    @Column
    private String observacion;

    @Column
    private boolean tienePiedra;

    @Column
    private String infoPiedra;

    @Column
    private boolean fueEditada;

    @Column
    private boolean vendido; // Nuevo
    @Column
    private LocalDateTime fechaIngreso;

    @Column
    private LocalDateTime fechaVendida;

    @Column
    private String estado ;

    // Constructor vacío
    public Joya() {
      this.vendido = false;
      this.estado = "disponible";
    }

    // Constructor con parámetros
    public Joya(String nombre, String precio, double peso, String categoria, String observacion, boolean tienePiedra, String infoPiedra) {
        this.nombre = nombre;
        this.precio = precio;
        this.peso = peso;
        this.categoria = categoria;
        this.observacion = observacion;
        this.tienePiedra = tienePiedra;
        this.infoPiedra = infoPiedra;
        this.fueEditada = false; // Inicializado como falso por defecto
        this.vendido = false;   // Inicializado como falso por defecto
        this.fechaIngreso = LocalDateTime.now();
        this.estado= "disponible";

    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean isTienePiedra() {
        return tienePiedra;
    }

    public void setTienePiedra(boolean tienePiedra) {
        this.tienePiedra = tienePiedra;
    }

    public String getInfoPiedra() {
        return infoPiedra;
    }

    public void setInfoPiedra(String infoPiedra) {
        this.infoPiedra = infoPiedra;
    }

    public boolean isFueEditada() {
        return fueEditada;
    }

    public void setFueEditada(boolean fueEditada) {
        this.fueEditada = fueEditada;
    }

    public boolean isVendido() {
        return vendido;
    }

    public void setVendido(boolean vendido) {
        this.vendido = vendido;
    }
    // Getters y Setters
    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDateTime getFechaVendida() {
        return fechaVendida;
    }

    public void setFechaVendida(LocalDateTime fechaVendida) {
        this.fechaVendida = fechaVendida;
    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Método toString
    @Override
    public String toString() {
        return "Joya{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", peso=" + peso +
                ", categoria='" + categoria + '\'' +
                ", observacion='" + observacion + '\'' +
                ", tienePiedra=" + tienePiedra +
                ", infoPiedra='" + infoPiedra + '\'' +
                ", fueEditada=" + fueEditada +
                ", vendido=" + vendido +
                ", estado='" + estado + '\'' +
                '}';
    }
}
