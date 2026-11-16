package com.armario.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProfile tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(
        name = "profile_shared",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedWith = new ArrayList<>();

    public Profile() {}

    public Profile(String nombre, TipoProfile tipo, User owner) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public TipoProfile getTipo() { return tipo; }
    public User getOwner() { return owner; }
    public List<User> getSharedWith() { return sharedWith; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(TipoProfile tipo) { this.tipo = tipo; }
    public void setOwner(User owner) { this.owner = owner; }
}