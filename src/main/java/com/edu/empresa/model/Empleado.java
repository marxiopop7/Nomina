package com.edu.empresa.model;

import java.io.Serializable;

public class Empleado extends Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    private double salarioBasico;
    private String cargo;

    public Empleado() {}

    public Empleado(String docId, String nombre, String apellido, String correo, double salarioBasico, String cargo) {
        super(docId, nombre, apellido, correo);
        
        this.salarioBasico = salarioBasico;
        this.cargo = cargo;
    }

    public boolean validar() {
        boolean personaValida = docId != null && !docId.isBlank()
                             && nombre != null && !nombre.isBlank()
                             && apellido != null && !apellido.isBlank();

        boolean salarioValido = salarioBasico > 0;

        return personaValida && salarioValido;
    }

    public String getMesNominaLegible(String mesNomina) {
        try {
            String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                              "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            
            String[] partes = mesNomina.split("-");
            int numeroMes = Integer.parseInt(partes[1]);
            String anio = partes[0];            
            return meses[numeroMes] + " " + anio;
            
        } catch (Exception e) {
            return mesNomina; 
        }
    }

    // --- GETTERS Y SETTERS ---
    public double getSalarioBasico() { return salarioBasico; }
    public void setSalarioBasico(double salarioBasico) { this.salarioBasico = salarioBasico; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}