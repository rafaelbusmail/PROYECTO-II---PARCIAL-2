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
import com.flowfree.modelo.Estadisticas;
import java.text.SimpleDateFormat;

public class PantallaPerfil extends PantallaBase {

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private BitmapFont fuenteSmall;
    private GlyphLayout layout;

    private float anchoVentana, altoVentana, xCentro;

    private static final Color COLOR_ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color COLOR_PLATA = new Color(0.75f, 0.75f, 0.75f, 1f);
    private static final Color COLOR_BRONCE = new Color(0.80f, 0.50f, 0.20f, 1f);

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PantallaPerfil(FlowFreeGame juego) {
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

        Usuario u = juego.gestorUsuarios.getUsuarioActual();
        if (u == null) {
            juego.setScreen(new PantallaMenu(juego));
            return;
        }

        Estadisticas stats = u.getEstadisticas();

        float panelW = Math.min(560f, anchoVentana - 40f);
        float panelH = 460f;
        float panelX = xCentro - panelW / 2f;
        float panelY = altoVentana / 2f - panelH / 2f - 10f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);

        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);

        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        float avR = 38f;
        float avX = xCentro, avY = panelY + panelH - 70f;
        juego.shapeRenderer.setColor(COLOR_BORDE);
        juego.shapeRenderer.circle(avX, avY, avR + 3);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.circle(avX, avY, avR);
        juego.shapeRenderer.setColor(new Color(0.08f, 0.08f, 0.12f, 1f));
        juego.shapeRenderer.circle(avX, avY, avR - 4);

        float cardW = (panelW - 60f) / 2f;
        float cardH = 64f;
        float cardY1 = panelY + 170f;
        float cardY2 = panelY + 95f;
        float cardX1 = panelX + 20f;
        float cardX2 = panelX + 30f + cardW;

        dibujarTarjeta(cardX1, cardY1, cardW, cardH, COLOR_ACENTO);
        dibujarTarjeta(cardX2, cardY1, cardW, cardH, new Color(0.25f, 0.55f, 0.90f, 1f));
        dibujarTarjeta(cardX1, cardY2, cardW, cardH, new Color(0.90f, 0.65f, 0.10f, 1f));
        dibujarTarjeta(cardX2, cardY2, cardW, cardH, new Color(0.55f, 0.10f, 0.80f, 1f));

        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 20f, panelY + 14f, 140f, 40f);
        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_TEXTO);
        dibujarTC(fuenteGrande, "MI PERFIL", xCentro, altoVentana - 42f);

        String inicial = u.getNombreCompleto().isEmpty() ? "?"
                : String.valueOf(u.getNombreCompleto().charAt(0)).toUpperCase();
        fuenteGrande.setColor(COLOR_ACENTO);
        dibujarTC(fuenteGrande, inicial, avX, avY + 14f);

        fuente.setColor(COLOR_TEXTO);
        dibujarTC(fuente, u.getNombreCompleto(), xCentro, panelY + panelH - 118f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        dibujarTC(fuenteSmall, "@" + u.getUsername(), xCentro, panelY + panelH - 140f);

        String fechaReg = u.getFechaRegistro() != null ? SDF.format(u.getFechaRegistro()) : "—";
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        dibujarTC(fuenteSmall, "Registrado: " + fechaReg, xCentro, panelY + panelH - 162f);

        String ultimaSes = u.getUltimaSesion() != null ? SDF.format(u.getUltimaSesion()) : "—";
        dibujarTC(fuenteSmall, "Última sesión: " + ultimaSes, xCentro, panelY + panelH - 180f);

        dibujarTarjetaTexto(cardX1, cardY1, cardW,
                String.valueOf(u.getNivelMaxDesbloqueado()),
                "Nivel alcanzado", fuente, fuenteSmall);

        dibujarTarjetaTexto(cardX2, cardY1, cardW,
                String.valueOf(stats.getPartidasJugadas()),
                "Partidas jugadas", fuente, fuenteSmall);

        dibujarTarjetaTexto(cardX1, cardY2, cardW,
                stats.getMejorPuntaje() + " pts",
                "Mejor puntaje", fuente, fuenteSmall);

        dibujarTarjetaTexto(cardX2, cardY2, cardW,
                stats.getPuntajeTotal() + " pts",
                "Puntaje total", fuente, fuenteSmall);

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< VOLVER", panelX + 35f, panelY + 40f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        dibujarTC(fuenteSmall, "[ESC] Volver al menú", xCentro, 28f);

        juego.batch.end();

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = altoVentana - Gdx.input.getY();
            if (mx >= panelX + 20f && mx <= panelX + 160f && my >= panelY + 14f && my <= panelY + 54f) {
                juego.setScreen(new PantallaMenu(juego));
            }
        }
    }

    private void dibujarTarjeta(float x, float y, float w, float h, Color acento) {
        juego.shapeRenderer.setColor(new Color(
                acento.r * 0.15f + 0.10f,
                acento.g * 0.15f + 0.10f,
                acento.b * 0.15f + 0.10f, 1f));
        juego.shapeRenderer.rect(x, y, w, h);
        juego.shapeRenderer.setColor(acento);
        juego.shapeRenderer.rect(x, y, w, 3f);
    }

    private void dibujarTarjetaTexto(float x, float y, float w,
            String valor, String etiqueta,
            BitmapFont fValor, BitmapFont fEt) {
        float cx = x + w / 2f;
        fValor.setColor(COLOR_TEXTO);
        dibujarTC(fValor, valor, cx, y + 48f);
        fEt.setColor(COLOR_TEXTO_GRIS);
        dibujarTC(fEt, etiqueta, cx, y + 20f);
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
