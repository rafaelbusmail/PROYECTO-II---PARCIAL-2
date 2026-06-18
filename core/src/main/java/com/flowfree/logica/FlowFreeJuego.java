/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.logica;

import com.flowfree.abstracts.Juego;
import com.flowfree.enums.ColorFlujo;
import com.flowfree.enums.EstadoJuego;
import com.flowfree.modelo.Celda;
import com.flowfree.modelo.Nivel;
import java.util.ArrayList;
import java.util.List;

public class FlowFreeJuego extends Juego {

    private Celda[][] grid;
    private Nivel nivelConfig;

    private ColorFlujo colorActivo;
    private List<int[]> rutaActiva;  

    private int movimientos;
    private int totalCeldas;

    public interface JuegoListener {

        void onVictoria(int puntaje, long tiempo, int movimientos);

        void onTiempoAgotado();
    }
    private JuegoListener listener;

    public FlowFreeJuego() {
        super("Flow Free");
        this.rutaActiva = new ArrayList<>();
        this.movimientos = 0;
    }

    public void setListener(JuegoListener l) {
        this.listener = l;
    }

    @Override
    public void iniciar() {
        estado = EstadoJuego.JUGANDO;
        tiempoInicio = System.currentTimeMillis();
        movimientos = 0;
        colorActivo = null;
        rutaActiva.clear();
    }

    @Override
    public void pausar() {
        if (estado == EstadoJuego.JUGANDO) {
            estado = EstadoJuego.PAUSADO;
        }
    }

    @Override
    public void reanudar() {
        if (estado == EstadoJuego.PAUSADO) {
            estado = EstadoJuego.JUGANDO;
        }
    }

    @Override
    public void reiniciar() {
        if (nivelConfig == null) {
            return;
        }
        grid = nivelConfig.construirGrid();
        movimientos = 0;
        colorActivo = null;
        rutaActiva.clear();
        estado = EstadoJuego.JUGANDO;
        tiempoInicio = System.currentTimeMillis();
    }

    @Override
    public void cargarNivel(int numeroNivel) {
        nivelConfig = Nivel.getNivel(numeroNivel);
        nivelActual = numeroNivel;
        grid = nivelConfig.construirGrid();
        totalCeldas = nivelConfig.getTamano() * nivelConfig.getTamano();
        movimientos = 0;
        colorActivo = null;
        rutaActiva.clear();
    }

