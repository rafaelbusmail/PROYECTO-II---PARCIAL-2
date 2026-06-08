/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.modelo;

import com.flowfree.enums.Idioma;
import java.io.Serializable;

public class Preferencias implements Serializable {

    private static final long serialVersionUID = 1L;

    private float volumenMusica;
    private float volumenSonido;
    private Idioma idioma;
    private boolean mostrarTimer;
    private boolean modoOscuro;

    public Preferencias() {
        this.volumenMusica = 0.7f;
        this.volumenSonido = 0.8f;
        this.idioma = Idioma.ES;
        this.mostrarTimer = true;
        this.modoOscuro = false;
    }

    public float getVolumenMusica() {
        return volumenMusica;
    }

    public float getVolumenSonido() {
        return volumenSonido;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public boolean isMostrarTimer() {
        return mostrarTimer;
    }

    public boolean isModoOscuro() {
        return modoOscuro;
    }

    public void setVolumenMusica(float v) {
        this.volumenMusica = v;
    }

    public void setVolumenSonido(float v) {
        this.volumenSonido = v;
    }

    public void setIdioma(Idioma i) {
        this.idioma = i;
    }

    public void setMostrarTimer(boolean b) {
        this.mostrarTimer = b;
    }

    public void setModoOscuro(boolean b) {
        this.modoOscuro = b;
    }
}
