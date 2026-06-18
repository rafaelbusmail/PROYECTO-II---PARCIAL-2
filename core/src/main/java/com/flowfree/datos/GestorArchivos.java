package com.flowfree.datos;

import com.flowfree.interfaces.ISerializable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorArchivos implements ISerializable {

    public GestorArchivos() {
    }

    @Override
    public void guardar(Object objeto, String ruta) throws IOException {
        File archivo = new File(ruta);

        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
        }
    }

    @Override
    public Object cargar(String ruta) throws IOException, ClassNotFoundException {
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            throw new FileNotFoundException("Archivo no encontrado: " + ruta);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return ois.readObject();
        }
    }

    @Override
    public boolean existeArchivo(String ruta) {
        return Files.isRegularFile(Paths.get(ruta));
    }
}
