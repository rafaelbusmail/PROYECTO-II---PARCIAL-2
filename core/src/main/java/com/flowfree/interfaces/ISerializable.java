/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.interfaces;

import java.io.IOException;

public interface ISerializable {

    void guardar(Object objeto, String ruta) throws IOException;

    Object cargar(String ruta) throws IOException, ClassNotFoundException;

    boolean existeArchivo(String ruta);
}
