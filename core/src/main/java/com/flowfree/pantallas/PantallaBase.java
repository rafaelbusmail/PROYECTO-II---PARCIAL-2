/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.flowfree.FlowFreeGame;

public abstract class PantallaBase implements Screen {

    protected static final Color COLOR_FONDO = new Color(0.08f, 0.08f, 0.12f, 1f);
    protected static final Color COLOR_PANEL = new Color(0.12f, 0.12f, 0.18f, 1f);
    protected static final Color COLOR_ACENTO = new Color(0.30f, 0.70f, 1.00f, 1f);
    protected static final Color COLOR_BOTON = new Color(0.20f, 0.20f, 0.28f, 1f);
    protected static final Color COLOR_BORDE = new Color(0.30f, 0.30f, 0.40f, 1f);
    protected static final Color COLOR_TEXTO = new Color(0.95f, 0.95f, 0.95f, 1f);
    protected static final Color COLOR_TEXTO_GRIS = new Color(0.60f, 0.60f, 0.70f, 1f);
    protected static final Color COLOR_ERROR = new Color(1.00f, 0.30f, 0.30f, 1f);
    protected static final Color COLOR_EXITO = new Color(0.30f, 0.90f, 0.40f, 1f);

    protected final FlowFreeGame juego;

    protected PantallaBase(FlowFreeGame juego) {
        this.juego = juego;
    }

    protected void limpiarPantalla() {
        Gdx.gl.glClearColor(COLOR_FONDO.r, COLOR_FONDO.g, COLOR_FONDO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected BitmapFont crearFuente(int tamano) {
        try {
            FreeTypeFontGenerator gen
                    = new FreeTypeFontGenerator(Gdx.files.internal("lsans.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter p
                    = new FreeTypeFontGenerator.FreeTypeFontParameter();
            p.size = tamano;
            p.color = Color.WHITE;
            p.borderWidth = 0;
            p.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚñÑüÜ¡¿";
            BitmapFont font = gen.generateFont(p);
            gen.dispose();
            return font;
        } catch (Exception e) {
            System.err.println("crearFuente fallback: " + e.getMessage());
            BitmapFont f = new BitmapFont();
            f.getData().setScale(tamano / 16f);
            return f;
        }
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
    public void dispose() {
    }
}
