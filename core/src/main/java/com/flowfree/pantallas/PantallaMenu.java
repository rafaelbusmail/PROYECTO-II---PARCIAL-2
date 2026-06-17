/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.modelo.Usuario;

public class PantallaMenu extends PantallaBase {

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private BitmapFont fuenteSmall;
    private GlyphLayout layout;

    private static final Color[] COLORES = {
        new Color(0.20f, 0.80f, 0.60f, 1f), new Color(0.25f, 0.55f, 0.90f, 1f),
        new Color(0.90f, 0.65f, 0.10f, 1f), new Color(0.70f, 0.20f, 0.20f, 1f),
        new Color(0.50f, 0.15f, 0.15f, 1f)
    };
    private String[] opciones;

    public PantallaMenu(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        juego.actualizarIdioma();
        juego.iniciarMusica();
        fuente = crearFuente(22);
        fuenteGrande = crearFuente(44);
        fuenteSmall = crearFuente(16);
        layout = new GlyphLayout();
        opciones = new String[]{
            com.flowfree.datos.Traductor.jugar(juego.idiomaActual),
            com.flowfree.datos.Traductor.miPerfil(juego.idiomaActual),
            com.flowfree.datos.Traductor.ranking(juego.idiomaActual),
            com.flowfree.datos.Traductor.cerrarSesion(juego.idiomaActual),
            com.flowfree.datos.Traductor.salir(juego.idiomaActual)
        };
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

        float anchoBoton = 320f, altoBoton = 54f, espaciado = 20f;
        float totalH = opciones.length * (altoBoton + espaciado);
        float startY = H / 2f - totalH / 2f - 30f;

        limpiarPantalla();

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            for (int i = 0; i < opciones.length; i++) {
                float bx = cx - anchoBoton / 2f;
                float by = startY + (opciones.length - 1 - i) * (altoBoton + espaciado);
                if (dentroDeRect(mx, my, bx, by, anchoBoton, altoBoton)) {
                    manejarOpcion(i);
                    return;
                }
            }
        }

        float mx = Gdx.input.getX(), my = H - Gdx.input.getY();

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(0, H - 4f, W, 4f);

        for (int i = 0; i < opciones.length; i++) {
            float bx = cx - anchoBoton / 2f;
            float by = startY + (opciones.length - 1 - i) * (altoBoton + espaciado);
            boolean hover = dentroDeRect(mx, my, bx, by, anchoBoton, altoBoton);
            Color cb = COLORES[i];
            Color cd = hover ? new Color(Math.min(cb.r + 0.1f, 1f), Math.min(cb.g + 0.1f, 1f), Math.min(cb.b + 0.1f, 1f), 1f) : cb;
            juego.shapeRenderer.setColor(cd);
            juego.shapeRenderer.rect(bx, by, anchoBoton, altoBoton);
            juego.shapeRenderer.setColor(new Color(cb.r * 0.6f, cb.g * 0.6f, cb.b * 0.6f, 1f));
            juego.shapeRenderer.rect(bx, by, 5f, altoBoton);
        }
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_ACENTO);
        tc(fuenteGrande, "FLOW FREE", cx, H - 60f);

        Usuario u = juego.gestorUsuarios.getUsuarioActual();
        if (u != null) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, com.flowfree.datos.Traductor.hola(juego.idiomaActual) + ", " + u.getNombreCompleto(), cx, H - 110f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            long tiempo = u.getEstadisticas().getTiempoTotalJugado();
            String tiempoStr = formatTiempo(tiempo);
            String stats = com.flowfree.datos.Traductor.nivel(juego.idiomaActual) + ": " + u.getNivelMaxDesbloqueado()
                    + "   " + com.flowfree.datos.Traductor.partidas(juego.idiomaActual) + ": " + u.getEstadisticas().getPartidasJugadas()
                    + "   " + com.flowfree.datos.Traductor.tiempo(juego.idiomaActual) + ": " + tiempoStr
                    + "   " + com.flowfree.datos.Traductor.mejor(juego.idiomaActual) + ": " + u.getEstadisticas().getMejorPuntaje() + " pts";
            tc(fuenteSmall, stats, cx, H - 140f);
        }

        for (int i = 0; i < opciones.length; i++) {
            float by = startY + (opciones.length - 1 - i) * (altoBoton + espaciado);
            fuente.setColor(Color.WHITE);
            tc(fuente, opciones[i], cx, by + altoBoton / 2f + 8f);
        }

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch, "v1.0.0", 15f, 25f);
        juego.batch.end();
    }

    private void manejarOpcion(int i) {
        switch (i) {
            case 0:
                juego.setScreen(new PantallaMapa(juego));
                break;
            case 1:
                juego.setScreen(new PantallaPerfil(juego));
                break;
            case 2:
                juego.setScreen(new PantallaRanking(juego));
                break;
            case 3:
                juego.gestorUsuarios.cerrarSesion();
                juego.setScreen(new PantallaLogin(juego));
                break;
            case 4:
                Gdx.app.exit();
                break;
        }
    }

    private String formatTiempo(long segundos) {
        int h = (int)(segundos / 3600);
        int m = (int)((segundos % 3600) / 60);
        int s = (int)(segundos % 60);
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    private boolean dentroDeRect(float mx, float my, float rx, float ry, float rw, float rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
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
