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
import com.flowfree.modelo.Usuario;
import com.flowfree.datos.Traductor;
import java.util.List;

public class PantallaRanking extends PantallaBase {

    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private static final Color ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color PLATA = new Color(0.80f, 0.80f, 0.80f, 1f);
    private static final Color BRONCE = new Color(0.80f, 0.50f, 0.20f, 1f);

    private int frameDelay = 5;

    public PantallaRanking(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();
        frameDelay = 5;
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

    @Override
    public void render(float delta) {
        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        limpiarPantalla();

        if (frameDelay > 0) {
            frameDelay--;
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                juego.setScreen(new PantallaMenu(juego));
                return;
            }
            if (Gdx.input.justTouched()) {
                float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
                if (mx >= 20f && mx <= 160f && my >= 20f && my <= 60f) {
                    juego.setScreen(new PantallaMenu(juego));
                    return;
                }
            }
        }

        List<Usuario> ranking = juego.gestorUsuarios.obtenerRanking();
        float filaH = 50f;
        int maxRows = Math.min(ranking.size(), 10);
        float headerH = 60f;
        float panelH = Math.max(200f, headerH + maxRows * filaH + 40f);
        float panelW = Math.min(580f, W - 40f);
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f - 20f;
        float filaY0 = panelY + panelH - headerH - filaH;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(ORO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);
        juego.shapeRenderer.setColor(new Color(0.10f, 0.10f, 0.16f, 1f));
        juego.shapeRenderer.rect(panelX + 10, filaY0 + filaH, panelW - 20, headerH - 16f);

        for (int i = 0; i < maxRows; i++) {
            float fy = filaY0 - i * filaH;
            if (i % 2 == 0) {
                juego.shapeRenderer.setColor(new Color(0.11f, 0.11f, 0.17f, 1f));
                juego.shapeRenderer.rect(panelX + 10, fy, panelW - 20, filaH);
            }
            Color bar = i == 0 ? ORO : i == 1 ? PLATA : i == 2 ? BRONCE : COLOR_BORDE;
            juego.shapeRenderer.setColor(bar);
            juego.shapeRenderer.rect(panelX + 10, fy, 4f, filaH);
        }
        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(20f, 20f, 140f, 40f);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.rankingGlobal(juego.idiomaActual), cx, H - 42f);

        float colPos = panelX + 55f, colUser = panelX + 120f;
        float colNivel = panelX + panelW - 230f, colPts = panelX + panelW - 110f;
        float cabY = filaY0 + filaH + headerH - 28f;
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch, "#", colPos, cabY);
        fuenteSmall.draw(juego.batch, Traductor.jugador(juego.idiomaActual), colUser, cabY);
        fuenteSmall.draw(juego.batch, Traductor.nivelAbr(juego.idiomaActual), colNivel, cabY);
        fuenteSmall.draw(juego.batch, Traductor.puntosRank(juego.idiomaActual), colPts, cabY);

        if (ranking.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.sinJugadores(juego.idiomaActual), cx, H / 2f);
        } else {
            for (int i = 0; i < maxRows; i++) {
                Usuario u = ranking.get(i);
                float fy = filaY0 - i * filaH + filaH / 2f + 7f;
                Color pc = i == 0 ? ORO : i == 1 ? PLATA : i == 2 ? BRONCE : COLOR_TEXTO_GRIS;
                fuente.setColor(pc);
                fuente.draw(juego.batch, String.valueOf(i + 1), colPos, fy);
                Usuario yo = juego.gestorUsuarios.getUsuarioActual();
                boolean esYo = yo != null && yo.getUsername().equals(u.getUsername());
                fuente.setColor(esYo ? COLOR_ACENTO : COLOR_TEXTO);
                fuente.draw(juego.batch, u.getUsername() + (esYo ? " <-" : ""), colUser, fy);
                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                fuenteSmall.draw(juego.batch, "Nv." + u.getNivelMaxDesbloqueado(), colNivel, fy);
                String ptsTxt = u.getEstadisticas().getPuntajeTotal() + " pts";
                fuente.setColor(i == 0 ? ORO : COLOR_TEXTO);
                fuente.draw(juego.batch, ptsTxt, colPts, fy);
            }
        }

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< " + Traductor.volver(juego.idiomaActual), 35f, 47f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual) + " al menu", cx, 28f);
        juego.batch.end();
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    @Override
    public void dispose() {
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
