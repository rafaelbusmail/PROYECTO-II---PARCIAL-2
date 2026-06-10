/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.modelo.Usuario;
import java.util.List;

public class PantallaRanking extends PantallaBase {

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private GlyphLayout layout;

    public PantallaRanking(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = new BitmapFont();
        fuenteGrande = new BitmapFont();
        layout = new GlyphLayout();
        fuente.getData().setScale(1.4f);
        fuenteGrande.getData().setScale(2.2f);
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
        }

        float cx = Gdx.graphics.getWidth() / 2f;
        float cy = Gdx.graphics.getHeight();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_ACENTO);
        dibujarTextoCentrado(fuenteGrande, "RANKING", cx, cy - 60f);

        List<Usuario> ranking = juego.gestorUsuarios.obtenerRanking();
        for (int i = 0; i < ranking.size() && i < 10; i++) {
            Usuario u = ranking.get(i);
            String linea = (i + 1) + ".  "
                    + u.getUsername()
                    + "   " + u.getEstadisticas().getPuntajeTotal()
                    + " pts";
            fuente.setColor(i == 0 ? COLOR_ACENTO : COLOR_TEXTO);
            fuente.draw(juego.batch, linea,
                    cx - 180f, cy - 130f - i * 40f);
        }

        if (ranking.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            dibujarTextoCentrado(fuente, "Sin jugadores aún",
                    cx, cy / 2f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        fuente.draw(juego.batch, "[ESC] Volver", 30f, 50f);

        juego.batch.end();
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto,
            float cx, float y) {
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
    }
}
