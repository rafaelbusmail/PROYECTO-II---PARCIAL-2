package com.flowfree.hilos;

import com.flowfree.datos.GestorUsuarios;
import com.flowfree.modelo.Usuario;

public class HiloAutoguardado extends Thread {

    private static final int INTERVALO_MS = 30000; 

    private volatile boolean corriendo;
    private GestorUsuarios gestorUsuarios;

    public HiloAutoguardado(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
        this.corriendo = false;
        setDaemon(true);
        setName("HiloAutoguardado");
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

            guardarProgreso();
        }
    }

    private synchronized void guardarProgreso() {
        try {
            Usuario actual = gestorUsuarios.getUsuarioActual();
            if (actual != null) {
                gestorUsuarios.actualizarUsuario(actual);
                System.out.println("[Autoguardado] Progreso guardado: "
                        + actual.getUsername());
            }
        } catch (Exception e) {
            System.err.println("[Autoguardado] Error: " + e.getMessage());
        }
    }

    public void guardarAhora() {
        guardarProgreso();
    }

    public void detener() {
        corriendo = false;
        interrupt();
    }

    public boolean isCorriendo() {
        return corriendo;
    }
}
