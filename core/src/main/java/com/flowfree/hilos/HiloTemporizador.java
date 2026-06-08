/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.hilos;

public class HiloTemporizador extends Thread {

    public interface TimerListener {

        void onTick(int segundosRestantes);

        void onTiempoAgotado();
    }

    private volatile boolean corriendo;
    private volatile boolean pausado;
    private int segundosRestantes;
    private final int segundosTotales;
    private TimerListener listener;
    private final Object pauseLock = new Object();

    public HiloTemporizador(int segundosTotales, TimerListener listener) {
        this.segundosTotales = segundosTotales;
        this.segundosRestantes = segundosTotales;
        this.listener = listener;
        this.corriendo = false;
        this.pausado = false;
        setDaemon(true); 
        setName("HiloTemporizador");
    }

    @Override
    public void run() {
        corriendo = true;

        while (corriendo && segundosRestantes > 0) {
            synchronized (pauseLock) {
                while (pausado && corriendo) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (corriendo && !pausado) {
                segundosRestantes--;
                if (listener != null) {
                    listener.onTick(segundosRestantes);
                }
            }
        }

        if (corriendo && segundosRestantes <= 0) {
            if (listener != null) {
                listener.onTiempoAgotado();
            }
        }
    }

    public void pausar() {
        synchronized (pauseLock) {
            pausado = true;
        }
    }

    public void reanudar() {
        synchronized (pauseLock) {
            pausado = false;
            pauseLock.notifyAll();
        }
    }

    public void detener() {
        corriendo = false;
        reanudar(); 
        interrupt();
    }

    public void reiniciar() {
        segundosRestantes = segundosTotales;
    }

    public int getSegundosRestantes() {
        return segundosRestantes;
    }

    public boolean isPausado() {
        return pausado;
    }

    public boolean isCorriendo() {
        return corriendo;
    }


    public String getTiempoFormateado() {
        int min = segundosRestantes / 60;
        int seg = segundosRestantes % 60;
        return String.format("%02d:%02d", min, seg);
    }
}
