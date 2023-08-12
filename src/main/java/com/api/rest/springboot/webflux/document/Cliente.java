package com.api.rest.springboot.webflux.document;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//1.-Creamos el documento y añadimos la anotacion @Document
@Document(collection = "clientes")
public class Cliente {

    //Agregamos los atributos
    @Id
    private String id;

    @NotEmpty
    private String nombre;

    @NotEmpty
    private String apellidos;

    @NotNull
    private Integer edad;

    @NotNull
    private Double sueldo;
    private String foto;

    //Creando el constructor vacion
    public Cliente() {

    }

    //Creamos los Getter and Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Double getSueldo() {
        return sueldo;
    }

    public void setSueldo(Double sueldo) {
        this.sueldo = sueldo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
