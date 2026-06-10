package com.flowfree.modelo;

import java.io.Serializable;

public class Estadisticas implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private int partidasJugadas;
    private int nivelesCompletados;
    private int nivelMaxAlcanzado;
    private long tiempoTotalJugado;
    private int puntajeTotal;
    private int mejorPuntaje;
    private int movimientosTotales;
    private int fallosTotales;

    public Estadisticas(String username) {
        this.username = username;
        this.partidasJugadas = 0;
        this.nivelesCompletados = 0;
        this.nivelMaxAlcanzado = 1;
        this.tiempoTotalJugado = 0;
        this.puntajeTotal = 0;
        this.mejorPuntaje = 0;
        this.movimientosTotales = 0;
        this.fallosTotales = 0;
    }

    public Estadisticas() {
        this("");
    }

    public void registrarNivelCompletado(int puntaje, long tiempo, int movimientos) {
        nivelesCompletados++;
        partidasJugadas++;
        puntajeTotal += puntaje;
        tiempoTotalJugado += tiempo;
        movimientosTotales += movimientos;
        if (puntaje > mejorPuntaje) {
            mejorPuntaje = puntaje;
        }
    }

    public void registrarFallo() {
        fallosTotales++;
        partidasJugadas++;
    }

    public double getTiempoPromedioPorNivel() {
        if (nivelesCompletados == 0) {
            return 0;
        }
        return (double) tiempoTotalJugado / nivelesCompletados;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNivelMaxAlcanzado(int n) {
        if (n > nivelMaxAlcanzado) {
            nivelMaxAlcanzado = n;
        }
    }

    public String getUsername() {
        return username;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public int getNivelesCompletados() {
        return nivelesCompletados;
    }

    public int getNivelMaxAlcanzado() {
        return nivelMaxAlcanzado;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public int getMejorPuntaje() {
        return mejorPuntaje;
    }

    public int getMovimientosTotales() {
        return movimientosTotales;
    }

    public int getFallosTotales() {
        return fallosTotales;
    }
}
