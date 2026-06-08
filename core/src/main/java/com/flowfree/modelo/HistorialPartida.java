/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.modelo;

import java.io.Serializable;
import java.util.Date;

public class HistorialPartida implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date fecha;
    private int nivelJugado;
    private int puntajeObtenido;
    private long tiempoEmpleado;   
    private int movimientos;
    private boolean completado;
    private String resultado;        

    public HistorialPartida(int nivelJugado, int puntaje,
            long tiempo, int movimientos,
            boolean completado, String resultado) {
        this.fecha = new Date();
        this.nivelJugado = nivelJugado;
        this.puntajeObtenido = puntaje;
        this.tiempoEmpleado = tiempo;
        this.movimientos = movimientos;
        this.completado = completado;
        this.resultado = resultado;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getNivelJugado() {
        return nivelJugado;
    }

    public int getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public long getTiempoEmpleado() {
        return tiempoEmpleado;
    }

    public int getMovimientos() {
        return movimientos;
    }

    public boolean isCompletado() {
        return completado;
    }

    public String getResultado() {
        return resultado;
    }

    @Override
    public String toString() {
        return String.format("Nivel %d | %s | Puntaje: %d | Tiempo: %ds",
                nivelJugado, resultado, puntajeObtenido, tiempoEmpleado);
    }
}
