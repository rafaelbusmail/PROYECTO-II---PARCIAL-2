/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.datos;

import com.flowfree.interfaces.ISerializable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GestorArchivos implements ISerializable {


    @Override
    public void guardar(Object objeto, String ruta) throws IOException {
        File archivo = new File(ruta);
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos
                = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
        }
    }
    
    @Override
    public Object cargar(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(ruta))) {
            return ois.readObject();
        }
    }


    @Override
    public boolean existeArchivo(String ruta) {
        return Files.isRegularFile(Paths.get(ruta));
    }
}
