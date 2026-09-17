package com.edu.empresa.service;

import com.edu.empresa.model.*;
import com.edu.empresa.util.JsonManagerNomina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NominaService implements Serializable {

    private static final long serialVersionUID = 1L;

    private JsonManagerNomina jsonManager;
    private Nomina nomina;

    public NominaService() {
        this.jsonManager = new JsonManagerNomina();
    }

    public List<Nomina> obtenerHistorico() throws Exception {
        List<Nomina> lista = jsonManager.leerLista(jsonManager.rutaArchivo, Nomina.class);
        for (Nomina n : lista) {
            n.calcularNomina();
        }
        return lista;
    }

    public Nomina guardarNuevaNomina(Empleado nuevoEmpleado, String mesNomina, int diasTrabajados) throws Exception {
        if (!nuevoEmpleado.validar()) {
            throw new IllegalArgumentException("Datos del empleado inválidos.");
        }

        List<Nomina> historico = jsonManager.leerLista(jsonManager.rutaArchivo, Nomina.class);

        for (Nomina n : historico) {
            if (n.getEmpleado().getDocId().equals(nuevoEmpleado.getDocId()) && n.getMesNomina().equals(mesNomina)) {
                throw new IllegalArgumentException("Ya existe una nómina de este empleado para ese mes. Usa 'Editar' si quieres modificarla.");
            }
        }

        List<Empleado> empleadosExistentes = new ArrayList<>();
        for (Nomina n : historico) {
            empleadosExistentes.add(n.getEmpleado());
        }
        verificarIdentidadUnica(empleadosExistentes, nuevoEmpleado);

        Nomina nuevaNomina = new Nomina(nuevoEmpleado);
        nuevaNomina.setMesNomina(mesNomina);
        nuevaNomina.setDiasTrabajados(diasTrabajados);
        nuevaNomina.calcularNomina();

        historico.add(nuevaNomina);
        jsonManager.guardarLista(jsonManager.rutaArchivo, historico);

        return nuevaNomina;
    }

    public Nomina actualizarNomina(String docIdOriginal, String mesNominaOriginal, Empleado empleadoActualizado, String nuevoMes, int nuevosDias) throws Exception {
        if (!empleadoActualizado.validar()) {
            throw new IllegalArgumentException("Datos del empleado inválidos.");
        }

        List<Nomina> historico = jsonManager.leerLista(jsonManager.rutaArchivo, Nomina.class);
        boolean encontrado = false;
        Nomina nominaActualizada = null;

        List<Empleado> otrosEmpleados = new ArrayList<>();
        for (Nomina n : historico) {
            if (!(n.getEmpleado().getDocId().equals(docIdOriginal) && n.getMesNomina().equals(mesNominaOriginal))) {
                otrosEmpleados.add(n.getEmpleado());
            }
        }
        verificarIdentidadUnica(otrosEmpleados, empleadoActualizado);

        for (int i = 0; i < historico.size(); i++) {
            Nomina n = historico.get(i);
            if (n.getEmpleado().getDocId().equals(docIdOriginal) && n.getMesNomina().equals(mesNominaOriginal)) {
                n.setEmpleado(empleadoActualizado);
                n.setMesNomina(nuevoMes);
                n.setDiasTrabajados(nuevosDias);
                n.calcularNomina();
                historico.set(i, n);
                nominaActualizada = n;
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IllegalArgumentException("No se encontró esa nómina para actualizar.");
        }

        jsonManager.guardarLista(jsonManager.rutaArchivo, historico);
        return nominaActualizada;
    }

    public boolean eliminarNomina(String docId, String mesNomina) throws Exception {
        List<Nomina> historico = jsonManager.leerLista(jsonManager.rutaArchivo, Nomina.class);
        boolean eliminado = historico.removeIf(n -> n.getEmpleado().getDocId().equals(docId) && n.getMesNomina().equals(mesNomina));

        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró esa nómina para eliminar.");
        }

        jsonManager.guardarLista(jsonManager.rutaArchivo, historico);
        return eliminado;
    }

    private void verificarIdentidadUnica(List<Empleado> empleados, Empleado nuevo) {
        for (Empleado emp : empleados) {
            if (emp.getDocId().equals(nuevo.getDocId())) {
                boolean nombreDiferente = !emp.getNombre().equalsIgnoreCase(nuevo.getNombre());
                boolean apellidoDiferente = !emp.getApellido().equalsIgnoreCase(nuevo.getApellido());
                if (nombreDiferente || apellidoDiferente) {
                    throw new IllegalArgumentException("Esa identificación ya está registrada a nombre de otra persona.");
                }
            }
        }
    }
}