package com.edu.empresa.controller;

import com.edu.empresa.model.*;
import com.edu.empresa.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Named
@ViewScoped
public class NominaBean implements Serializable {

	private static final long serialVersionUID = 1L;

    //validación
    private static final Pattern PATRON_NUMEROS = Pattern.compile("^\\d+$");
    private static final Pattern PATRON_LETRAS = Pattern.compile("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ\\s]+$");
    private static final Pattern PATRON_CORREO = Pattern.compile("^[\\w.+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    
    private String nombres, apellidos, identificacion, correo;
    private double salarioBasico;
    private int diasTrabajados = 30;
    private Integer mesSeleccionado, anioSeleccionado;

    
    private Nomina nominaCalculada;
    private List<Nomina> listaHistorico;
    private NominaService nominaService;
    private boolean mostrarResultados;

    
    private boolean modoEdicion;
    private String idOriginal, mesOriginal;

    @PostConstruct
    public void init() {
        nominaService = new NominaService();
        cargarHistorico();
    }

    public void cargarHistorico() {
        try {
            listaHistorico = nominaService.obtenerHistorico();
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar el archivo JSON.");
        }
    }

    public void calcular() {
        try {
            validarFormatoCampos();
            String mesTexto = String.format("%04d-%02d", anioSeleccionado, mesSeleccionado);
            Empleado emp = new Empleado(identificacion, nombres, apellidos, correo, salarioBasico, diasTrabajados, mesTexto);

            Nomina resultadoTemporal;

            if (modoEdicion) {
                resultadoTemporal = nominaService.actualizarNomina(idOriginal, mesOriginal, emp);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Nómina actualizada correctamente.");
            } else {
                resultadoTemporal = nominaService.guardarNuevaNomina(emp);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Nómina guardada correctamente.");
            }

            cargarHistorico();
            
            limpiar();
            
            
            this.nominaCalculada = resultadoTemporal;
            this.mostrarResultados = true; 

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }
    public void prepararEdicion(Nomina nomina) {
        Empleado emp = nomina.getEmpleado();
        
        
        this.identificacion = emp.getDocId();
        this.nombres = emp.getNombre();
        this.apellidos = emp.getApellido();
        this.correo = emp.getCorreo();
        this.salarioBasico = emp.getSalarioBasico();
        this.diasTrabajados = emp.getDiasTrabajados();
        
        
        try {
            String[] partes = emp.getMesNomina().split("-");
            this.anioSeleccionado = Integer.parseInt(partes[0]);
            this.mesSeleccionado = Integer.parseInt(partes[1]);
        } catch (Exception e) {
            this.anioSeleccionado = null;
            this.mesSeleccionado = null;
        }

        
        this.idOriginal = emp.getDocId();
        this.mesOriginal = emp.getMesNomina();
        this.modoEdicion = true;
        this.mostrarResultados = false;
    }

    public void eliminar(String docId, String mesNomina) {
        try {
            nominaService.eliminarNomina(docId, mesNomina);
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro eliminado correctamente.");
            cargarHistorico();

            if (docId.equals(idOriginal) && mesNomina.equals(mesOriginal)) {
                limpiar();
            }
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void limpiar() {
        this.nombres = this.apellidos = this.identificacion = this.correo = "";
        this.salarioBasico = 0;
        this.diasTrabajados = 30;
        this.mesSeleccionado = this.anioSeleccionado = null;
        
        this.nominaCalculada = null;
        this.mostrarResultados = false;
        this.modoEdicion = false;
        this.idOriginal = this.mesOriginal = null;
    }

    private void validarFormatoCampos() {
        if (mesSeleccionado == null || anioSeleccionado == null) throw new IllegalArgumentException("Selecciona el mes y el año.");
        if (identificacion == null || !PATRON_NUMEROS.matcher(identificacion.trim()).matches()) throw new IllegalArgumentException("La identificación debe contener solo números.");
        if (nombres == null || !PATRON_LETRAS.matcher(nombres.trim()).matches()) throw new IllegalArgumentException("Los nombres solo deben contener letras.");
        if (apellidos == null || !PATRON_LETRAS.matcher(apellidos.trim()).matches()) throw new IllegalArgumentException("Los apellidos solo deben contener letras.");
        if (correo == null || !PATRON_CORREO.matcher(correo.trim()).matches()) throw new IllegalArgumentException("El correo no tiene un formato válido.");
        if (diasTrabajados < 1 || diasTrabajados > ConstantesNomina.DIAS_MES) throw new IllegalArgumentException("Días trabajados inválidos (1 - " + ConstantesNomina.DIAS_MES + ").");
    }

    
    private void mostrarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    public List<Integer> getAniosDisponibles() {
        int anioActual = Year.now().getValue();
        List<Integer> anios = new ArrayList<>();
        for (int a = anioActual; a >= anioActual - 5; a--) {
            anios.add(a);
        }
        return anios;
    }
    
    // --- GETTERS Y SETTERS ---
    public List<Nomina> getListaHistorico() { return listaHistorico; }
    public void setListaHistorico(List<Nomina> listaHistorico) { this.listaHistorico = listaHistorico; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public double getSalarioBasico() { return salarioBasico; }
    public void setSalarioBasico(double salarioBasico) { this.salarioBasico = salarioBasico; }
    public int getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(int diasTrabajados) { this.diasTrabajados = diasTrabajados; }
    public Integer getMesSeleccionado() { return mesSeleccionado; }
    public void setMesSeleccionado(Integer mesSeleccionado) { this.mesSeleccionado = mesSeleccionado; }
    public Integer getAnioSeleccionado() { return anioSeleccionado; }
    public void setAnioSeleccionado(Integer anioSeleccionado) { this.anioSeleccionado = anioSeleccionado; }


    public Nomina getNominaCalculada() { return nominaCalculada; }
    public void setNominaCalculada(Nomina nominaCalculada) { this.nominaCalculada = nominaCalculada; }
    public boolean isMostrarResultados() { return mostrarResultados; }
    public void setMostrarResultados(boolean mostrarResultados) { this.mostrarResultados = mostrarResultados; }
    public boolean isModoEdicion() { return modoEdicion; }
    public void setModoEdicion(boolean modoEdicion) { this.modoEdicion = modoEdicion; }
}

