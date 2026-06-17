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

public class PantallaMapa extends PantallaBase {

    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private static final int TOTAL = 5;
    private static final Color[] COLORES = {
        new Color(0.20f, 0.80f, 0.60f, 1f), new Color(0.25f, 0.55f, 0.90f, 1f),
        new Color(0.90f, 0.65f, 0.10f, 1f), new Color(0.90f, 0.25f, 0.25f, 1f),
        new Color(0.55f, 0.10f, 0.80f, 1f)
    };
    private static final String[] NOMBRES = {
        "Nivel 1\n5x5", "Nivel 2\n6x6", "Nivel 3\n7x7", "Nivel 4\n8x8", "Nivel 5\n9x9"
    };

    public PantallaMapa(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();
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

        float radio = 45f, espX = 130f;
        float startX = cx - (TOTAL - 1) * espX / 2f;
        float nodoY = H / 2f;

        limpiarPantalla();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            if (dentroDeRect(mx, my, 20f, 20f, 120f, 40f)) {
                juego.setScreen(new PantallaMenu(juego));
                return;
            }
            Usuario u = juego.gestorUsuarios.getUsuarioActual();
            int nivelMax = u != null ? u.getNivelMaxDesbloqueado() : 1;
            for (int i = 0; i < TOTAL; i++) {
                float nx = startX + i * espX;
                if (dentroDeCirculo(mx, my, nx + radio, nodoY + radio, radio)) {
                    if ((i + 1) <= nivelMax) {
                        juego.setScreen(new PantallaJuego(juego, i + 1));
                    }
                    return;
                }
            }
        }

        float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
        Usuario usuario = juego.gestorUsuarios.getUsuarioActual();
        int nivelMax = usuario != null ? usuario.getNivelMaxDesbloqueado() : 1;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(0, H - 4f, W, 4f);

        for (int i = 0; i < TOTAL - 1; i++) {
            float x1 = startX + i * espX + radio;
            float x2 = startX + (i + 1) * espX - radio;
            juego.shapeRenderer.setColor((i + 2) <= nivelMax
                    ? new Color(0.35f, 0.35f, 0.45f, 1f) : new Color(0.20f, 0.20f, 0.28f, 1f));
            juego.shapeRenderer.rect(x1, nodoY - 3f, x2 - x1, 6f);
        }

        for (int i = 0; i < TOTAL; i++) {
            float nx = startX + i * espX;
            boolean desbloqueado = (i + 1) <= nivelMax;
            boolean hover = dentroDeCirculo(mx, my, nx + radio, nodoY + radio, radio);
            Color cn = desbloqueado ? COLORES[i] : new Color(0.20f, 0.20f, 0.28f, 1f);
            if (hover && desbloqueado) {
                cn = new Color(Math.min(cn.r + 0.15f, 1f), Math.min(cn.g + 0.15f, 1f), Math.min(cn.b + 0.15f, 1f), 1f);
            }
            juego.shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.3f));
            juego.shapeRenderer.circle(nx + radio + 3f, nodoY + radio - 3f, radio);
            juego.shapeRenderer.setColor(cn);
            juego.shapeRenderer.circle(nx + radio, nodoY + radio, radio);
            if (!desbloqueado) {
                juego.shapeRenderer.setColor(new Color(0.40f, 0.40f, 0.50f, 1f));
                juego.shapeRenderer.rect(nx + radio - 10f, nodoY + radio - 8f, 20f, 16f);
            }
        }

        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(20f, 20f, 120f, 40f);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, "SELECCIONAR NIVEL", cx, H - 50f);

        if (usuario != null) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, usuario.getNombreCompleto() + " - Nivel " + nivelMax + " desbloqueado",
                    cx, H - 95f);
        }

        for (int i = 0; i < TOTAL; i++) {
            float nx = startX + i * espX;
            boolean desbloqueado = (i + 1) <= nivelMax;
            if (desbloqueado) {
                fuente.setColor(Color.WHITE);
                tc(fuente, String.valueOf(i + 1), nx + radio, nodoY + radio + 10f);
            } else {
                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                tc(fuenteSmall, "[X]", nx + radio, nodoY + radio + 8f);
            }
            fuenteSmall.setColor(desbloqueado ? COLORES[i] : COLOR_TEXTO_GRIS);
            String[] lineas = NOMBRES[i].split("\n");
            for (int l = 0; l < lineas.length; l++) {
                tc(fuenteSmall, lineas[l], nx + radio, nodoY - 20f - l * 18f);
            }
        }

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< VOLVER", 32f, 47f);
        juego.batch.end();
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    private boolean dentroDeRect(float mx, float my, float rx, float ry, float rw, float rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    private boolean dentroDeCirculo(float mx, float my, float cx, float cy, float r) {
        float dx = mx - cx, dy = my - cy;
        return dx * dx + dy * dy <= r * r;
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
