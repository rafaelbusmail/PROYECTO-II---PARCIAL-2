package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.modelo.Reto;
import com.flowfree.modelo.Usuario;
import java.util.List;
import com.flowfree.datos.Traductor;
import java.io.File;

public class PantallaAmigos extends PantallaBase {

    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private List<Usuario> amigos;
    private String mensaje = "";
    private boolean mensajeExito = true;
    private String inputBuffer = "";
    private boolean modoInput = false;
    private boolean modoSolicitudes = false;
    private int seleccionComparar = -1;
    private int amigoSeleccionado = -1;
    private boolean modoComparar = false;
    private Usuario amigoComparado = null;
    private List<String> solicitudesPendientes;
    private int nivelReto = -1;
    private String usernameReto = "";
    private boolean modoRetos = false;
    private List<Reto> retosPendientes;
    private int frameDelay = 0;
    private int mensajeFrames = 0;
    private Texture[] friendAvatars = new Texture[0];

    private static final Color COLOR_ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color COLOR_PLATA = new Color(0.80f, 0.80f, 0.80f, 1f);
    private static final Color[] COLORES_AVATAR = {
        new Color(0.30f, 0.70f, 1.00f, 1f),
        new Color(0.20f, 0.80f, 0.60f, 1f),
        new Color(0.90f, 0.65f, 0.10f, 1f),
        new Color(0.90f, 0.25f, 0.25f, 1f),
        new Color(0.55f, 0.10f, 0.80f, 1f),
        new Color(0.95f, 0.45f, 0.75f, 1f),
        new Color(0.10f, 0.85f, 0.90f, 1f),
        new Color(1.00f, 0.84f, 0.00f, 1f),
    };

    public PantallaAmigos(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(32);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();
        inputBuffer = "";
        modoInput = false;
        modoSolicitudes = false;
        modoRetos = false;
        modoComparar = false;
        seleccionComparar = -1;
        amigoSeleccionado = -1;
        amigoComparado = null;
        mensaje = "";
        mensajeFrames = 0;
        nivelReto = -1;
        usernameReto = "";
        recargarAmigos();
        solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
        retosPendientes = juego.gestorUsuarios.obtenerRetosPendientes();
    }

