package com.flowfree.modelo;

import java.io.Serializable;

public class Reto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String remitente;
    private String destinatario;
    private int nivel;
    private long tiempoRemitente;
    private int puntajeRemitente;
    private EstadoReto estado;
    private long tiempoDestinatario;
    private int puntajeDestinatario;
    private String ganador;

    public enum EstadoReto {
        PENDIENTE, ACEPTADO, COMPLETADO, RECHAZADO
    }

    public Reto(String remitente, String destinatario, int nivel,
                long tiempoRemitente, int puntajeRemitente) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.nivel = nivel;
        this.tiempoRemitente = tiempoRemitente;
        this.puntajeRemitente = puntajeRemitente;
        this.estado = EstadoReto.PENDIENTE;
        this.tiempoDestinatario = 0;
        this.puntajeDestinatario = 0;
        this.ganador = null;
    }

    public String getRemitente() { return remitente; }
    public String getDestinatario() { return destinatario; }
    public int getNivel() { return nivel; }
    public long getTiempoRemitente() { return tiempoRemitente; }
    public int getPuntajeRemitente() { return puntajeRemitente; }
    public EstadoReto getEstado() { return estado; }
    public long getTiempoDestinatario() { return tiempoDestinatario; }
    public int getPuntajeDestinatario() { return puntajeDestinatario; }
    public String getGanador() { return ganador; }

    public void setEstado(EstadoReto estado) { this.estado = estado; }

    public void rechazarReto() {
        this.estado = EstadoReto.RECHAZADO;
    }

    public void completarReto(long tiempoDestinatario, int puntajeDestinatario) {
        this.tiempoDestinatario = tiempoDestinatario;
        this.puntajeDestinatario = puntajeDestinatario;
        this.estado = EstadoReto.COMPLETADO;
        if (tiempoRemitente < tiempoDestinatario) {
            this.ganador = remitente;
        } else if (tiempoDestinatario < tiempoRemitente) {
            this.ganador = destinatario;
        } else {
            this.ganador = "EMPATE";
        }
    }
}
