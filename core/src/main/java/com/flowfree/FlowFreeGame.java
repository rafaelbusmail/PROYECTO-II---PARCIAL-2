package com.flowfree;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.flowfree.datos.GestorUsuarios;
import com.flowfree.hilos.HiloAutoguardado;
import com.flowfree.pantallas.PantallaLogin;

public class FlowFreeGame extends Game {

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;

    public GestorUsuarios gestorUsuarios;

    private HiloAutoguardado hiloAutoguardado;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        gestorUsuarios = new GestorUsuarios();

        hiloAutoguardado = new HiloAutoguardado(gestorUsuarios);
        hiloAutoguardado.start();

        setScreen(new PantallaLogin(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (hiloAutoguardado != null) {
            hiloAutoguardado.detener();
        }
    }
}
