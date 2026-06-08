/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.datos;

import com.flowfree.interfaces.ISerializable;
import java.io.*;

public class GestorArchivos implements ISerializable {

    private static final String CARPETA_RAIZ = "datos/usuarios/";


    @Override
    public void guardar(Object objeto, String ruta) throws IOException {
        File archivo = new File(ruta);
        archivo.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
        }
    }


    @Override
    public Object cargar(String ruta)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ruta))) {
            return ois.readObject();
        }
    }


    @Override
    public boolean existeArchivo(String ruta) {
        return new File(ruta).exists();
    }


    public void crearCarpetaUsuario(String username) {
        new File(CARPETA_RAIZ + username).mkdirs();
    }

 
    public boolean eliminarCarpetaUsuario(String username) {
        File carpeta = new File(CARPETA_RAIZ + username);
        return eliminarCarpetaRecursivo(carpeta);
    }

    private boolean eliminarCarpetaRecursivo(File carpeta) {
        if (carpeta.isDirectory()) {
            File[] contenido = carpeta.listFiles();
            if (contenido != null) {
                for (File archivo : contenido) {
                    eliminarCarpetaRecursivo(archivo);
                }
            }
        }
        return carpeta.delete();
    }

  
    public static String getRutaPerfil(String username) {
        return CARPETA_RAIZ + username + "/perfil.bin";
    }

   
    public static String getRutaEstadisticas(String username) {
        return CARPETA_RAIZ + username + "/estadisticas.bin";
    }

   
    public static String getRutaHistorial(String username) {
        return CARPETA_RAIZ + username + "/historial.bin";
    }

    
    public static String getRutaPreferencias(String username) {
        return CARPETA_RAIZ + username + "/preferencias.bin";
    }


    public String[] obtenerUsuariosRegistrados() {
        File carpetaRaiz = new File(CARPETA_RAIZ);
        if (!carpetaRaiz.exists()) {
            return new String[0];
        }
        String[] carpetas = carpetaRaiz.list(
                (dir, name) -> new File(dir, name).isDirectory()
        );
        return carpetas != null ? carpetas : new String[0];
    }
}
