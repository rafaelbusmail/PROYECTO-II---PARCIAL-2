/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.hilos.HiloEstadisticas;
import com.flowfree.hilos.HiloTemporizador;
import com.flowfree.logica.FlowFreeJuego;
import com.flowfree.modelo.Celda;
import com.flowfree.modelo.HistorialPartida;
import com.flowfree.modelo.Nivel;
import com.flowfree.datos.Traductor;

public class PantallaJuego extends PantallaBase
        implements FlowFreeJuego.JuegoListener,
        HiloTemporizador.TimerListener {

    private FlowFreeJuego motor;
    private final int numeroNivel;
    private HiloTemporizador hiloTimer;
    private HiloEstadisticas hiloStats;
    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private int filaArrastre = -1, colArrastre = -1;
    private boolean arrastrando = false;
    private volatile int segundosRestantes = 0;
    private volatile long tiempoJugado = 0;
    private boolean victoria = false, gameOver = false;
    private boolean esPartidaReto = false;
    private String mensajeOverlay = "";

    private static final Color COLOR_HUD_BG = new Color(0.10f, 0.10f, 0.15f, 1f);
    private static final float HUD_H = 80f;
    private static final float PADDING = 12f;

    public PantallaJuego(FlowFreeGame juego, int numeroNivel) {
        super(juego);
        this.numeroNivel = numeroNivel;
    }

    @Override
    public void show() {
        fuente = crearFuente(16);
        fuenteGrande = crearFuente(28);
        fuenteSmall = crearFuente(13);
        layout = new GlyphLayout();
        motor = new FlowFreeJuego();
        motor.setListener(this);
        motor.cargarNivel(numeroNivel);
        motor.iniciar();
        iniciarHilos();
        esPartidaReto = (juego.retoDestinatario != null || juego.retoRemitente != null);
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    private void iniciarHilos() {
        Nivel cfg = motor.getNivelConfig();
        if (cfg.getTiempoLimite() > 0) {
            segundosRestantes = cfg.getTiempoLimite();
            hiloTimer = new HiloTemporizador(cfg.getTiempoLimite(), this);
            hiloTimer.start();
        }
        hiloStats = new HiloEstadisticas(juego.gestorUsuarios, seg -> tiempoJugado = seg);
        hiloStats.start();
        hiloStats.iniciarConteo();
    }

    @Override
    public void render(float delta) {
        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        int tam = motor.getNivelConfig().getTamano();
        float espacio = Math.min(W, H - HUD_H * 2) - PADDING * 2;
        float tamCelda = espacio / tam;
        float tableroX = cx - (tam * tamCelda) / 2f;
        float tableroY = HUD_H + PADDING;

        limpiarPantalla();

        if (victoria || gameOver) {
            manejarOverlayInput(tableroX, tableroY, tamCelda, tam);
        } else {
            manejarInput(W, H, tableroX, tableroY, tamCelda, tam);
        }

        dibujarHUD(W, H, cx, tam);
        dibujarTablero(W, H, tableroX, tableroY, tamCelda, tam);

        if (victoria || gameOver) {
            dibujarOverlay(W, H, cx);
        }
    }

    private void dibujarHUD(float W, float H, float cx, int tam) {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_HUD_BG);
        juego.shapeRenderer.rect(0, H - HUD_H, W, HUD_H);
        juego.shapeRenderer.rect(0, 0, W, HUD_H);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuente.setColor(COLOR_TEXTO_GRIS);
        fuente.draw(juego.batch,
                Traductor.nivel(juego.idiomaActual) + " " + numeroNivel + "  " + tam + "x" + tam,
                PADDING, H - 14f);

        Nivel cfg = motor.getNivelConfig();
        if (cfg.getTiempoLimite() > 0) {
            int min = segundosRestantes / 60, seg = segundosRestantes % 60;
            fuenteGrande.setColor(segundosRestantes <= 10 ? COLOR_ERROR : COLOR_TEXTO);
            tc(fuenteGrande, String.format("%02d:%02d", min, seg), cx, H - 12f);
        } else {
            fuenteGrande.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteGrande, String.format("%02d:%02d", tiempoJugado / 60, tiempoJugado % 60), cx, H - 12f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        String movTxt = Traductor.movimientos(juego.idiomaActual) + ": " + motor.getMovimientos();
        layout.setText(fuente, movTxt);
        fuente.draw(juego.batch, movTxt, W - layout.width - PADDING, H - 14f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch,
                Traductor.relleno(juego.idiomaActual) + ": " + motor.getPorcentajeRelleno() + "%",
                PADDING, HUD_H - 12f);
        fuenteSmall.setColor(COLOR_ACENTO);
        tc(fuenteSmall, Traductor.vidas(juego.idiomaActual) + ": " + motor.getVidas(), cx, HUD_H - 12f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        String hint = "[R] " + Traductor.reiniciar(juego.idiomaActual)
                + " [ESC] " + Traductor.menu(juego.idiomaActual);
        layout.setText(fuenteSmall, hint);
        fuenteSmall.draw(juego.batch, hint, W - layout.width - PADDING, HUD_H - 12f);
        juego.batch.end();
    }

    private void dibujarTablero(float W, float H,
            float tableroX, float tableroY,
            float tamCelda, int tam) {
        if (motor.getGrid() == null) {
            return;
        }

        float centro = tamCelda / 2f;
        float radioFijo = tamCelda * 0.40f;   
        float radioFlujo = tamCelda * 0.22f;  
        float anchoFranja = tamCelda * 0.36f; 

        Celda[][] grid = motor.getGrid();

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                juego.shapeRenderer.setColor(new Color(0.14f, 0.14f, 0.20f, 1f));
                juego.shapeRenderer.rect(
                        tableroX + c * tamCelda + 1,
                        tableroY + (tam - 1 - f) * tamCelda + 1,
                        tamCelda - 2, tamCelda - 2);
            }
        }

        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                Celda celda = grid[f][c];
                if (celda.getColor() == null) {
                    continue;
                }

                Color col = celda.getColor().getColorGDX();
                juego.shapeRenderer.setColor(col);

                float cx2 = tableroX + c * tamCelda + centro;
                float cy2 = tableroY + (tam - 1 - f) * tamCelda + centro;

                boolean rutaActivaColor = (motor.getColorActivo() == celda.getColor() && !motor.rutaActivaVacia());

                if (c < tam - 1 && grid[f][c + 1].getColor() == celda.getColor()) {
                    boolean skip = false;
                    if (rutaActivaColor) {
                        Celda v = grid[f][c + 1];
                        if ((celda.esPuntoFijo() && !motor.esCeldaEnRutaActiva(f, c)) ||
                            (v.esPuntoFijo() && !motor.esCeldaEnRutaActiva(f, c + 1))) {
                            skip = true;
                        }
                    }
                    if (!skip) {
                        float cx2r = tableroX + (c + 1) * tamCelda + centro;
                        juego.shapeRenderer.rect(cx2, cy2 - anchoFranja / 2f, cx2r - cx2, anchoFranja);
                    }
                }

                if (f < tam - 1 && grid[f + 1][c].getColor() == celda.getColor()) {
                    boolean skip = false;
                    if (rutaActivaColor) {
                        Celda v = grid[f + 1][c];
                        if ((celda.esPuntoFijo() && !motor.esCeldaEnRutaActiva(f, c)) ||
                            (v.esPuntoFijo() && !motor.esCeldaEnRutaActiva(f + 1, c))) {
                            skip = true;
                        }
                    }
                    if (!skip) {
                        float cy2d = tableroY + (tam - 1 - (f + 1)) * tamCelda + centro;
                        juego.shapeRenderer.rect(cx2 - anchoFranja / 2f, cy2d, anchoFranja, cy2 - cy2d);
                    }
                }
            }
        }

        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                Celda celda = grid[f][c];
                if (!celda.esPuntoFijo() || celda.getColor() == null) {
                    continue;
                }

                Color col = celda.getColor().getColorGDX();
                float cx2 = tableroX + c * tamCelda + centro;
                float cy2 = tableroY + (tam - 1 - f) * tamCelda + centro;

                juego.shapeRenderer.setColor(celda.getColor().getColorOscuro());
                juego.shapeRenderer.circle(cx2, cy2, radioFijo + 3f);
                juego.shapeRenderer.setColor(col);
                juego.shapeRenderer.circle(cx2, cy2, radioFijo);
            }
        }

        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                Celda celda = grid[f][c];
                if (celda.esPuntoFijo() || celda.getColor() == null) {
                    continue;
                }

                Color col = celda.getColor().getColorGDX();
                float cx2 = tableroX + c * tamCelda + centro;
                float cy2 = tableroY + (tam - 1 - f) * tamCelda + centro;

                juego.shapeRenderer.setColor(col);
                juego.shapeRenderer.circle(cx2, cy2, radioFlujo);
            }
        }

        juego.shapeRenderer.setColor(new Color(0.22f, 0.22f, 0.30f, 1f));
        for (int i = 0; i <= tam; i++) {
            float lx = tableroX + i * tamCelda;
            float ly = tableroY + i * tamCelda;
            juego.shapeRenderer.rect(lx, tableroY, 1f, tam * tamCelda);
            juego.shapeRenderer.rect(tableroX, ly, tam * tamCelda, 1f);
        }

        juego.shapeRenderer.end();
    }

    private void dibujarOverlay(float W, float H, float cx) {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.65f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.end();
        juego.batch.begin();
        fuenteGrande.setColor(victoria ? COLOR_EXITO : COLOR_ERROR);
        tc(fuenteGrande, mensajeOverlay, cx, H / 2f + 60f);
        fuente.setColor(COLOR_TEXTO_GRIS);
        tc(fuente, "[ENTER] " + Traductor.siguiente(juego.idiomaActual)
                + "   [R] " + Traductor.reiniciar(juego.idiomaActual)
                + "   [ESC] " + Traductor.menu(juego.idiomaActual),
                cx, H / 2f - 20f);
        juego.batch.end();
    }

    private void manejarOverlayInput(float tableroX, float tableroY, float tamCelda, int tam) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            detenerHilos();
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            detenerHilos();
            motor.reiniciar();
            iniciarHilos();
            victoria = false;
            gameOver = false;
            mensajeOverlay = "";
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            detenerHilos();
            if (victoria && numeroNivel < 5 && !esPartidaReto) {
                juego.setScreen(new PantallaJuego(juego, numeroNivel + 1));
            } else {
                juego.setScreen(new PantallaMapa(juego));
            }
        }
    }

    private void manejarInput(float W, float H, float tableroX, float tableroY,
            float tamCelda, int tam) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            detenerHilos();
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            detenerHilos();
            motor.reiniciar();
            iniciarHilos();
            victoria = false;
            gameOver = false;
            mensajeOverlay = "";
            return;
        }
        manejarTouch(H, tableroX, tableroY, tamCelda, tam);
    }

    private void manejarTouch(float H, float tableroX, float tableroY,
            float tamCelda, int tam) {
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            int[] cel = coordsACelda(mx, my, tableroX, tableroY, tamCelda, tam);
            if (cel != null) {
                filaArrastre = cel[0];
                colArrastre = cel[1];
                motor.iniciarRuta(filaArrastre, colArrastre);
                arrastrando = true;
            }
        }
        if (arrastrando && Gdx.input.isTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            int[] cel = coordsACelda(mx, my, tableroX, tableroY, tamCelda, tam);
            if (cel != null && (cel[0] != filaArrastre || cel[1] != colArrastre)) {
                if (motor.continuarRuta(cel[0], cel[1])) {
                    filaArrastre = cel[0];
                    colArrastre = cel[1];
                }
            }
        }
        if (arrastrando && !Gdx.input.isTouched()) {
            motor.terminarRuta();
            arrastrando = false;
            filaArrastre = -1;
            colArrastre = -1;
        }
    }

    private int[] coordsACelda(float mx, float my, float tableroX, float tableroY,
            float tamCelda, int tam) {
        if (mx < tableroX || my < tableroY) {
            return null;
        }
        int c = (int) ((mx - tableroX) / tamCelda);
        int f = tam - 1 - (int) ((my - tableroY) / tamCelda);
        if (f < 0 || f >= tam || c < 0 || c >= tam) {
            return null;
        }
        return new int[]{f, c};
    }

    @Override
    public void onVictoria(int puntaje, long tiempo, int movs) {
        victoria = true;
        detenerHilos();

        int puntosFinales = puntaje;
        String resultado = "VICTORIA";
        String extraMsg = "";

        if (juego.retoDestinatario != null && juego.retoNivel == numeroNivel) {
            puntosFinales = (int)(puntaje * 0.15f);
            juego.gestorUsuarios.enviarReto(juego.retoDestinatario, numeroNivel, tiempo, puntaje);
            extraMsg = "  [" + Traductor.retoEnviadoA(juego.idiomaActual) + " " + juego.retoDestinatario + "]";
            juego.retoDestinatario = null;
            juego.retoNivel = -1;
        }

        if (juego.retoRemitente != null && juego.retoNivel == numeroNivel) {
            boolean gano = tiempo < juego.retoTiempoRemitente;
            boolean empate = tiempo == juego.retoTiempoRemitente;
            if (gano) {
                puntosFinales = (int)(puntaje * 0.15f);
            } else {
                puntosFinales = 0;
                if (!empate) resultado = "RETO_PERDIDO";
            }
            juego.gestorUsuarios.aceptarReto(juego.retoRemitente, numeroNivel, tiempo, puntaje);
            String ganadorNombre;
            if (gano) {
                ganadorNombre = juego.gestorUsuarios.getUsuarioActual().getUsername();
            } else if (empate) {
                ganadorNombre = Traductor.empate(juego.idiomaActual);
            } else {
                ganadorNombre = juego.retoRemitente;
            }
            extraMsg = "  [" + Traductor.retoCompletado(juego.idiomaActual)
                    + " - " + Traductor.ganador(juego.idiomaActual) + ": " + ganadorNombre + "]";
            juego.retoRemitente = null;
            juego.retoNivel = -1;
            juego.retoTiempoRemitente = 0;
            juego.retoPuntajeRemitente = 0;
        }

        String ptsText = puntosFinales > 0 ? "  +" + puntosFinales + " pts" : "";
        mensajeOverlay = "!" + Traductor.nivelCompletado(juego.idiomaActual) + "!" + ptsText + extraMsg;

        HistorialPartida h = new HistorialPartida(numeroNivel, puntosFinales, tiempo, movs, true, resultado);
        juego.gestorUsuarios.registrarPartida(
                juego.gestorUsuarios.getUsuarioActual().getUsername(), h);
    }

    @Override
    public void onTiempoAgotado() {
        gameOver = true;
        mensajeOverlay = "!" + Traductor.tiempoAgotado(juego.idiomaActual) + "!";
        motor.notificarTiempoAgotado();
        HistorialPartida h = new HistorialPartida(numeroNivel, 0,
                motor.getNivelConfig().getTiempoLimite(), motor.getMovimientos(), false, "TIEMPO_AGOTADO");
        juego.gestorUsuarios.registrarPartida(
                juego.gestorUsuarios.getUsuarioActual().getUsername(), h);
        juego.retoDestinatario = null;
        juego.retoRemitente = null;
        juego.retoNivel = -1;
    }

    @Override
    public void onTick(int segundos) {
        segundosRestantes = segundos;
    }

    private void detenerHilos() {
        if (hiloTimer != null) {
            hiloTimer.detener();
            hiloTimer = null;
        }
        if (hiloStats != null) {
            hiloStats.detener();
            hiloStats = null;
        }
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    @Override
    public void dispose() {
        detenerHilos();
        if (fuente != null) {
            fuente.dispose();
        }
        if (fuenteGrande != null) {
            fuenteGrande.dispose();
        }
        if (fuenteSmall != null) {
            fuenteSmall.dispose();
        }
    }
}
