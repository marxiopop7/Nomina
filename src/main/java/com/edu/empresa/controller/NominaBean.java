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

    
    private static final Pattern PATRON_NUMEROS = Pattern.compile("^\\d+$");
    private static final Pattern PATRON_LETRAS = Pattern.compile("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ\\s]+$");
    private static final Pattern PATRON_CORREO = Pattern.compile("^[\\w.+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    
    private Nomina nomina;
    private List<Nomina> listaHistorico;
    private NominaService nominaService;
    private boolean modoEdicion;

    
    private Integer mesSeleccionado;
    private Integer anioSeleccionado;
    private String idOriginal;
    private String mesOriginal;
    private boolean mostrarResultados;

    @PostConstruct
    public void init() {
        nominaService = new NominaService();
        limpiar();
        cargarHistorico();
    }

    public void cargarHistorico() {
        try {
            listaHistorico = nominaService.obtenerHistorico();
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar la información de nóminas.");
        }
    }

    public void calcular() {
        try {
            validarFormatoCampos();
            
            String mesTexto = String.format("%04d-%02d", anioSeleccionado, mesSeleccionado);
            nomina.setMesNomina(mesTexto);

            Nomina resultadoTemporal;

            if (modoEdicion) {
                resultadoTemporal = nominaService.actualizarNomina(
                    idOriginal, 
                    mesOriginal, 
                    nomina.getEmpleado(), 
                    nomina.getMesNomina(), 
                    nomina.getDiasTrabajados()
                );
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Nómina actualizada correctamente.");
            } else {
                resultadoTemporal = nominaService.guardarNuevaNomina(
                    nomina.getEmpleado(), 
                    nomina.getMesNomina(), 
                    nomina.getDiasTrabajados()
                );
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Nómina guardada correctamente.");
            }

            cargarHistorico();
            limpiar();

            
            this.nomina = resultadoTemporal;
            this.mostrarResultados = true;

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void prepararEdicion(Nomina nominaSeleccionada) {
        Empleado emp = nominaSeleccionada.getEmpleado();
        
        
        Empleado empEdicion = new Empleado(
            emp.getDocId(), 
            emp.getNombre(), 
            emp.getApellido(), 
            emp.getCorreo(), 
            emp.getSalarioBasico(), 
            emp.getCargo()
        );
        
        this.nomina = new Nomina(empEdicion);
        this.nomina.setDiasTrabajados(nominaSeleccionada.getDiasTrabajados());
        this.nomina.setMesNomina(nominaSeleccionada.getMesNomina());

        try {
            String[] partes = nominaSeleccionada.getMesNomina().split("-");
            this.anioSeleccionado = Integer.parseInt(partes[0]);
            this.mesSeleccionado = Integer.parseInt(partes[1]);
            
        } catch (Exception e) {
            this.anioSeleccionado = null;
            this.mesSeleccionado = null;
        }

        this.idOriginal = emp.getDocId();
        this.mesOriginal = nominaSeleccionada.getMesNomina();
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
        Empleado empNuevo = new Empleado("", "", "", "", 0.0, "");
        this.nomina = new Nomina(empNuevo);
        this.nomina.setDiasTrabajados(30);
        
        this.mesSeleccionado = null;
        this.anioSeleccionado = null;
        this.modoEdicion = false;
        this.mostrarResultados = false;
        this.idOriginal = null;
        this.mesOriginal = null;
    }

    public List<Integer> getAniosDisponibles() {
    	
        int anioActual = Year.now().getValue();
        List<Integer> anios = new ArrayList<>();
        for (int a = anioActual; a >= anioActual - 5; a--) {
            anios.add(a);
        }
        return anios;
    }

    private void validarFormatoCampos() {
        Empleado emp = nomina.getEmpleado();
        if (mesSeleccionado == null || anioSeleccionado == null) throw new IllegalArgumentException("Selecciona el mes y el año.");
        if (emp.getDocId() == null || !PATRON_NUMEROS.matcher(emp.getDocId().trim()).matches()) throw new IllegalArgumentException("La identificación debe contener solo números.");
        if (emp.getNombre() == null || !PATRON_LETRAS.matcher(emp.getNombre().trim()).matches()) throw new IllegalArgumentException("Los nombres solo deben contener letras.");
        if (emp.getApellido() == null || !PATRON_LETRAS.matcher(emp.getApellido().trim()).matches()) throw new IllegalArgumentException("Los apellidos solo deben contener letras.");
        if (emp.getCorreo() == null || !PATRON_CORREO.matcher(emp.getCorreo().trim()).matches()) throw new IllegalArgumentException("El correo no tiene un formato válido.");
        if (nomina.getDiasTrabajados() < 1 || nomina.getDiasTrabajados() > 30) throw new IllegalArgumentException("Días trabajados inválidos (1 - 30).");
    }

    private void mostrarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    
    public Nomina getNomina() { return nomina; }
    public void setNomina(Nomina nomina) { this.nomina = nomina; }

    public List<Nomina> getListaHistorico() { return listaHistorico; }
    public void setListaHistorico(List<Nomina> listaHistorico) { this.listaHistorico = listaHistorico; }

    public NominaService getNominaService() { return nominaService; }
    public void setNominaService(NominaService nominaService) { this.nominaService = nominaService; }

    public boolean isModoEdicion() { return modoEdicion; }
    public void setModoEdicion(boolean modoEdicion) { this.modoEdicion = modoEdicion; }

    public Integer getMesSeleccionado() { return mesSeleccionado; }
    public void setMesSeleccionado(Integer mesSeleccionado) { this.mesSeleccionado = mesSeleccionado; }

    public Integer getAnioSeleccionado() { return anioSeleccionado; }
    public void setAnioSeleccionado(Integer anioSeleccionado) { this.anioSeleccionado = anioSeleccionado; }

    public boolean isMostrarResultados() { return mostrarResultados; }
    public void setMostrarResultados(boolean mostrarResultados) { this.mostrarResultados = mostrarResultados; }
}