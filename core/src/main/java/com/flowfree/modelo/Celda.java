/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.modelo;

import com.flowfree.enums.ColorFlujo;
import com.flowfree.enums.EstadoCelda;

public class Celda {

    private int fila;
    private int columna;
    private EstadoCelda estado;
    private ColorFlujo color;      
    private boolean esPuntoFijo; 
    
    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoCelda.VACIA;
        this.color = null;
        this.esPuntoFijo = false;
    }

    public void setPuntoFijo(ColorFlujo color) {
        this.color = color;
        this.estado = EstadoCelda.PUNTO_INICIO;
        this.esPuntoFijo = true;
    }

    public void setFlujo(ColorFlujo color) {
        if (esPuntoFijo) {
            return; 
        }
        this.color = color;
        this.estado = EstadoCelda.FLUJO;
    }

    public void limpiar() {
        if (esPuntoFijo) {
            return;
        }
        this.color = null;
        this.estado = EstadoCelda.VACIA;
    }

    public boolean estaVacia() {
        return estado == EstadoCelda.VACIA;
    }

    public boolean esFlujo() {
        return estado == EstadoCelda.FLUJO;
    }

    public boolean esPuntoFijo() {
        return esPuntoFijo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public EstadoCelda getEstado() {
        return estado;
    }

    public ColorFlujo getColor() {
        return color;
    }
}
