package com.edu.empresa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonManagerNomina implements Serializable {

    private static final long serialVersionUID = 1L;

    public String rutaArchivo;
    public transient ObjectMapper mapper;

    public JsonManagerNomina() {
        this.rutaArchivo = System.getProperty("user.dir") + File.separator + "datos_nominas.json";
    }

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    public <T> void guardarLista(String ruta, List<T> lista) throws Exception {
        File archivo = new File(ruta);
        getMapper().writerWithDefaultPrettyPrinter().writeValue(archivo, lista);
    }

    public <T> List<T> leerLista(String ruta, Class<T> claseElemento) throws Exception {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        CollectionType tipoLista = getMapper().getTypeFactory().constructCollectionType(List.class, claseElemento);
        return getMapper().readValue(archivo, tipoLista);
    }
}