    @Override
    public boolean verificarVictoria() {
        if (grid == null) {
            return false;
        }
        int tamano = nivelConfig.getTamano();
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (grid[f][c].estaVacia()) {
                    return false;
                }
            }
        }

        for (int i = 0; i < nivelConfig.getNumColores(); i++) {
            if (!colorConectado(nivelConfig.getColor(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean colorConectado(ColorFlujo color) {
        int tamano = nivelConfig.getTamano();
        List<int[]> puntosFijos = new ArrayList<>();
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (grid[f][c].esPuntoFijo() && grid[f][c].getColor() == color) {
                    puntosFijos.add(new int[]{f, c});
                }
            }
        }

        if (puntosFijos.size() < 2) {
            return false;
        }

        boolean[][] visitado = new boolean[tamano][tamano];
        List<int[]> cola = new ArrayList<>();
        cola.add(puntosFijos.get(0));
        visitado[puntosFijos.get(0)[0]][puntosFijos.get(0)[1]] = true;
        int[] destino = puntosFijos.get(1);

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!cola.isEmpty()) {
            int[] cur = cola.remove(0);
            if (cur[0] == destino[0] && cur[1] == destino[1]) {
                return true;
            }
            for (int[] d : dirs) {
                int nf = cur[0] + d[0], nc = cur[1] + d[1];
                if (nf < 0 || nf >= tamano || nc < 0 || nc >= tamano) {
                    continue;
                }
                if (visitado[nf][nc]) {
                    continue;
                }
                if (grid[nf][nc].getColor() != color) {
                    continue;
                }
                visitado[nf][nc] = true;
                cola.add(new int[]{nf, nc});
            }
        }
        return false;
    }

    @Override
    public int calcularPuntaje() {
        if (nivelConfig == null) {
            return 0;
        }
        int base = nivelConfig.getTamano() * 100;
        int bonusTiempo = (int) Math.max(0,
                1000 - (System.currentTimeMillis() - tiempoInicio) / 1000);
        int penalMov = movimientos * 2;
        return Math.max(0, base + bonusTiempo - penalMov);
    }

    @Override
    public void procesarInput(float x, float y, boolean presionado) {
    }

    public void iniciarRuta(int fila, int col) {
        if (estado != EstadoJuego.JUGANDO) {
            return;
        }
        Celda c = grid[fila][col];

        if (c.estaVacia()) {
            return;
        }

        if (c.esPuntoFijo()) {
            colorActivo = c.getColor();
            rutaActiva.clear();
            rutaActiva.add(new int[]{fila, col});
            limpiarColor(colorActivo);
        } else if (c.getColor() != null) {
            colorActivo = c.getColor();
            rutaActiva.clear();
            rutaActiva.add(new int[]{fila, col});
            limpiarColor(colorActivo);
        }
    }

    public boolean continuarRuta(int fila, int col) {
        if (estado != EstadoJuego.JUGANDO || colorActivo == null) {
            return false;
        }
        if (rutaActiva.isEmpty()) {
            return false;
        }

        int[] ultimo = rutaActiva.get(rutaActiva.size() - 1);

        if (!esAdyacente(ultimo[0], ultimo[1], fila, col)) {
            return false;
        }

        Celda destino = grid[fila][col];
        if (destino.esPuntoFijo() && destino.getColor() != colorActivo) {
            return false;
        }

        for (int i = 0; i < rutaActiva.size(); i++) {
            if (rutaActiva.get(i)[0] == fila && rutaActiva.get(i)[1] == col) {
                for (int j = rutaActiva.size() - 1; j > i; j--) {
                    int[] p = rutaActiva.remove(j);
                    if (!grid[p[0]][p[1]].esPuntoFijo()) {
                        grid[p[0]][p[1]].limpiar();
                    }
                }
                return true;
            }
        }

        // Cannot move beyond a fixed point that was entered mid-route (start is the only exit point)
        int[] lastInRoute = rutaActiva.get(rutaActiva.size() - 1);
        if (grid[lastInRoute[0]][lastInRoute[1]].esPuntoFijo() && rutaActiva.size() > 1) {
            return false;
        }

        // Prevent phasing through any already-colored non-fixed cell
        if (!destino.esPuntoFijo() && destino.getColor() != null) {
            return false;
        }

        if (!destino.esPuntoFijo()) {
            destino.setFlujo(colorActivo);
        }
        rutaActiva.add(new int[]{fila, col});
        movimientos++;
        return true;
    }

    public void terminarRuta() {
        if (colorActivo == null) {
            return;
        }

        if (rutaActiva.size() >= 2) {
            int[] fin = rutaActiva.get(rutaActiva.size() - 1);
            Celda celdaFin = grid[fin[0]][fin[1]];
            if (celdaFin.esPuntoFijo() && celdaFin.getColor() == colorActivo) {
                if (verificarVictoria()) {
                    estado = EstadoJuego.COMPLETADO;
                    tiempoTranscurrido
                            = (System.currentTimeMillis() - tiempoInicio) / 1000;
                    if (listener != null) {
                        listener.onVictoria(calcularPuntaje(),
                                tiempoTranscurrido, movimientos);
                    }
                }
            }
        }

        colorActivo = null;
        rutaActiva.clear();
    }

    private void limpiarColor(ColorFlujo color) {
        int tamano = nivelConfig.getTamano();
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (!grid[f][c].esPuntoFijo()
                        && grid[f][c].getColor() == color) {
                    grid[f][c].limpiar();
                }
            }
        }
    }

    private boolean esAdyacente(int f1, int c1, int f2, int c2) {
        return (Math.abs(f1 - f2) + Math.abs(c1 - c2)) == 1;
    }

    private int contarVecinosMismoColor(int fila, int col) {
        if (grid[fila][col].getColor() == null) return 0;
        int tam = nivelConfig.getTamano();
        ColorFlujo colr = grid[fila][col].getColor();
        int count = 0;
        for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
            int nf = fila + d[0], nc = col + d[1];
            if (nf < 0 || nf >= tam || nc < 0 || nc >= tam) continue;
            if (grid[nf][nc].getColor() == colr) count++;
        }
        return count;
    }

    public void notificarTiempoAgotado() {
        estado = EstadoJuego.GAME_OVER;
        if (listener != null) {
            listener.onTiempoAgotado();
        }
    }

    public Celda[][] getGrid() {
        return grid;
    }

    public Nivel getNivelConfig() {
        return nivelConfig;
    }

    public int getMovimientos() {
        return movimientos;
    }

    public ColorFlujo getColorActivo() {
        return colorActivo;
    }

    public boolean esCeldaEnRutaActiva(int fila, int col) {
        for (int[] p : rutaActiva) {
            if (p[0] == fila && p[1] == col) return true;
        }
        return false;
    }

    public boolean rutaActivaVacia() {
        return rutaActiva.isEmpty();
    }

    public int getTotalCeldas() {
        return totalCeldas;
    }

    public int getCeldasRellenas() {
        if (grid == null) {
            return 0;
        }
        int tamano = nivelConfig.getTamano();
        int count = 0;
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (!grid[f][c].estaVacia()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getPorcentajeRelleno() {
        if (totalCeldas == 0) {
            return 0;
        }
        return getCeldasRellenas() * 100 / totalCeldas;
    }
}
