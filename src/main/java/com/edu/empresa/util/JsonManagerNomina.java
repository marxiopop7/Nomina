
package com.edu.empresa.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.edu.empresa.model.Empleado;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonManagerNomina implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rutaArchivo;

    private transient ObjectMapper mapper;

    public JsonManagerNomina() {
        
        this.rutaArchivo = System.getProperty("user.home") + File.separator + "datos_empleados.json";
    }

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    
    public void guardarListaEmpleados(List<Empleado> empleados) throws Exception {
        File archivo = new File(rutaArchivo);
        getMapper().writerWithDefaultPrettyPrinter().writeValue(archivo, empleados);
    }

    public List<Empleado> procesarEmpleados() throws Exception {
        List<Empleado> listaEmpleados = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return listaEmpleados; 
        }

        try (InputStream inputStream = new FileInputStream(archivo)) {
            JsonNode root = getMapper().readTree(inputStream);

            if (root != null && root.isArray()) {
                for (JsonNode node : root) {

                    
                    String docId = node.get("docId").asText();
                    String nombre = node.get("nombre").asText();
                    String apellido = node.get("apellido").asText();
                    String correo = node.get("correo").asText();
                    double salarioBasico = node.get("salarioBasico").asDouble();
                    int diasTrabajados = node.get("diasTrabajados").asInt();
                    String mesNomina = node.get("mesNomina").asText();

                    Empleado emp = new Empleado(docId, nombre, apellido, correo, salarioBasico, diasTrabajados, mesNomina);
                    listaEmpleados.add(emp);
                }
            }
        }
        return listaEmpleados;
    }

    public boolean actualizarEmpleado(String docIdOriginal, String mesNominaOriginal, Empleado empleadoActualizado) throws Exception {
        List<Empleado> empleados = procesarEmpleados();
        boolean encontrado = false;

        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            if (e.getDocId().equals(docIdOriginal) && e.getMesNomina().equals(mesNominaOriginal)) {
                empleados.set(i, empleadoActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            guardarListaEmpleados(empleados);
        }
        return encontrado;
    }

    public boolean eliminarEmpleado(String docId, String mesNomina) throws Exception {
        List<Empleado> empleados = procesarEmpleados();
        boolean removido = empleados.removeIf(e -> e.getDocId().equals(docId) && e.getMesNomina().equals(mesNomina));

        if (removido) {
            guardarListaEmpleados(empleados);
        }
        return removido;
    }
}


