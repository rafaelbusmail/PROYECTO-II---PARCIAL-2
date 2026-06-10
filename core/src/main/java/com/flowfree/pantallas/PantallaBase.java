/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.flowfree.FlowFreeGame;

public abstract class PantallaBase implements Screen {

    protected FlowFreeGame juego;

    protected static final Color COLOR_FONDO = new Color(0.08f, 0.08f, 0.12f, 1f);
    protected static final Color COLOR_PANEL = new Color(0.13f, 0.13f, 0.18f, 1f);
    protected static final Color COLOR_ACENTO = new Color(0.20f, 0.80f, 0.60f, 1f);
    protected static final Color COLOR_TEXTO = new Color(0.95f, 0.95f, 0.95f, 1f);
    protected static final Color COLOR_TEXTO_GRIS = new Color(0.60f, 0.60f, 0.65f, 1f);
    protected static final Color COLOR_ERROR = new Color(0.90f, 0.25f, 0.25f, 1f);
    protected static final Color COLOR_BOTON = new Color(0.18f, 0.18f, 0.25f, 1f);
    protected static final Color COLOR_BOTON_HOVER = new Color(0.25f, 0.25f, 0.35f, 1f);
    protected static final Color COLOR_BORDE = new Color(0.25f, 0.25f, 0.35f, 1f);

    public PantallaBase(FlowFreeGame juego) {
        this.juego = juego;
    }

    /**
     * Crea una BitmapFont nítida al tamaño real en píxeles usando FreeType.
     * Evita el escalado de bitmap que causa pixelación. El archivo lsans.ttf
     * debe estar en assets/. Si no existe se usa la fuente por defecto (con
     * menor calidad) para no romper el build.
     */
    protected BitmapFont crearFuente(int tamano) {
        try {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                    Gdx.files.internal("lsans.ttf"));
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.size = tamano;
            param.color = Color.WHITE;
            param.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
            param.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
            BitmapFont font = gen.generateFont(param);
            gen.dispose();
            return font;
        } catch (Exception e) {
            BitmapFont fallback = new BitmapFont();
            fallback.getData().setScale(tamano / 15f);
            return fallback;
        }
    }

    protected void limpiarPantalla() {
        Gdx.gl.glClearColor(
                COLOR_FONDO.r, COLOR_FONDO.g,
                COLOR_FONDO.b, COLOR_FONDO.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public abstract void render(float delta);

    @Override
    public abstract void show();

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
