/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.modelo;

import com.flowfree.enums.ColorFlujo;

public class Nivel {

    private final int numero;
    private final int tamano;          
    private final int tiempoLimite;    
    private final ColorFlujo[] colores;
    private final int[][] puntosColor;

    private Nivel(Builder b) {
        this.numero = b.numero;
        this.tamano = b.tamano;
        this.tiempoLimite = b.tiempoLimite;
        this.colores = b.colores;
        this.puntosColor = b.puntosColor;
    }

    public Celda[][] construirGrid() {
        Celda[][] grid = new Celda[tamano][tamano];
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                grid[f][c] = new Celda(f, c);
            }
        }

        for (int i = 0; i < colores.length; i++) {
            int fA = puntosColor[i][0], cA = puntosColor[i][1];
            int fB = puntosColor[i][2], cB = puntosColor[i][3];
            grid[fA][cA].setPuntoFijo(colores[i]);
            grid[fB][cB].setPuntoFijo(colores[i]);
        }
        return grid;
    }

    public int getNumero() {
        return numero;
    }

    public int getTamano() {
        return tamano;
    }

    public int getTiempoLimite() {
        return tiempoLimite;
    }

    public int getNumColores() {
        return colores.length;
    }

    public ColorFlujo getColor(int i) {
        return colores[i];
    }

    public static class Builder {

        private int numero, tamano, tiempoLimite;
        private ColorFlujo[] colores;
        private int[][] puntosColor;

        public Builder numero(int n) {
            this.numero = n;
            return this;
        }

        public Builder tamano(int t) {
            this.tamano = t;
            return this;
        }

        public Builder tiempoLimite(int s) {
            this.tiempoLimite = s;
            return this;
        }

        public Builder colores(ColorFlujo[] c) {
            this.colores = c;
            return this;
        }

        public Builder puntos(int[][] p) {
            this.puntosColor = p;
            return this;
        }

        public Nivel build() {
            return new Nivel(this);
        }
    }

    public static Nivel getNivel(int numero) {
        switch (numero) {
            case 1:
                return nivel1();
            case 2:
                return nivel2();
            case 3:
                return nivel3();
            case 4:
                return nivel4();
            case 5:
                return nivel5();
            default:
                return nivel1();
        }
    }

    private static Nivel nivel1() {
        return new Builder()
                .numero(1).tamano(5).tiempoLimite(0)
                .colores(new ColorFlujo[]{
            ColorFlujo.AMARILLO,
            ColorFlujo.ROJO,
            ColorFlujo.NARANJA,
            ColorFlujo.AZUL,
            ColorFlujo.VERDE
        })
                .puntos(new int[][]{
            {0, 4, 3, 3},  
            {0, 0, 4, 1},  
            {1, 4, 4, 3},  
            {1, 2, 4, 2}, 
            {0, 2, 3, 1}   
        })
                .build();
    }

    private static Nivel nivel2() {
        return new Builder()
                .numero(2).tamano(6).tiempoLimite(120)
                .colores(new ColorFlujo[]{
            ColorFlujo.AZUL,
            ColorFlujo.AMARILLO,
            ColorFlujo.CYAN,
            ColorFlujo.NARANJA,
            ColorFlujo.ROJO,
            ColorFlujo.VERDE
        })
                .puntos(new int[][]{
            {0, 5, 5, 2},  
            {0, 1, 5, 0},  
            {0, 2, 2, 2},  
            {1, 4, 4, 2}, 
            {0, 4, 3, 2},  
            {0, 0, 4, 0}  
        })
                .build();
    }

    private static Nivel nivel3() {
        return new Builder()
                .numero(3).tamano(7).tiempoLimite(100)
                .colores(new ColorFlujo[]{
            ColorFlujo.ROSADO,
            ColorFlujo.CYAN,
            ColorFlujo.ROJO,
            ColorFlujo.AZUL,
            ColorFlujo.NARANJA,
            ColorFlujo.AMARILLO,
            ColorFlujo.VERDE
        })
                .puntos(new int[][]{
            {1, 2, 2, 4},  
            {1, 5, 3, 4}, 
            {4, 1, 5, 5}, 
            {0, 5, 3, 6}, 
            {0, 0, 4, 0}, 
            {1, 0, 4, 2}, 
            {0, 2, 0, 4}  
        })
                .build();
    }

    private static Nivel nivel4() {
        return new Builder()
                .numero(4).tamano(8).tiempoLimite(90)
                .colores(new ColorFlujo[]{
            ColorFlujo.ROJO,
            ColorFlujo.CYAN,
            ColorFlujo.NARANJA,
            ColorFlujo.VERDE,
            ColorFlujo.AZUL,
            ColorFlujo.AMARILLO
        })
                .puntos(new int[][]{
            {0, 4, 7, 1}, 
            {1, 4, 2, 7}, 
            {3, 3, 4, 4},  
            {1, 6, 2, 4},  
            {3, 4, 5, 4},  
            {2, 5, 1, 7}  
        })
                .build();
    }

    private static Nivel nivel5() {
        return new Builder()
                .numero(5).tamano(9).tiempoLimite(75)
                .colores(new ColorFlujo[]{
            ColorFlujo.CYAN,
            ColorFlujo.MORADO,
            ColorFlujo.ROJO_OSCURO,
            ColorFlujo.AMARILLO,
            ColorFlujo.VERDE,
            ColorFlujo.AZUL,
            ColorFlujo.ROSADO,
            ColorFlujo.NARANJA,
            ColorFlujo.ROJO
        })
                .puntos(new int[][]{
            {1, 2, 2, 3},  
            {5, 8, 6, 1}, 
            {5, 7, 7, 1},  
            {5, 0, 6, 2},  
            {4, 1, 4, 3}, 
            {1, 3, 2, 7}, 
            {3, 7, 5, 1}, 
            {2, 4, 2, 6}, 
            {1, 1, 4, 4}   
        })
                .build();
    }
}
