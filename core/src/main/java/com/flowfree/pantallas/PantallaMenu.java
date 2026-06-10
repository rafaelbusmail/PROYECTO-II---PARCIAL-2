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

    private float anchoVentana, altoVentana, xCentro;

    private static final String[] OPCIONES = {
        "JUGAR", "MI PERFIL", "RANKING", "CERRAR SESIÓN"
    };

    private static final Color[] COLORES_BOTONES = {
        new Color(0.20f, 0.80f, 0.60f, 1f),
        new Color(0.25f, 0.55f, 0.90f, 1f),
        new Color(0.90f, 0.65f, 0.10f, 1f),
        new Color(0.70f, 0.20f, 0.20f, 1f)
    };

    private int botonHover = -1;

    public PantallaMenu(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(22);
        fuenteGrande = crearFuente(44);
        fuenteSmall = crearFuente(16);
        layout = new GlyphLayout();
        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        xCentro = anchoVentana / 2f;
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();
        manejarInput();

        float mx = Gdx.input.getX();
        float my = altoVentana - Gdx.input.getY();
        float anchoBoton = 320f, altoBoton = 54f, espaciado = 20f;
        float totalH = OPCIONES.length * (altoBoton + espaciado);
        float startY = altoVentana / 2f - totalH / 2f - 30f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(0, altoVentana - 4f, anchoVentana, 4f);

        botonHover = -1;
        for (int i = 0; i < OPCIONES.length; i++) {
            float bx = xCentro - anchoBoton / 2f;
            float by = startY + (OPCIONES.length - 1 - i) * (altoBoton + espaciado);
            boolean hover = dentroDeRect(mx, my, bx, by, anchoBoton, altoBoton);
            if (hover) {
                botonHover = i;
            }
            Color cb = COLORES_BOTONES[i];
            Color cd = hover ? new Color(Math.min(cb.r + 0.1f, 1f), Math.min(cb.g + 0.1f, 1f), Math.min(cb.b + 0.1f, 1f), 1f) : cb;
            juego.shapeRenderer.setColor(cd);
            juego.shapeRenderer.rect(bx, by, anchoBoton, altoBoton);
            juego.shapeRenderer.setColor(new Color(cb.r * 0.6f, cb.g * 0.6f, cb.b * 0.6f, 1f));
            juego.shapeRenderer.rect(bx, by, 5f, altoBoton);
        }
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_ACENTO);
        dibujarTextoCentrado(fuenteGrande, "FLOW FREE", xCentro, altoVentana - 60f);

        Usuario u = juego.gestorUsuarios.getUsuarioActual();
        if (u != null) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            dibujarTextoCentrado(fuente, "Hola, " + u.getNombreCompleto(), xCentro, altoVentana - 110f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            String stats = "Nivel: " + u.getNivelMaxDesbloqueado()
                    + "   Partidas: " + u.getEstadisticas().getPartidasJugadas()
                    + "   Mejor: " + u.getEstadisticas().getMejorPuntaje() + " pts";
            dibujarTextoCentrado(fuenteSmall, stats, xCentro, altoVentana - 140f);
        }

        for (int i = 0; i < OPCIONES.length; i++) {
            float by = startY + (OPCIONES.length - 1 - i) * (altoBoton + espaciado);
            fuente.setColor(Color.WHITE);
            dibujarTextoCentrado(fuente, OPCIONES[i], xCentro, by + altoBoton / 2f + 8f);
        }

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        fuenteSmall.draw(juego.batch, "v1.0.0", 15f, 25f);
        juego.batch.end();
    }

    private void manejarInput() {
        if (!Gdx.input.justTouched()) {
            return;
        }
        float mx = Gdx.input.getX(), my = altoVentana - Gdx.input.getY();
        float anchoBoton = 320f, altoBoton = 54f, espaciado = 20f;
        float totalH = OPCIONES.length * (altoBoton + espaciado);
        float startY = altoVentana / 2f - totalH / 2f - 30f;
        for (int i = 0; i < OPCIONES.length; i++) {
            float bx = xCentro - anchoBoton / 2f;
            float by = startY + (OPCIONES.length - 1 - i) * (altoBoton + espaciado);
            if (dentroDeRect(mx, my, bx, by, anchoBoton, altoBoton)) {
                manejarOpcion(i);
                return;
            }
        }
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
        }
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto, float cx, float y) {
        layout.setText(font, texto);
        font.draw(juego.batch, texto, cx - layout.width / 2f, y);
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