    private void recargarAmigos() {
        for (Texture t : friendAvatars) {
            if (t != null) t.dispose();
        }
        amigos = juego.gestorUsuarios.obtenerAmigos();
        friendAvatars = new Texture[amigos.size()];
        for (int i = 0; i < amigos.size(); i++) {
            Usuario a = amigos.get(i);
            String ruta = a.getAvatarRuta();
            if (ruta != null && !ruta.isEmpty()) {
                try {
                    friendAvatars[i] = cargarTexturaCircular(ruta, 12f);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        if (frameDelay > 0) frameDelay--;

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        limpiarPantalla();

        if (modoComparar) {
            renderComparar(W, H, cx);
        } else if (modoSolicitudes) {
            renderSolicitudes(W, H, cx);
        } else if (modoRetos) {
            renderRetos(W, H, cx);
        } else if (nivelReto > 0) {
            renderReto(W, H, cx);
        } else {
            renderLista(W, H, cx);
        }
    }

    private void renderSolicitudes(float W, float H, float cx) {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.solicitudesPendientes(juego.idiomaActual), cx, H - 42f);

        float y = H / 2f + 80f;
        float btnW = 160f, btnH = 36f, gap = 12f;
        if (solicitudesPendientes.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.noSolicitudes(juego.idiomaActual), cx, y);
        } else {
            for (String s : solicitudesPendientes) {
                juego.batch.end();

                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
                juego.shapeRenderer.rect(cx - btnW - gap / 2f, y - btnH / 2f, btnW, btnH);
                juego.shapeRenderer.setColor(new Color(0.70f, 0.25f, 0.25f, 1f));
                juego.shapeRenderer.rect(cx + gap / 2f, y - btnH / 2f, btnW, btnH);
                juego.shapeRenderer.end();

                juego.batch.begin();
                fuente.setColor(COLOR_TEXTO);
                tc(fuente, s, cx, y + btnH / 2f + 22f);
                fuenteSmall.setColor(Color.WHITE);
                tc(fuenteSmall, Traductor.aceptar(juego.idiomaActual), cx - btnW / 2f - gap / 2f, y + 6f);
                tc(fuenteSmall, Traductor.rechazar(juego.idiomaActual), cx + btnW / 2f + gap / 2f, y + 6f);
                y -= 80f;
            }
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual), cx, 40f);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            modoSolicitudes = false;
            return;
        }
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            float y0 = H / 2f + 80f;
            for (int i = 0; i < solicitudesPendientes.size(); i++) {
                float fy = y0 - i * 80f;
                if (my >= fy - btnH / 2f && my <= fy + btnH / 2f
                        && mx >= cx - btnW - gap / 2f && mx <= cx - gap / 2f) {
                    boolean ok = juego.gestorUsuarios.aceptarSolicitud(solicitudesPendientes.get(i));
                    mensaje = ok ? Traductor.solicitudAceptada(juego.idiomaActual) + ": " + solicitudesPendientes.get(i) : Traductor.errorAceptar(juego.idiomaActual);
                    mensajeFrames = 180;
                    mensajeExito = ok;
                    recargarAmigos();
                    solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
                    modoSolicitudes = false;
                    frameDelay = 5;
                    return;
                }
                if (my >= fy - btnH / 2f && my <= fy + btnH / 2f
                        && mx >= cx + gap / 2f && mx <= cx + btnW + gap / 2f) {
                    juego.gestorUsuarios.rechazarSolicitud(solicitudesPendientes.get(i));
                    mensaje = Traductor.solicitudRechazada(juego.idiomaActual) + ": " + solicitudesPendientes.get(i);
                    mensajeFrames = 180;
                    mensajeExito = false;
                    recargarAmigos();
                    solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
                    frameDelay = 5;
                    return;
                }
            }
        }
    }

    private void renderRetos(float W, float H, float cx) {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.retosPendientes(juego.idiomaActual), cx, H - 42f);

        float y = H / 2f + 80f;
        float btnW = 160f, btnH = 36f, gap = 12f;
        if (retosPendientes.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.noRetos(juego.idiomaActual), cx, y);
        } else {
            for (Reto r : retosPendientes) {
                juego.batch.end();

                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
                juego.shapeRenderer.rect(cx - btnW - gap / 2f, y - btnH / 2f, btnW, btnH);
                juego.shapeRenderer.setColor(new Color(0.70f, 0.25f, 0.25f, 1f));
                juego.shapeRenderer.rect(cx + gap / 2f, y - btnH / 2f, btnW, btnH);
                juego.shapeRenderer.end();

                juego.batch.begin();
                fuente.setColor(COLOR_TEXTO);
                tc(fuente, r.getRemitente() + " - " + Traductor.nivel(juego.idiomaActual) + " " + r.getNivel(), cx, y + btnH / 2f + 22f);
                fuenteSmall.setColor(Color.WHITE);
                tc(fuenteSmall, Traductor.aceptar(juego.idiomaActual), cx - btnW / 2f - gap / 2f, y + 6f);
                tc(fuenteSmall, Traductor.rechazar(juego.idiomaActual), cx + btnW / 2f + gap / 2f, y + 6f);
                y -= 80f;
            }
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual), cx, 40f);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            modoRetos = false;
            retosPendientes = juego.gestorUsuarios.obtenerRetosPendientes();
            return;
        }
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            float y0 = H / 2f + 80f;
            for (int i = 0; i < retosPendientes.size(); i++) {
                float fy = y0 - i * 80f;
                if (my >= fy - btnH / 2f && my <= fy + btnH / 2f
                        && mx >= cx - btnW - gap / 2f && mx <= cx - gap / 2f) {
                    Reto r = retosPendientes.get(i);
                    juego.retoRemitente = r.getRemitente();
                    juego.retoNivel = r.getNivel();
                    juego.retoTiempoRemitente = r.getTiempoRemitente();
                    juego.retoPuntajeRemitente = r.getPuntajeRemitente();
                    modoRetos = false;
                    juego.setScreen(new PantallaJuego(juego, r.getNivel()));
                    return;
                }
                if (my >= fy - btnH / 2f && my <= fy + btnH / 2f
                        && mx >= cx + gap / 2f && mx <= cx + btnW + gap / 2f) {
                    Reto r = retosPendientes.get(i);
                    juego.gestorUsuarios.rechazarReto(r.getRemitente(), r.getNivel());
                    retosPendientes = juego.gestorUsuarios.obtenerRetosPendientes();
                    return;
                }
            }
        }
    }

    private void renderReto(float W, float H, float cx) {
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.retarA(juego.idiomaActual) + " " + usernameReto, cx, H - 42f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        tc(fuente, Traductor.seleccionaNivelReto(juego.idiomaActual), cx, H / 2f + 60f);
        tc(fuente, Traductor.nivelActual(juego.idiomaActual) + ": " + nivelReto, cx, H / 2f + 20f);

        Usuario yoReto = juego.gestorUsuarios.getUsuarioActual();
        int maxNivelReto = (yoReto != null) ? yoReto.getNivelMaxDesbloqueado() : 5;

        fuenteSmall.setColor(COLOR_TEXTO);
        tc(fuenteSmall, Traductor.retoInst(juego.idiomaActual), cx, 40f);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) nivelReto = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) nivelReto = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) nivelReto = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) nivelReto = 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) nivelReto = 5;
        if (nivelReto > maxNivelReto) nivelReto = maxNivelReto;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            nivelReto = -1;
            usernameReto = "";
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && nivelReto >= 1 && nivelReto <= maxNivelReto) {
            juego.retoDestinatario = usernameReto.toUpperCase();
            juego.retoNivel = nivelReto;
            juego.setScreen(new PantallaJuego(juego, nivelReto));
        }
    }

    private void renderLista(float W, float H, float cx) {
        float panelW = Math.min(580f, W - 40f);
        float panelH = Math.min(440f, H - 80f);
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f;
        float xCont = panelX + 24f;
        float yCont = panelY + panelH - 50f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, yCont + 16f, panelW - 40, 3);

        float btnBaseW = (panelW - 60f) / 3f;
        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 15f, panelY + 14f, btnBaseW, 40f);
        juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
        juego.shapeRenderer.rect(panelX + 20f + btnBaseW, panelY + 14f, btnBaseW, 40f);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        float btnAddX = panelX + 25f + btnBaseW * 2f;
        juego.shapeRenderer.rect(btnAddX, panelY + 14f, btnBaseW, 40f);

        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.misAmigos(juego.idiomaActual), cx, H - 42f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        float colUser = xCont + 30f;
        float colNivel = xCont + panelW - 300f;
        float colPts = xCont + panelW - 260f;
        float colC = xCont + panelW - 60f;
        float colR = xCont + panelW - 95f;
        float colX = xCont + panelW - 130f;
        tc(fuenteSmall, Traductor.amigo(juego.idiomaActual), colUser, yCont + 6f);
        tc(fuenteSmall, Traductor.nivelAbr(juego.idiomaActual), colNivel, yCont + 6f);
        tc(fuenteSmall, Traductor.puntos(juego.idiomaActual), colPts, yCont + 6f);

        yCont -= 30f;

        if (amigos.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.noAmigos(juego.idiomaActual), cx, yCont - 60f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteSmall, Traductor.usarAgregar(juego.idiomaActual), cx, yCont - 90f);
        } else {
            float filaH = 38f;
            float avatarR = 12f;
            for (int i = 0; i < amigos.size(); i++) {
                Usuario a = amigos.get(i);
                float fy = yCont - i * filaH;
                if (fy < panelY + 60f) break;

                float rowMidY = fy + filaH / 2f;

                juego.batch.end();

                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                if (i % 2 == 0) {
                    juego.shapeRenderer.setColor(new Color(0.11f, 0.11f, 0.17f, 1f));
                    juego.shapeRenderer.rect(panelX + 10, fy, panelW - 20, filaH);
                }

                if (amigoSeleccionado == i) {
                    juego.shapeRenderer.setColor(new Color(0.15f, 0.25f, 0.45f, 1f));
                    juego.shapeRenderer.rect(panelX + 10, fy, panelW - 20, filaH);
                }

                Color bar = i == 0 ? COLOR_ORO : i == 1 ? COLOR_PLATA : COLOR_BORDE;
                juego.shapeRenderer.setColor(bar);
                juego.shapeRenderer.rect(panelX + 10, fy, 4f, filaH);

                float avCx = xCont + 10f;
                if (friendAvatars.length > i && friendAvatars[i] != null) {
                    juego.shapeRenderer.setColor(COLOR_BORDE);
                    juego.shapeRenderer.circle(avCx, rowMidY + 4f, avatarR + 2f);
                } else {
                    String ini = a.getUsername().isEmpty() ? "?" : a.getUsername().substring(0, 1).toUpperCase();
                    Color ac = COLORES_AVATAR[i % COLORES_AVATAR.length];
                    juego.shapeRenderer.setColor(COLOR_BORDE);
                    juego.shapeRenderer.circle(avCx, rowMidY + 4f, avatarR + 2f);
                    juego.shapeRenderer.setColor(ac);
                    juego.shapeRenderer.circle(avCx, rowMidY + 4f, avatarR);
                }

                juego.shapeRenderer.end();

                juego.batch.begin();
                if (friendAvatars.length > i && friendAvatars[i] != null) {
                    juego.batch.draw(friendAvatars[i], avCx - avatarR, rowMidY + 4f - avatarR, avatarR * 2, avatarR * 2);
                } else {
                    String ini = a.getUsername().isEmpty() ? "?" : a.getUsername().substring(0, 1).toUpperCase();
                    fuenteSmall.setColor(COLOR_TEXTO);
                    tc(fuenteSmall, ini, avCx, rowMidY + 10f);
                }
                juego.batch.end();

                juego.batch.begin();

                fuente.setColor(COLOR_TEXTO);
                fuente.draw(juego.batch, a.getUsername(), colUser, fy + filaH / 2f + 8f);
                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                fuenteSmall.draw(juego.batch, "Nv." + a.getNivelMaxDesbloqueado(), colNivel, fy + filaH / 2f + 8f);
                fuente.setColor(bar);
                fuente.draw(juego.batch, a.getEstadisticas().getPuntajeTotal() + " pts", colPts, fy + filaH / 2f + 8f);

                fuenteSmall.setColor(seleccionComparar == i ? COLOR_EXITO : COLOR_ACENTO);
                String cmp = seleccionComparar == i ? "[-]" : "[C]";
                fuenteSmall.draw(juego.batch, cmp, colC, fy + filaH / 2f + 8f);

                fuenteSmall.setColor(new Color(0.90f, 0.65f, 0.10f, 1f));
                fuenteSmall.draw(juego.batch, "[R]", colR, fy + filaH / 2f + 8f);

                fuenteSmall.setColor(COLOR_ERROR);
                fuenteSmall.draw(juego.batch, "[X]", colX, fy + filaH / 2f + 8f);
            }
        }

        if (mensajeFrames > 0) {
            mensajeFrames--;
            if (mensajeFrames == 0) mensaje = "";
        }
        if (!mensaje.isEmpty()) {
            fuenteSmall.setColor(mensajeExito ? COLOR_EXITO : COLOR_ERROR);
            tc(fuenteSmall, mensaje, cx, panelY + 80f);
        }

        fuente.setColor(COLOR_TEXTO);
        tc(fuente, "< " + Traductor.volver(juego.idiomaActual), panelX + 15f + btnBaseW / 2f, panelY + 40f);
        String solLabel = Traductor.solicitudes(juego.idiomaActual);
        if (retosPendientes.size() > 0) solLabel += "(" + solicitudesPendientes.size() + ") R:" + retosPendientes.size();
        else solLabel += "(" + solicitudesPendientes.size() + ")";
        fuente.setColor(Color.WHITE);
        tc(fuente, solLabel, panelX + 20f + btnBaseW + btnBaseW / 2f, panelY + 40f);
        fuente.setColor(new Color(0.05f, 0.05f, 0.10f, 1f));
        tc(fuente, Traductor.agregarAmigo(juego.idiomaActual), btnAddX + btnBaseW / 2f, panelY + 40f);

        if (modoInput) {
            fuenteSmall.setColor(COLOR_TEXTO);
            tc(fuenteSmall, Traductor.usuario(juego.idiomaActual) + ": " + inputBuffer + (System.currentTimeMillis() / 500 % 2 == 0 ? "|" : ""), cx, panelY + 105f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteSmall, "[ENTER] Confirmar  [ESC] Cancelar", cx, panelY + 85f);
        }

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[R] " + Traductor.retar(juego.idiomaActual) + "  "
                + Traductor.clickComparar(juego.idiomaActual)
                + "  [T] " + Traductor.retos(juego.idiomaActual)
                + "  [ESC] " + Traductor.volver(juego.idiomaActual), cx, 28f);
        juego.batch.end();

        manejarInputLista(W, H, panelX, panelY, panelW, btnAddX, xCont, yCont);
    }

    private void renderComparar(float W, float H, float cx) {
        if (amigoComparado == null) {
            modoComparar = false;
            return;
        }
        Usuario yo = juego.gestorUsuarios.getUsuarioActual();
        if (yo == null) return;

        float panelW = Math.min(620f, W - 40f);
        float panelH = 440f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f;
        float colW = panelW / 2f - 30f;
        float xIzq = panelX + 20f;
        float xDer = panelX + panelW / 2f + 10f;
        float yTop = panelY + panelH - 50f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, yTop + 16f, panelW - 40, 3);
        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 20f, panelY + 20f, 120f, 32f);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.compararEstadisticas(juego.idiomaActual), cx, H - 42f);

        fuente.setColor(COLOR_ACENTO);
        tc(fuente, yo.getUsername(), xIzq + colW / 2f, yTop + 6f);
        tc(fuente, amigoComparado.getUsername(), xDer + colW / 2f, yTop + 6f);

        String[][] stats = {
            {"Partidas jugadas", String.valueOf(yo.getEstadisticas().getPartidasJugadas()), String.valueOf(amigoComparado.getEstadisticas().getPartidasJugadas())},
            {"Nivel maximo", String.valueOf(yo.getNivelMaxDesbloqueado()), String.valueOf(amigoComparado.getNivelMaxDesbloqueado())},
            {"Puntaje total", yo.getEstadisticas().getPuntajeTotal() + " pts", amigoComparado.getEstadisticas().getPuntajeTotal() + " pts"},
            {"Mejor puntaje", yo.getEstadisticas().getMejorPuntaje() + " pts", amigoComparado.getEstadisticas().getMejorPuntaje() + " pts"},
            {"Niveles completados", String.valueOf(yo.getEstadisticas().getNivelesCompletados()), String.valueOf(amigoComparado.getEstadisticas().getNivelesCompletados())},
            {"Tiempo total", formatTiempo(yo.getEstadisticas().getTiempoTotalJugado()), formatTiempo(amigoComparado.getEstadisticas().getTiempoTotalJugado())},
        };

        float yc = yTop - 40f;
        for (String[] s : stats) {
            if (yc < panelY + 50f) break;
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteSmall, s[0], cx, yc);
            fuente.setColor(COLOR_TEXTO);
            tc(fuente, s[1], xIzq + colW / 2f, yc - 20f);
            tc(fuente, s[2], xDer + colW / 2f, yc - 20f);
            yc -= 48f;
        }

        fuente.setColor(COLOR_TEXTO);
        fuente.draw(juego.batch, "< " + Traductor.volver(juego.idiomaActual), panelX + 35f, panelY + 40f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual) + " a lista de amigos", cx, 28f);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            if (mx >= panelX + 20f && mx <= panelX + 140f && my >= panelY + 20f && my <= panelY + 52f) {
                modoComparar = false;
                amigoComparado = null;
            }
        }
    }

    private void manejarInputLista(float W, float H, float panelX, float panelY, float panelW, float btnAddX, float xCont, float yCont) {
        float mx = Gdx.input.getX(), my = H - Gdx.input.getY();

        if (modoInput) {
            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0 + i)) {
                    inputBuffer += (char)('0' + i);
                }
            }
            for (char c = 'A'; c <= 'Z'; c++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(String.valueOf(c)))) {
                    inputBuffer += c;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) inputBuffer += ".";
            if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) inputBuffer += "-";
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && inputBuffer.length() > 0) {
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (!inputBuffer.isEmpty()) {
                    boolean ok = juego.gestorUsuarios.enviarSolicitudAmistad(inputBuffer);
                    mensaje = ok ? Traductor.solicitudEnviada(juego.idiomaActual) + ": " + inputBuffer.toUpperCase() : Traductor.usuarioNoEncontrado(juego.idiomaActual);
                    mensajeFrames = 180;
                    mensajeExito = ok;
                    inputBuffer = "";
                }
                modoInput = false;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                inputBuffer = "";
                modoInput = false;
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaPerfil(juego));
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && amigoSeleccionado >= 0 && amigoSeleccionado < amigos.size()) {
            usernameReto = amigos.get(amigoSeleccionado).getUsername();
            nivelReto = 1;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && amigoSeleccionado >= 0 && amigoSeleccionado < amigos.size()) {
            if (seleccionComparar == amigoSeleccionado) {
                amigoComparado = amigos.get(amigoSeleccionado);
                modoComparar = true;
            } else {
                seleccionComparar = amigoSeleccionado;
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            modoRetos = true;
            retosPendientes = juego.gestorUsuarios.obtenerRetosPendientes();
            return;
        }

        if (frameDelay > 0) return;
        if (!Gdx.input.justTouched()) return;

        float btnBw = (panelW - 60f) / 3f;
        if (mx >= panelX + 15f && mx <= panelX + 15f + btnBw && my >= panelY + 14f && my <= panelY + 54f) {
            juego.setScreen(new PantallaPerfil(juego));
            return;
        }

        if (mx >= panelX + 20f + btnBw && mx <= panelX + 20f + btnBw * 2f && my >= panelY + 14f && my <= panelY + 54f) {
            modoSolicitudes = true;
            solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
            return;
        }

        if (mx >= btnAddX && mx <= btnAddX + btnBw && my >= panelY + 14f && my <= panelY + 54f) {
            modoInput = true;
            inputBuffer = "";
            mensaje = "";
            return;
        }

        float filaH = 38f;
        float colC = xCont + panelW - 60f;
        float colR = xCont + panelW - 95f;
        float colX = xCont + panelW - 130f;
        float btnHalf = 15f;
        for (int i = 0; i < amigos.size(); i++) {
            float fy = yCont - i * filaH;
            if (fy < panelY + 60f) break;

            if (mx >= colC - btnHalf && mx <= colC + btnHalf && my >= fy && my <= fy + filaH) {
                if (seleccionComparar == i) {
                    amigoComparado = amigos.get(i);
                    modoComparar = true;
                } else {
                    seleccionComparar = i;
                }
                return;
            }

            if (mx >= colR - btnHalf && mx <= colR + btnHalf && my >= fy && my <= fy + filaH) {
                usernameReto = amigos.get(i).getUsername();
                nivelReto = 1;
                return;
            }

            if (mx >= colX - btnHalf && mx <= colX + btnHalf && my >= fy && my <= fy + filaH) {
                String uname = amigos.get(i).getUsername();
                juego.gestorUsuarios.eliminarAmigo(uname);
                mensaje = Traductor.amigoEliminado(juego.idiomaActual) + ": " + uname;
                mensajeFrames = 180;
                mensajeExito = false;
                recargarAmigos();
                return;
            }

            if (my >= fy && my <= fy + filaH) {
                amigoSeleccionado = i;
                return;
            }
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



    private Pixmap crearPixmapDesdeArchivo(String ruta) {
        try {
            Pixmap px = new Pixmap(Gdx.files.absolute(ruta));
            return px;
        } catch (Exception e) {
        }        try {
            java.io.File imgFile = new java.io.File(ruta);
            java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(imgFile);
            if (bi != null) {
                int w = bi.getWidth(), h = bi.getHeight();
                Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int argb = bi.getRGB(x, y);
                        int a = (argb >> 24) & 0xff;
                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;
                        int rgba = (r << 24) | (g << 16) | (b << 8) | a;
                        px.drawPixel(x, y, rgba);
                    }
                }
                return px;
            }
        } catch (Exception e) {
        }
        try {
            java.awt.Image awtImg = java.awt.Toolkit.getDefaultToolkit().createImage(ruta);
            java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(awtImg, 0, 0, -1, -1, true);
            pg.grabPixels();
            int w = pg.getWidth(), h = pg.getHeight();
            if (w <= 0 || h <= 0) return null;
            int[] pixels = (int[]) pg.getPixels();
            Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int argb = pixels[y * w + x];
                    int a = (argb >> 24) & 0xff;
                    int r = (argb >> 16) & 0xff;
                    int g = (argb >> 8) & 0xff;
                    int b = argb & 0xff;
                    int rgba = (r << 24) | (g << 16) | (b << 8) | a;
                    px.drawPixel(x, y, rgba);
                }
            }
            return px;
        } catch (Exception e) {
            System.err.println("crearPixmap error (todos fallaron): " + e.getMessage());
            return null;
        }
    }

    private Texture cargarTexturaCircular(String ruta, float radius) {
        try {
            java.io.File imgFile = new java.io.File(ruta);
            if (!imgFile.exists()) return null;
            Pixmap full = crearPixmapDesdeArchivo(ruta);
            if (full == null) return null;
            int w = full.getWidth(), h = full.getHeight();
            int ri = (int) radius, diam = ri * 2;
            Pixmap dst = new Pixmap(diam, diam, Pixmap.Format.RGBA8888);
            dst.setColor(0, 0, 0, 0);
            dst.fill();
            for (int py = 0; py < diam; py++) {
                for (int px = 0; px < diam; px++) {
                    float dx = px - ri, dy = py - ri;
                    if (dx * dx + dy * dy <= ri * ri) {
                        int sx = px * w / diam;
                        int sy = py * h / diam;
                        if (sx >= 0 && sx < w && sy >= 0 && sy < h) {
                            dst.drawPixel(px, py, full.getPixel(sx, sy));
                        }
                    }
                }
            }
            full.dispose();
            Texture tex = new Texture(dst);
            dst.dispose();
            return tex;
        } catch (Exception e) {
            System.err.println("cargarTexturaCircular error: " + e.getMessage());
            return null;
        }
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    @Override
    public void dispose() {
        if (fuente != null) fuente.dispose();
        if (fuenteGrande != null) fuenteGrande.dispose();
        if (fuenteSmall != null) fuenteSmall.dispose();
        for (Texture t : friendAvatars) {
            if (t != null) t.dispose();
        }
    }
}
