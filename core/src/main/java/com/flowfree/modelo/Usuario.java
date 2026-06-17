package com.flowfree.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String nombreCompleto;
    private LocalDate fechaRegistro;
    private LocalDate ultimaConexion;

    private int nivelActual;
    private int nivelMaxDesbloqueado;
    private long tiempoTotalJugadoSegundos;
    private double puntuacionGeneral;
    private String avatarRuta;

    
    private Estadisticas estadisticas;

    private List<HistorialPartida> historial;
    private List<String> amigos;
    private List<String> solicitudesPendientes;
    private boolean activo;

    public Usuario() {
        inicializarDefaults();
    }

    public Usuario(String username, String password,
            String nombreCompleto, LocalDate fechaRegistro) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = fechaRegistro;
        inicializarDefaults();
        this.ultimaConexion = fechaRegistro;
    }

    private void inicializarDefaults() {
        nivelActual = 1;
        nivelMaxDesbloqueado = 1;
        tiempoTotalJugadoSegundos = 0L;
        puntuacionGeneral = 0.0;
        avatarRuta = "";
        estadisticas = new Estadisticas(username != null ? username : "");
        historial = new ArrayList<>();
        amigos = new ArrayList<>();
        solicitudesPendientes = new ArrayList<>();
        activo = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public LocalDate getUltimaConexion() {
        return ultimaConexion;
    }

    public LocalDate getUltimaSesion() {
        return ultimaConexion;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public int getNivelMaxDesbloqueado() {
        return nivelMaxDesbloqueado;
    }

    public long getTiempoTotalJugadoSegundos() {
        return tiempoTotalJugadoSegundos;
    }

    public double getPuntuacionGeneral() {
        return puntuacionGeneral;
    }

    public String getAvatarRuta() {
        return avatarRuta;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public List<HistorialPartida> getHistorial() {
        return historial;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<String> getAmigos() {
        if (amigos == null) {
            amigos = new ArrayList<>();
        }
        return amigos;
    }

    public boolean agregarAmigo(String usernameAmigo) {
        if (usernameAmigo == null || usernameAmigo.equals(this.username)) {
            return false;
        }
        String upper = usernameAmigo.toUpperCase();
        if (!amigos.contains(upper)) {
            amigos.add(upper);
            return true;
        }
        return false;
    }

    public boolean eliminarAmigo(String usernameAmigo) {
        return amigos.remove(usernameAmigo.toUpperCase());
    }

    public List<String> getSolicitudesPendientes() {
        if (solicitudesPendientes == null) {
            solicitudesPendientes = new ArrayList<>();
        }
        return solicitudesPendientes;
    }

    public boolean agregarSolicitud(String username) {
        String upper = username.toUpperCase();
        if (!getSolicitudesPendientes().contains(upper) && !upper.equals(this.username)) {
            solicitudesPendientes.add(upper);
            return true;
        }
        return false;
    }

    public boolean eliminarSolicitud(String username) {
        return getSolicitudesPendientes().remove(username.toUpperCase());
    }

    public void setUsername(String username) {
        this.username = username;
        if (estadisticas != null) {
            estadisticas.setUsername(username);
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setUltimaConexion(LocalDate ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public void setAvatarRuta(String avatarRuta) {
        this.avatarRuta = avatarRuta;
    }

    public void setPuntuacionGeneral(double p) {
        this.puntuacionGeneral = p;
    }

    public void setNivelActual(int nivel) {
        this.nivelActual = nivel;
        if (nivel > nivelMaxDesbloqueado) {
            nivelMaxDesbloqueado = nivel;
        }
    }

    public void incrementarPartidas() {
        estadisticas.registrarFallo();
        }

    public void incrementarNivelesCompletados() {
         }

    public void agregarTiempo(long segundos) {
        tiempoTotalJugadoSegundos += segundos;
    }

    public void desbloquearSiguienteNivel() {
        nivelMaxDesbloqueado = Math.max(nivelMaxDesbloqueado, nivelActual + 1);
    }

    public void registrarPartida(HistorialPartida partida) {
        historial.add(partida);
        if (partida.isCompletado()) {
            estadisticas.registrarNivelCompletado(
                    partida.getPuntajeObtenido(),
                    partida.getTiempoEmpleado(),
                    partida.getMovimientos());
            int nivelJugado = partida.getNivelJugado();
            if (nivelJugado >= nivelMaxDesbloqueado) {
                nivelMaxDesbloqueado = nivelJugado + 1;
            }
            puntuacionGeneral += partida.getPuntajeObtenido();
        } else {
            estadisticas.registrarFallo();
        }
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "username='" + username + '\''
                + ", nombreCompleto='" + nombreCompleto + '\''
                + ", fechaRegistro=" + fechaRegistro
                + ", ultimaConexion=" + ultimaConexion
                + ", nivelActual=" + nivelActual
                + ", partidas=" + estadisticas.getPartidasJugadas()
                + '}';
    }
}
