
package com.edu.empresa.service;

import com.edu.empresa.model.*;

import com.edu.empresa.util.JsonManagerNomina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NominaService implements Serializable {

    private static final long serialVersionUID = 1L;

    private JsonManagerNomina jsonManager;

    public NominaService() {
        this.jsonManager = new JsonManagerNomina();
    }

    
    public List<Nomina> obtenerHistorico() throws Exception {
    	
        List<Empleado> empleadosGuardados = jsonManager.procesarEmpleados();
        List<Nomina> historialCalculado = new ArrayList<>();

        for (Empleado emp : empleadosGuardados) {
            Nomina nomina = new Nomina(emp);
            nomina.calcularNomina(); 
            historialCalculado.add(nomina);
        }

        return historialCalculado;
    }

    
    public Nomina guardarNuevaNomina(Empleado nuevoEmpleado) throws Exception {
    	
        if (!nuevoEmpleado.validar()) {
            throw new IllegalArgumentException("Datos del empleado inválidos.");
        }

        List<Empleado> empleados = jsonManager.procesarEmpleados();

        for (Empleado emp : empleados) {
            boolean mismoDocumento = emp.getDocId().equals(nuevoEmpleado.getDocId());
            boolean mismoMes = emp.getMesNomina().equals(nuevoEmpleado.getMesNomina());
            
            if (mismoDocumento && mismoMes) {
                throw new IllegalArgumentException("Ya existe una nómina de este empleado para ese mes. Usa 'Editar' si quieres modificarla.");
            }
        }

        
        verificarIdentidadUnica(empleados, nuevoEmpleado);
        empleados.add(nuevoEmpleado);
        jsonManager.guardarListaEmpleados(empleados);        
        Nomina nuevaNomina = new Nomina(nuevoEmpleado);
        nuevaNomina.calcularNomina();
        return nuevaNomina;
    }

    public Nomina actualizarNomina(String docIdOriginal, String mesNominaOriginal, Empleado empleadoActualizado) throws Exception {
        
    	if (!empleadoActualizado.validar()) {
            throw new IllegalArgumentException("Datos del empleado inválidos.");
        }

    	List<Empleado> todosLosEmpleados = jsonManager.procesarEmpleados();
        List<Empleado> otrosEmpleados = new ArrayList<>();
        
        for (Empleado emp : todosLosEmpleados) {
            boolean esElMismoDocumento = emp.getDocId().equals(docIdOriginal);
            boolean esElMismoMes = emp.getMesNomina().equals(mesNominaOriginal);
            
            if (!(esElMismoDocumento && esElMismoMes)) {
                otrosEmpleados.add(emp);
            }
        }
        
        
        verificarIdentidadUnica(otrosEmpleados, empleadoActualizado);

        boolean actualizado = jsonManager.actualizarEmpleado(docIdOriginal, mesNominaOriginal, empleadoActualizado);
        if (!actualizado) {
            throw new IllegalArgumentException("No se encontró esa nómina (documento + mes) para actualizar.");
        }

        Nomina nominaActualizada = new Nomina(empleadoActualizado);
        nominaActualizada.calcularNomina();
        return nominaActualizada;
    }

    private void verificarIdentidadUnica(List<Empleado> empleados, Empleado nuevo) {
        for (Empleado emp : empleados) {
            if (emp.getDocId().equals(nuevo.getDocId())) {
                
                boolean nombreDiferente = !emp.getNombre().equalsIgnoreCase(nuevo.getNombre());
                boolean apellidoDiferente = !emp.getApellido().equalsIgnoreCase(nuevo.getApellido());
                
                if (nombreDiferente || apellidoDiferente) {
                    throw new IllegalArgumentException(
                        "Esa identificación ya está registrada a nombre de otra persona. Verifica el número de documento.");
                }
            }
        }
    }

    public boolean eliminarNomina(String docId, String mesNomina) throws Exception {
        boolean eliminado = jsonManager.eliminarEmpleado(docId, mesNomina);
        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró esa nómina (documento + mes) para eliminar.");
        }
        return eliminado;
    }
}


