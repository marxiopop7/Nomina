package com.edu.empresa.model;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Empleado extends Persona implements IValidable, Serializable {

    private static final long serialVersionUID = 1L;

    private double salarioBasico;
    private int diasTrabajados;
    private String mesNomina;

    public Empleado() {}

    public Empleado(String docId, String nombre, String apellido, String correo, 
                    double salarioBasico, int diasTrabajados, String mesNomina) {
        super(docId, nombre, apellido, correo);
        this.salarioBasico = salarioBasico;
        this.diasTrabajados = diasTrabajados;
        this.mesNomina = mesNomina;
    }

    @Override
    public boolean validar() {
        
        boolean personaValida = docId != null && !docId.isBlank()
                             && nombre != null && !nombre.isBlank()
                             && apellido != null && !apellido.isBlank();

        boolean nominaValida = salarioBasico > 0 
                            && diasTrabajados >= 1 
                            && diasTrabajados <= ConstantesNomina.DIAS_MES;

        return personaValida && nominaValida;
    }

    public String getMesNominaLegible() {
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

    public int getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(int diasTrabajados) { this.diasTrabajados = diasTrabajados; }

    public String getMesNomina() { return mesNomina; }
    public void setMesNomina(String mesNomina) { this.mesNomina = mesNomina; }
}