/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.enums;

import com.badlogic.gdx.graphics.Color;

public enum ColorFlujo {

    ROJO    (new Color(0.9f, 0.15f, 0.15f, 1f), "Rojo"),
    AZUL    (new Color(0.15f, 0.40f, 0.9f,  1f), "Azul"),
    VERDE   (new Color(0.10f, 0.75f, 0.20f, 1f), "Verde"),
    AMARILLO(new Color(0.95f, 0.85f, 0.10f, 1f), "Amarillo"),
    NARANJA (new Color(0.95f, 0.50f, 0.05f, 1f), "Naranja"),
    MORADO  (new Color(0.55f, 0.10f, 0.80f, 1f), "Morado"),
    CYAN    (new Color(0.10f, 0.85f, 0.90f, 1f), "Cyan"),
    MARRON  (new Color(0.55f, 0.27f, 0.07f, 1f), "Marrón"),
    ROSADO  (new Color(0.95f, 0.45f, 0.75f, 1f), "Rosado"),
    GRIS    (new Color(0.55f, 0.55f, 0.55f, 1f), "Gris");

    private final Color colorGDX;
    private final String nombre;

    ColorFlujo(Color colorGDX, String nombre) {
        this.colorGDX = colorGDX;
        this.nombre   = nombre;
    }

    public Color getColorGDX() {
        return colorGDX;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColorOscuro() {
        return new Color(
            colorGDX.r * 0.6f,
            colorGDX.g * 0.6f,
            colorGDX.b * 0.6f,
            1f
        );
    }
}