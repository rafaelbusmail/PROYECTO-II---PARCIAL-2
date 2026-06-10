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
import java.util.List;

public class PantallaRanking extends PantallaBase {

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private BitmapFont fuenteSmall;
    private GlyphLayout layout;

    private float anchoVentana, altoVentana, xCentro;

    private static final Color COLOR_ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color COLOR_PLATA = new Color(0.80f, 0.80f, 0.80f, 1f);
    private static final Color COLOR_BRONCE = new Color(0.80f, 0.50f, 0.20f, 1f);

    public PantallaRanking(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();
        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        xCentro = anchoVentana / 2f;
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = altoVentana - Gdx.input.getY();
            if (mx >= 20f && mx <= 160f && my >= 20f && my <= 60f) {
                juego.setScreen(new PantallaMenu(juego));
                return;
            }
        }

        List<Usuario> ranking = juego.gestorUsuarios.obtenerRanking();

        float filaH = 50f;
        int maxRows = Math.min(ranking.size(), 10);
        float headerH = 60f;
        float panelH = headerH + maxRows * filaH + 40f;
        if (panelH < 200f) {
            panelH = 200f;
        }
        float panelW = Math.min(580f, anchoVentana - 40f);
        float panelX = xCentro - panelW / 2f;
        float panelY = altoVentana / 2f - panelH / 2f - 20f;
        float filaY0 = panelY + panelH - headerH - filaH;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);

        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);

        juego.shapeRenderer.setColor(COLOR_ORO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        juego.shapeRenderer.setColor(new Color(0.10f, 0.10f, 0.16f, 1f));
        juego.shapeRenderer.rect(panelX + 10, filaY0 + filaH, panelW - 20, headerH - 16f);

        for (int i = 0; i < maxRows; i++) {
            float fy = filaY0 - i * filaH;
            if (i % 2 == 0) {
                juego.shapeRenderer.setColor(new Color(0.11f, 0.11f, 0.17f, 1f));
                juego.shapeRenderer.rect(panelX + 10, fy, panelW - 20, filaH);
            }
            Color barColor = i == 0 ? COLOR_ORO : i == 1 ? COLOR_PLATA : i == 2 ? COLOR_BRONCE : COLOR_BORDE;
            juego.shapeRenderer.setColor(barColor);
            juego.shapeRenderer.rect(panelX + 10, fy, 4f, filaH);
        }

        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(20f, 20f, 140f, 40f);

        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_TEXTO);
        dibujarTC(fuenteGrande, "RANKING GLOBAL", xCentro, altoVentana - 42f);

        float colPos = panelX + 55f;
        float colUser = panelX + 120f;
        float colNivel = panelX + panelW - 200f;
        float colPts = panelX + panelW - 80f;
        float cabY = filaY0 + filaH + headerH - 28f;
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch, "#", colPos, cabY);
        fuenteSmall.draw(juego.batch, "JUGADOR", colUser, cabY);
        fuenteSmall.draw(juego.batch, "NIVEL", colNivel, cabY);
        fuenteSmall.draw(juego.batch, "PUNTOS", colPts, cabY);

        if (ranking.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            dibujarTC(fuente, "Sin jugadores registrados aún", xCentro, altoVentana / 2f);
        } else {
            for (int i = 0; i < maxRows; i++) {
                Usuario u = ranking.get(i);
                float fy = filaY0 - i * filaH + filaH / 2f + 7f;

                Color posColor = i == 0 ? COLOR_ORO : i == 1 ? COLOR_PLATA
                        : i == 2 ? COLOR_BRONCE : COLOR_TEXTO_GRIS;
                fuente.setColor(posColor);
                fuente.draw(juego.batch, String.valueOf(i + 1), colPos, fy);

                Usuario actual = juego.gestorUsuarios.getUsuarioActual();
                boolean esActual = actual != null
                        && actual.getUsername().equals(u.getUsername());
                fuente.setColor(esActual ? COLOR_ACENTO : COLOR_TEXTO);
                fuente.draw(juego.batch, u.getUsername()
                        + (esActual ? " ←" : ""), colUser, fy);

                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                fuenteSmall.draw(juego.batch,
                        "Nv." + u.getNivelMaxDesbloqueado(), colNivel, fy);

                fuente.setColor(i == 0 ? COLOR_ORO : COLOR_TEXTO);
                fuente.draw(juego.batch,
                        u.getEstadisticas().getPuntajeTotal() + " pts", colPts, fy);
            }
        }

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< VOLVER", 35f, 47f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        dibujarTC(fuenteSmall, "[ESC] Volver al menú", xCentro, 28f);

        juego.batch.end();
    }

    private void dibujarTC(BitmapFont font, String texto, float cx, float y) {
        layout.setText(font, texto);
        font.draw(juego.batch, texto, cx - layout.width / 2f, y);
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
