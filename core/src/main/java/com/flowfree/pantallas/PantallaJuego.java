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
import com.flowfree.datos.Traductor;
import com.flowfree.enums.ColorFlujo;
import com.flowfree.enums.EstadoJuego;
import com.flowfree.hilos.HiloTemporizador;
import com.flowfree.hilos.HiloEstadisticas;
import com.flowfree.logica.FlowFreeJuego;
import com.flowfree.modelo.Celda;
import com.flowfree.modelo.HistorialPartida;
import com.flowfree.modelo.Nivel;

public class PantallaJuego extends PantallaBase
        implements FlowFreeJuego.JuegoListener,
        HiloTemporizador.TimerListener {

    private FlowFreeJuego motor;
    private final int numeroNivel;

    private HiloTemporizador hiloTimer;
    private HiloEstadisticas hiloStats;

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private BitmapFont fuenteSmall;
    private GlyphLayout layout;

    private float anchoVentana, altoVentana, xCentro;
    private float tamCelda;
    private float tableroX, tableroY;

    private int filaArrastre = -1;
    private int colArrastre = -1;
    private boolean arrastrando = false;

    private volatile int segundosRestantes = 0;
    private volatile long tiempoJugado = 0;
    private boolean victoria = false;
    private boolean gameOver = false;
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

        recalcularLayout();
        iniciarHilos();
    }

    @Override
    public void resize(int w, int h) {
        recalcularLayout();
    }

    private void recalcularLayout() {
        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        xCentro = anchoVentana / 2f;

        int tam = motor.getNivelConfig().getTamano();
        float espacio = Math.min(anchoVentana, altoVentana - HUD_H * 2) - PADDING * 2;
        tamCelda = espacio / tam;
        tableroX = xCentro - (tam * tamCelda) / 2f;
        tableroY = HUD_H + PADDING;
    }

    private void iniciarHilos() {
        Nivel cfg = motor.getNivelConfig();

        if (cfg.getTiempoLimite() > 0) {
            segundosRestantes = cfg.getTiempoLimite();
            hiloTimer = new HiloTemporizador(cfg.getTiempoLimite(), this);
            hiloTimer.start();
        }

        hiloStats = new HiloEstadisticas(juego.gestorUsuarios,
                seg -> tiempoJugado = seg);
        hiloStats.start();
        hiloStats.iniciarConteo();
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();

        if (victoria || gameOver) {
            manejarOverlayInput();
        } else {
            manejarInput();
        }

        dibujarHUD();
        dibujarTablero();

        if (victoria || gameOver) {
            dibujarOverlay();
        }
    }

    private void dibujarHUD() {
        int tam = motor.getNivelConfig().getTamano();

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(COLOR_HUD_BG);
        juego.shapeRenderer.rect(0, altoVentana - HUD_H, anchoVentana, HUD_H);

        juego.shapeRenderer.setColor(COLOR_HUD_BG);
        juego.shapeRenderer.rect(0, 0, anchoVentana, HUD_H);

        juego.shapeRenderer.end();

        juego.batch.begin();

        fuente.setColor(COLOR_TEXTO_GRIS);
        fuente.draw(juego.batch, Traductor.nivel(juego.idiomaActual) + " " + numeroNivel + "  " + tam + "x" + tam,
                PADDING, altoVentana - 14f);

        Nivel cfg = motor.getNivelConfig();
        if (cfg.getTiempoLimite() > 0) {
            int min = segundosRestantes / 60;
            int seg = segundosRestantes % 60;
            String timerTxt = String.format("%02d:%02d", min, seg);
            Color cTimer = segundosRestantes <= 10 ? COLOR_ERROR : COLOR_TEXTO;
            fuenteGrande.setColor(cTimer);
            dibujarTextoCentrado(fuenteGrande, timerTxt,
                    xCentro, altoVentana - 12f);
        } else {
            fuenteGrande.setColor(COLOR_TEXTO_GRIS);
            dibujarTextoCentrado(fuenteGrande, String.format("%02d:%02d",
                    tiempoJugado / 60, tiempoJugado % 60),
                    xCentro, altoVentana - 12f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        String movTxt = Traductor.movimientos(juego.idiomaActual) + ": " + motor.getMovimientos();
        layout.setText(fuente, movTxt);
        fuente.draw(juego.batch, movTxt,
                anchoVentana - layout.width - PADDING,
                altoVentana - 14f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch,
                Traductor.relleno(juego.idiomaActual) + ": " + motor.getPorcentajeRelleno() + "%",
                PADDING, HUD_H - 12f);

        fuenteSmall.setColor(COLOR_ACENTO);
        dibujarTextoCentrado(fuenteSmall,
                Traductor.vidas(juego.idiomaActual) + ": " + motor.getVidas(),
                xCentro, HUD_H - 12f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        String hint = "[R] " + Traductor.reiniciar(juego.idiomaActual) + "   [ESC] " + Traductor.menu(juego.idiomaActual);
        layout.setText(fuenteSmall, hint);
        fuenteSmall.draw(juego.batch, hint,
                anchoVentana - layout.width - PADDING, HUD_H - 12f);

        juego.batch.end();
    }

    private void dibujarTablero() {
        if (motor.getGrid() == null) {
            return;
        }

        int tam = motor.getNivelConfig().getTamano();
        float radio = tamCelda * 0.38f;
        float center = tamCelda / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                float cx = tableroX + c * tamCelda + center;
                float cy = tableroY + (tam - 1 - f) * tamCelda + center;
                Celda celda = motor.getGrid()[f][c];

                juego.shapeRenderer.setColor(
                        new Color(0.14f, 0.14f, 0.20f, 1f));
                juego.shapeRenderer.rect(
                        tableroX + c * tamCelda + 1,
                        tableroY + (tam - 1 - f) * tamCelda + 1,
                        tamCelda - 2, tamCelda - 2);

                if (celda.getColor() == null) {
                    continue;
                }

                Color col = celda.getColor().getColorGDX();
                float pw = tamCelda * 0.38f;

                if (c < tam - 1) {
                    Celda der = motor.getGrid()[f][c + 1];
                    if (der.getColor() == celda.getColor()) {
                        juego.shapeRenderer.rect(cx + radio, cy - pw / 2f, tamCelda - 2 * radio, pw);
                    }
                }
                if (f > 0) {
                    Celda arr = motor.getGrid()[f - 1][c];
                    if (arr.getColor() == celda.getColor()) {
                        juego.shapeRenderer.rect(cx - pw / 2f, cy + radio, pw, tamCelda - 2 * radio);
                    }
                }

                if (celda.esPuntoFijo()) {
                    juego.shapeRenderer.setColor(celda.getColor().getColorOscuro());
                    juego.shapeRenderer.circle(cx, cy, radio + 4f);
                    juego.shapeRenderer.setColor(col);
                    juego.shapeRenderer.circle(cx, cy, radio);
                } else {
                    juego.shapeRenderer.setColor(col);
                    juego.shapeRenderer.rect(cx - pw / 2f, cy - pw / 2f, pw, pw);
                }
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

    private void dibujarOverlay() {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.65f);
        juego.shapeRenderer.rect(0, 0, anchoVentana, altoVentana);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(victoria ? COLOR_EXITO : COLOR_ERROR);
        dibujarTextoCentrado(fuenteGrande, mensajeOverlay,
                xCentro, altoVentana / 2f + 60f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente,
                "[ENTER] " + Traductor.siguiente(juego.idiomaActual) + "   [R] " + Traductor.reiniciar(juego.idiomaActual) + "   [ESC] " + Traductor.menu(juego.idiomaActual),
                xCentro, altoVentana / 2f - 20f);
        juego.batch.end();
    }

    private void manejarOverlayInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            detenerHilos();
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            motor.reiniciar();
            if (hiloTimer != null) {
                hiloTimer.reiniciar();
            }
            victoria = false;
            gameOver = false;
            mensajeOverlay = "";
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            detenerHilos();
            if (victoria && numeroNivel < 5) {
                juego.setScreen(new PantallaJuego(juego, numeroNivel + 1));
            } else {
                juego.setScreen(new PantallaMapa(juego));
            }
        }
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            detenerHilos();
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            motor.reiniciar();
            if (hiloTimer != null) {
                hiloTimer.reiniciar();
            }
            victoria = false;
            gameOver = false;
            mensajeOverlay = "";
            return;
        }
        manejarTouch();
    }

    private void manejarTouch() {
        int tam = motor.getNivelConfig().getTamano();

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = altoVentana - Gdx.input.getY();
            int[] cel = coordsACelda(mx, my, tam);
            if (cel != null) {
                filaArrastre = cel[0];
                colArrastre = cel[1];
                motor.iniciarRuta(filaArrastre, colArrastre);
                arrastrando = true;
            }
        }

        if (arrastrando && Gdx.input.isTouched()) {
            float mx = Gdx.input.getX();
            float my = altoVentana - Gdx.input.getY();
            int[] cel = coordsACelda(mx, my, tam);
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

    private int[] coordsACelda(float mx, float my, int tam) {
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
        mensajeOverlay = "¡" + Traductor.nivelCompletado(juego.idiomaActual) + "!  +" + puntaje + " pts";
        detenerHilos();

        HistorialPartida h = new HistorialPartida(
                numeroNivel, puntaje, tiempo, movs, true, "VICTORIA");
        juego.gestorUsuarios.registrarPartida(
                juego.gestorUsuarios.getUsuarioActual().getUsername(), h);

        if (juego.retoDestinatario != null && juego.retoNivel == numeroNivel) {
            juego.gestorUsuarios.enviarReto(juego.retoDestinatario, numeroNivel, tiempo, puntaje);
            mensajeOverlay += "  [" + Traductor.retoEnviadoA(juego.idiomaActual) + " " + juego.retoDestinatario + "]";
            juego.retoDestinatario = null;
            juego.retoNivel = -1;
        }
    }

    @Override
    public void onTiempoAgotado() {
        gameOver = true;
        mensajeOverlay = "¡" + Traductor.tiempoAgotado(juego.idiomaActual) + "!";
        motor.notificarTiempoAgotado();
        HistorialPartida h = new HistorialPartida(
                numeroNivel, 0, motor.getNivelConfig().getTiempoLimite(),
                motor.getMovimientos(), false, "TIEMPO_AGOTADO");
        juego.gestorUsuarios.registrarPartida(
                juego.gestorUsuarios.getUsuarioActual().getUsername(), h);
        juego.retoDestinatario = null;
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

    private void dibujarTextoCentrado(BitmapFont font, String txt,
            float cx, float y) {
        layout.setText(font, txt);
        font.draw(juego.batch, txt, cx - layout.width / 2f, y);
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
