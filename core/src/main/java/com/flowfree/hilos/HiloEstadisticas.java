/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.hilos;

import com.flowfree.datos.GestorUsuarios;
import com.flowfree.modelo.Usuario;

public class HiloEstadisticas extends Thread {

    public interface EstadisticasListener {

        void onTiempoActualizado(long segundosTotales);
    }

    private static final int INTERVALO_MS = 1000; 

    private volatile boolean corriendo;
    private volatile boolean jugando;
    private GestorUsuarios gestorUsuarios;
    private EstadisticasListener listener;
    private long segundosEnPartida;

    public HiloEstadisticas(GestorUsuarios gestorUsuarios,
            EstadisticasListener listener) {
        this.gestorUsuarios = gestorUsuarios;
        this.listener = listener;
        this.corriendo = false;
        this.jugando = false;
        this.segundosEnPartida = 0;
        setDaemon(true);
        setName("HiloEstadisticas");
    }

    @Override
    public void run() {
        corriendo = true;

        while (corriendo) {
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (jugando) {
                segundosEnPartida++;
                if (listener != null) {
                    listener.onTiempoActualizado(segundosEnPartida);
                }
            }
        }
    }

    public void iniciarConteo() {
        jugando = true;
        segundosEnPartida = 0;
    }

    public void pausarConteo() {
        jugando = false;
    }

    public void reanudarConteo() {
        jugando = true;
    }

    public void detener() {
        corriendo = false;
        jugando = false;
        interrupt();
    }

    public long getSegundosEnPartida() {
        return segundosEnPartida;
    }
}
