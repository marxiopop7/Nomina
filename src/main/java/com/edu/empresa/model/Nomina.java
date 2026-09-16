package com.edu.empresa.model;

import java.io.Serializable;

public class Nomina implements ICalculable, Serializable {

    private static final long serialVersionUID = 1L;

    private Empleado empleado;
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

    @Override
    public double calcularSalarioDevengado() {
        return (empleado.getSalarioBasico() / ConstantesNomina.DIAS_MES) * empleado.getDiasTrabajados();
    }

    @Override
    public double calcularAuxilioTransporte() {
    	if (empleado.getSalarioBasico() <= (ConstantesNomina.SMMLV * 2)) {
            return (ConstantesNomina.VALOR_AUX_TRANSPORTE / ConstantesNomina.DIAS_MES) * empleado.getDiasTrabajados();
        }
        return 0;
    }

    @Override
    public double calcularDescuentoSalud() {
    	return calcularSalarioDevengado() * ConstantesNomina.PORC_SALUD;
    }

    @Override
    public double calcularDescuentoPension() {
        
        return calcularSalarioDevengado() * ConstantesNomina.PORC_PENSION;
    }

    @Override
    public double calcularNetoPagar() {
        return calcularSalarioDevengado() + calcularAuxilioTransporte() - calcularDescuentoSalud() - calcularDescuentoPension();
    }

    // Getters y Setters
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public double getSalarioDevengado() { return salarioDevengado; }
    public void setSalarioDevengado(double salarioDevengado) { this.salarioDevengado = salarioDevengado; }

    public double getAuxTransporte() { return auxTransporte; }
    public void setAuxTransporte(double auxTransporte) { this.auxTransporte = auxTransporte; }

    public double getDescSalud() { return descSalud; }
    public void setDescSalud(double descSalud) { this.descSalud = descSalud; }

    public double getDescPension() { return descPension; }
    public void setDescPension(double descPension) { this.descPension = descPension; }

    public double getNetoPagar() { return netoPagar; }
    public void setNetoPagar(double netoPagar) { this.netoPagar = netoPagar; }
}



