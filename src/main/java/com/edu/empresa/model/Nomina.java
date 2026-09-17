package com.edu.empresa.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Nomina implements Serializable {

	
    private static final long serialVersionUID = 1L;

    private Empleado empleado;
    private String mesNomina;
    private int diasTrabajados;

   
    public static final double SMMLV = 1750950;
    public static final int DIAS_MES = 30;
    public static final double VALOR_AUX_TRANSPORTE = 250000;
    public static final double PORC_PENSION = 0.04;
    public static final double PORC_SALUD = 0.04;

    private double salarioDevengado;
    private double auxTransporte;
    private double descSalud;
    private double descPension;
    private double netoPagar;

    public Nomina() {}

    public Nomina(Empleado empleado) {
        this.empleado = empleado;
    }

    public void calcularNomina() {
        this.salarioDevengado = calcularSalarioDevengado();
        this.auxTransporte = calcularAuxilioTransporte();
        this.descSalud = calcularDescuentoSalud();
        this.descPension = calcularDescuentoPension();
        this.netoPagar = calcularNetoPagar();
    }

    public double calcularSalarioDevengado() {
        if (empleado == null) return 0;
        return (empleado.getSalarioBasico() / DIAS_MES) * diasTrabajados;
    }

    public double calcularAuxilioTransporte() {
        if (empleado == null) return 0;
        if (empleado.getSalarioBasico() <= (SMMLV * 2)) {
            return (VALOR_AUX_TRANSPORTE / DIAS_MES) * diasTrabajados;
        }
        return 0;
    }

    public double calcularDescuentoSalud() {
        return calcularSalarioDevengado() * PORC_SALUD;
    }

    public double calcularDescuentoPension() {
        return calcularSalarioDevengado() * PORC_PENSION;
    }

    public double calcularNetoPagar() {
        return calcularSalarioDevengado() + calcularAuxilioTransporte() - calcularDescuentoSalud() - calcularDescuentoPension();
    }

    // --- GETTERS Y SETTERS ---
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public String getMesNomina() { return mesNomina; }
    public void setMesNomina(String mesNomina) { this.mesNomina = mesNomina; }

    public int getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(int diasTrabajados) { this.diasTrabajados = diasTrabajados; }

    @JsonIgnore
    public double getSalarioDevengado() { return salarioDevengado; }
    public void setSalarioDevengado(double salarioDevengado) { this.salarioDevengado = salarioDevengado; }

    @JsonIgnore
    public double getAuxTransporte() { return auxTransporte; }
    public void setAuxTransporte(double auxTransporte) { this.auxTransporte = auxTransporte; }

    @JsonIgnore
    public double getDescSalud() { return descSalud; }
    public void setDescSalud(double descSalud) { this.descSalud = descSalud; }

    @JsonIgnore
    public double getDescPension() { return descPension; }
    public void setDescPension(double descPension) { this.descPension = descPension; }
    
    @JsonIgnore
    public double getNetoPagar() { return netoPagar; }
    public void setNetoPagar(double netoPagar) { this.netoPagar = netoPagar; }
}