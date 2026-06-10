/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.flowfree.FlowFreeGame;

public class PantallaPerfil extends PantallaBase {

    private BitmapFont fuente;

    public PantallaPerfil(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = new BitmapFont();
        fuente.getData().setScale(1.5f);
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
        }

        juego.batch.begin();
        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "PERFIL - Próximamente",
                50f, Gdx.graphics.getHeight() / 2f);
        fuente.setColor(COLOR_TEXTO_GRIS);
        fuente.draw(juego.batch, "[ESC] Volver", 50f, 50f);
        juego.batch.end();
    }

    @Override
    public void dispose() {
        if (fuente != null) {
            fuente.dispose();
        }
    }
}
