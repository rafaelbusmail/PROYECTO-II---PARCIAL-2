/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.abstracts;

import com.flowfree.enums.EstadoJuego;

public abstract class Juego {

    protected String nombre;
    protected EstadoJuego estado;
    protected int nivelActual;
    protected int vidas;
    protected int puntaje;
    protected long tiempoInicio;
    protected long tiempoTranscurrido; 

    public Juego(String nombre) {
        this.nombre = nombre;
        this.estado = EstadoJuego.MENU;
        this.nivelActual = 1;
        this.vidas = 3;
        this.puntaje = 0;
        this.tiempoInicio = 0;
    }

    public abstract void iniciar();

    public abstract void pausar();

    public abstract void reanudar();

    public abstract void reiniciar();

    public abstract boolean verificarVictoria();

    public abstract int calcularPuntaje();

    public abstract void cargarNivel(int numeroNivel);

    public abstract void procesarInput(float x, float y,
            boolean presionado);

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
        if (vidas == 0) {
            estado = EstadoJuego.GAME_OVER;
        }
    }

    public boolean estaActivo() {
        return estado == EstadoJuego.JUGANDO;
    }

    public boolean estaCompletado() {
        return estado == EstadoJuego.COMPLETADO;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public long getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public void setEstado(EstadoJuego estado) {
        this.estado = estado;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}
