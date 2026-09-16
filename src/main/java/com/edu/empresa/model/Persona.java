
package com.edu.empresa.model;

import java.io.Serializable;

public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String docId;
    protected String nombre;
    protected String apellido;
    protected String correo;

    public Persona() {
        
    }

    public Persona(String docId, String nombre, String apellido, String correo) {
        this.docId = docId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String obtenerNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}

