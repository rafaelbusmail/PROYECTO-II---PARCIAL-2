package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.enums.Idioma;
import com.flowfree.datos.Traductor;
import com.flowfree.modelo.Preferencias;

public class PantallaPreferencias extends PantallaBase {

    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private Preferencias pref;

    private float volMusica;
    private int idxIdioma;

    private static final String[] IDIOMAS = {"Español", "English"};
    private static final Color COLOR_SLIDER_BG = new Color(0.18f, 0.18f, 0.25f, 1f);
    private static final Color COLOR_SLIDER_FILL = new Color(0.30f, 0.70f, 1.00f, 1f);

    private static class Zonas {
        float slider1Y, sliderX, sliderW;
        float langY, langH;
    }
    private Zonas zonas = new Zonas();

    public PantallaPreferencias(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();

        String user = juego.gestorUsuarios.getUsuarioActual().getUsername();
        pref = juego.gestorUsuarios.cargarPreferencias(user);
        volMusica = pref.getVolumenMusica();
        idxIdioma = pref.getIdioma() == Idioma.EN ? 1 : 0;
    }

    @Override
    public void render(float delta) {
        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        limpiarPantalla();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            guardarSalir();
            return;
        }

        manejarInput(W, H);

        float panelW = Math.min(520f, W - 40f);
        float panelH = 460f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f - 10f;
        float xCont = panelX + 40f;
        float anchoC = panelW - 80f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 20f, panelY + 14f, 240f, 40f);

        float sliderY = panelY + 200f;
        zonas.slider1Y = sliderY;
        zonas.sliderX = xCont + 10f;
        zonas.sliderW = anchoC - 20f;
        dibujarSliderShape(zonas.sliderX, sliderY, zonas.sliderW, volMusica);

        float langY = panelY + 80f;
        zonas.langY = langY;
        zonas.langH = 36f;
        for (int i = 0; i < IDIOMAS.length; i++) {
            float ix = xCont + i * (anchoC / 2f + 10f);
            float iw = anchoC / 2f;
            juego.shapeRenderer.setColor(i == idxIdioma ? COLOR_ACENTO : COLOR_BOTON);
            juego.shapeRenderer.rect(ix, langY, iw, zonas.langH);
        }

        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.preferenciasTitulo(juego.idiomaActual), cx, H - 42f);

        fuente.setColor(COLOR_TEXTO);
        tc(fuente, Traductor.volumenMusica(juego.idiomaActual), xCont + anchoC / 2f, panelY + 310f);
        tc(fuenteSmall, (int)(volMusica * 100) + "%", xCont + anchoC / 2f, panelY + 280f);

        tc(fuente, Traductor.idioma(juego.idiomaActual), xCont + anchoC / 2f, panelY + 170f);

        for (int i = 0; i < IDIOMAS.length; i++) {
            float ix = xCont + i * (anchoC / 2f + 10f);
            float iw = anchoC / 2f;
            fuenteSmall.setColor(i == idxIdioma ? new Color(0.05f, 0.05f, 0.10f, 1f) : COLOR_TEXTO_GRIS);
            tc(fuenteSmall, IDIOMAS[i], ix + iw / 2f, langY + 24f);
        }

        fuente.setColor(COLOR_TEXTO);
        layout.setText(fuente, "< " + Traductor.guardarYVolver(juego.idiomaActual));
        fuente.draw(juego.batch, "< " + Traductor.guardarYVolver(juego.idiomaActual),
                panelX + 20f + (240f - layout.width) / 2f, panelY + 40f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.guardarYVolver(juego.idiomaActual), cx, 28f);

        juego.batch.end();
    }

    private static float panelH = 460f;

    private void dibujarSliderShape(float x, float y, float ancho, float valor) {
        float alto = 12f;
        float bClick = ancho - 20f;
        float fillW = bClick * valor;

        juego.shapeRenderer.setColor(COLOR_SLIDER_BG);
        juego.shapeRenderer.rect(x + 10f, y + 4f, bClick, alto);
        juego.shapeRenderer.setColor(COLOR_SLIDER_FILL);
        juego.shapeRenderer.rect(x + 10f, y + 4f, fillW, alto);
        juego.shapeRenderer.setColor(COLOR_TEXTO);
        juego.shapeRenderer.circle(x + 10f + fillW, y + 10f, 8f);
    }

    private void manejarInput(float W, float H) {
        if (!Gdx.input.justTouched()) return;

        float mx = Gdx.input.getX();
        float my = H - Gdx.input.getY();

        float panelW = Math.min(520f, W - 40f);
        float cx = W / 2f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f - 10f;

        if (mx >= panelX + 20f && mx <= panelX + 260f && my >= panelY + 14f && my <= panelY + 54f) {
            guardarSalir();
            return;
        }

        if (my >= zonas.slider1Y && my <= zonas.slider1Y + 20f
                && mx >= zonas.sliderX && mx <= zonas.sliderX + zonas.sliderW) {
            volMusica = Math.max(0f, Math.min(1f, (mx - zonas.sliderX) / zonas.sliderW));
        }

        if (my >= zonas.langY && my <= zonas.langY + zonas.langH) {
            float anchoC = panelW - 80f;
            float xCont = panelX + 40f;
            for (int i = 0; i < IDIOMAS.length; i++) {
                float ix = xCont + i * (anchoC / 2f + 10f);
                float iw = anchoC / 2f;
                if (mx >= ix && mx <= ix + iw) {
                    idxIdioma = i;
                }
            }
        }
    }

    private void guardarSalir() {
        pref.setVolumenMusica(volMusica);
        pref.setIdioma(idxIdioma == 0 ? Idioma.ES : Idioma.EN);
        juego.idiomaActual = pref.getIdioma();
        juego.ajustarVolumenMusica(volMusica);
        String user = juego.gestorUsuarios.getUsuarioActual().getUsername();
        juego.gestorUsuarios.guardarPreferencias(user, pref);
        juego.gestorUsuarios.guardarIdiomaGlobal(juego.idiomaActual);
        juego.setScreen(new PantallaPerfil(juego));
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void dispose() {
        if (fuente != null) fuente.dispose();
        if (fuenteGrande != null) fuenteGrande.dispose();
        if (fuenteSmall != null) fuenteSmall.dispose();
    }
}
