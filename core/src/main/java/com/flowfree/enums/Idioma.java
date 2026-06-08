/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.enums;

public enum Idioma {
    ES("Español"),
    EN("English");

    private final String nombre;

    Idioma(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}