/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private BitmapFont fuenteSmall;
    private GlyphLayout layout;

    private float anchoVentana, altoVentana, xCentro;
    private static final int TOTAL_NIVELES = 5;

    private static final Color[] COLORES_NIVEL = {
        new Color(0.20f, 0.80f, 0.60f, 1f),
        new Color(0.25f, 0.55f, 0.90f, 1f),
        new Color(0.90f, 0.65f, 0.10f, 1f),
        new Color(0.90f, 0.25f, 0.25f, 1f),
        new Color(0.55f, 0.10f, 0.80f, 1f)
    };

    private static final String[] NOMBRES_NIVEL = {
        "Tutorial\n5x5", "Facil\n6x6", "Normal\n7x7", "Dificil\n8x8", "Experto\n9x9"
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
        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        xCentro = anchoVentana / 2f;
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();
        manejarInput();

        Usuario usuario = juego.gestorUsuarios.getUsuarioActual();
        int nivelMax = usuario != null ? usuario.getNivelMaxDesbloqueado() : 1;

        float mx = Gdx.input.getX(), my = altoVentana - Gdx.input.getY();
        float radioNodo = 45f, espaciadoX = 130f;
        float totalAncho = (TOTAL_NIVELES - 1) * espaciadoX;
        float startX = xCentro - totalAncho / 2f;
        float nodoY = altoVentana / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(0, altoVentana - 4f, anchoVentana, 4f);

        for (int i = 0; i < TOTAL_NIVELES - 1; i++) {
            float x1 = startX + i * espaciadoX + radioNodo;
            float x2 = startX + (i + 1) * espaciadoX - radioNodo;
            juego.shapeRenderer.setColor((i + 2) <= nivelMax
                    ? new Color(0.35f, 0.35f, 0.45f, 1f)
                    : new Color(0.20f, 0.20f, 0.28f, 1f));
            juego.shapeRenderer.rect(x1, nodoY - 3f, x2 - x1, 6f);
        }

        for (int i = 0; i < TOTAL_NIVELES; i++) {
            float nx = startX + i * espaciadoX;
            boolean desbloqueado = (i + 1) <= nivelMax;
            boolean hover = dentroDeCirculo(mx, my, nx + radioNodo, nodoY + radioNodo, radioNodo);
            Color cn = desbloqueado ? COLORES_NIVEL[i] : new Color(0.20f, 0.20f, 0.28f, 1f);
            if (hover && desbloqueado) {
                cn = new Color(Math.min(cn.r + 0.15f, 1f), Math.min(cn.g + 0.15f, 1f), Math.min(cn.b + 0.15f, 1f), 1f);
            }

            juego.shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.3f));
            juego.shapeRenderer.circle(nx + radioNodo + 3f, nodoY + radioNodo - 3f, radioNodo);
            juego.shapeRenderer.setColor(cn);
            juego.shapeRenderer.circle(nx + radioNodo, nodoY + radioNodo, radioNodo);

            if (!desbloqueado) {
                juego.shapeRenderer.setColor(new Color(0.40f, 0.40f, 0.50f, 1f));
                juego.shapeRenderer.rect(nx + radioNodo - 10f, nodoY + radioNodo - 8f, 20f, 16f);
            }
        }

        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(20f, 20f, 120f, 40f);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        dibujarTextoCentrado(fuenteGrande, "SELECCIONAR NIVEL", xCentro, altoVentana - 50f);

        if (usuario != null) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            dibujarTextoCentrado(fuente,
                    usuario.getNombreCompleto() + " — Nivel " + nivelMax + " desbloqueado",
                    xCentro, altoVentana - 95f);
        }

        for (int i = 0; i < TOTAL_NIVELES; i++) {
            float nx = startX + i * espaciadoX;
            boolean desbloqueado = (i + 1) <= nivelMax;
            fuente.setColor(desbloqueado ? Color.WHITE : COLOR_TEXTO_GRIS);

            if (desbloqueado) {
                dibujarTextoCentrado(fuente, String.valueOf(i + 1), nx + radioNodo, nodoY + radioNodo + 10f);
            } else {
                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                dibujarTextoCentrado(fuenteSmall, "[X]", nx + radioNodo, nodoY + radioNodo + 8f);
            }

            fuenteSmall.setColor(desbloqueado ? COLORES_NIVEL[i] : COLOR_TEXTO_GRIS);
            String[] lineas = NOMBRES_NIVEL[i].split("\n");
            for (int l = 0; l < lineas.length; l++) {
                dibujarTextoCentrado(fuenteSmall, lineas[l], nx + radioNodo, nodoY - 20f - l * 18f);
            }
        }

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< VOLVER", 32f, 47f);
        juego.batch.end();
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        if (!Gdx.input.justTouched()) {
            return;
        }
        float mx = Gdx.input.getX(), my = altoVentana - Gdx.input.getY();
        if (dentroDeRect(mx, my, 20f, 20f, 120f, 40f)) {
            juego.setScreen(new PantallaMenu(juego));
            return;
        }
        Usuario usuario = juego.gestorUsuarios.getUsuarioActual();
        int nivelMax = usuario != null ? usuario.getNivelMaxDesbloqueado() : 1;
        float radioNodo = 45f, espaciadoX = 130f;
        float startX = xCentro - (TOTAL_NIVELES - 1) * espaciadoX / 2f;
        float nodoY = altoVentana / 2f;
        for (int i = 0; i < TOTAL_NIVELES; i++) {
            float nx = startX + i * espaciadoX;
            if (dentroDeCirculo(mx, my, nx + radioNodo, nodoY + radioNodo, radioNodo)) {
                if ((i + 1) <= nivelMax) {
                    System.out.println("Iniciando nivel " + (i + 1));
                } else {
                    System.out.println("Nivel " + (i + 1) + " bloqueado");
                }
                return;
            }
        }
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto, float cx, float y) {
        layout.setText(font, texto);
        font.draw(juego.batch, texto, cx - layout.width / 2f, y);
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

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
