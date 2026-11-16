package com.armario.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "prendas")
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private String nombre;

    private String color;

    private String temporada;

    private String talla;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPrenda tipoPrenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrenda estado = EstadoPrenda.DISPONIBLE;

    private String imagenUrl;

    @Column(nullable = false)
    private Instant fechaCaptura = Instant.now();

    private Instant ultimoUso;

    private String tejido;

    public Prenda() {}

    public Prenda(Long profileId, String nombre, TipoPrenda tipoPrenda) {
        this.profileId = profileId;
        this.nombre = nombre;
        this.tipoPrenda = tipoPrenda;
    }

    public Long getId() { return id; }
    public Long getProfileId() { return profileId; }
    public String getNombre() { return nombre; }
    public String getColor() { return color; }
    public String getTemporada() { return temporada; }
    public String getTalla() { return talla; }
    public TipoPrenda getTipoPrenda() { return tipoPrenda; }
    public EstadoPrenda getEstado() { return estado; }
    public String getImagenUrl() { return imagenUrl; }
    public Instant getFechaCaptura() { return fechaCaptura; }
    public Instant getUltimoUso() { return ultimoUso; }
    public String getTejido() { return tejido; }

    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setColor(String color) { this.color = color; }
    public void setTemporada(String temporada) { this.temporada = temporada; }
    public void setTalla(String talla) { this.talla = talla; }
    public void setTipoPrenda(TipoPrenda tipoPrenda) { this.tipoPrenda = tipoPrenda; }
    public void setEstado(EstadoPrenda estado) {
        this.estado = estado;
        if (estado == EstadoPrenda.EN_USO) this.ultimoUso = Instant.now();
    }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setFechaCaptura(Instant fechaCaptura) { this.fechaCaptura = fechaCaptura; }
    public void setUltimoUso(Instant ultimoUso) { this.ultimoUso = ultimoUso; }
    public void setTejido(String tejido) { this.tejido = tejido; }
}