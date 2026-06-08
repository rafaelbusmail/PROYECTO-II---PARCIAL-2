/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private Date fechaRegistro;
    private Date ultimaSesion;
    private int nivelMaxDesbloqueado;
    private Estadisticas estadisticas;
    private List<HistorialPartida> historial;
    private Preferencias preferencias;
    private String rutaAvatar;
    private List<String> amigos;

    public Usuario(String username, String passwordHash,
            String nombreCompleto) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = new Date();
        this.ultimaSesion = new Date();
        this.nivelMaxDesbloqueado = 1;
        this.estadisticas = new Estadisticas(username);
        this.historial = new ArrayList<>();
        this.preferencias = new Preferencias();
        this.rutaAvatar = "avatares/default.png";
        this.amigos = new ArrayList<>();
    }

    public void agregarPartida(HistorialPartida partida) {
        historial.add(0, partida); 
        if (historial.size() > 50) {
            historial.remove(historial.size() - 1);
        }
    }

    public void actualizarSesion() {
        this.ultimaSesion = new Date();
    }

    public void desbloquearNivel(int nivel) {
        if (nivel > nivelMaxDesbloqueado) {
            nivelMaxDesbloqueado = nivel;
        }
    }

    public boolean esNivelDesbloqueado(int nivel) {
        return nivel <= nivelMaxDesbloqueado;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public Date getUltimaSesion() {
        return ultimaSesion;
    }

    public int getNivelMaxDesbloqueado() {
        return nivelMaxDesbloqueado;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public List<HistorialPartida> getHistorial() {
        return historial;
    }

    public Preferencias getPreferencias() {
        return preferencias;
    }

    public String getRutaAvatar() {
        return rutaAvatar;
    }

    public List<String> getAmigos() {
        return amigos;
    }

    public void setPasswordHash(String hash) {
        this.passwordHash = hash;
    }

    public void setNombreCompleto(String nombre) {
        this.nombreCompleto = nombre;
    }

    public void setRutaAvatar(String ruta) {
        this.rutaAvatar = ruta;
    }

    public void setPreferencias(Preferencias p) {
        this.preferencias = p;
    }

    public void setEstadisticas(Estadisticas e) {
        this.estadisticas = e;
    }

    public void agregarAmigo(String username) {
        if (!amigos.contains(username)) {
            amigos.add(username);
        }
    }
}
