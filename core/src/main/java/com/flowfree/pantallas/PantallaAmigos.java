package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.FlowFreeGame;
import com.flowfree.modelo.Reto;
import com.flowfree.modelo.Usuario;
import java.util.List;
import com.flowfree.datos.Traductor;

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
    private boolean modoComparar = false;
    private Usuario amigoComparado = null;
    private List<String> solicitudesPendientes;
    private int nivelReto = -1;
    private String usernameReto = "";

    private static final Color COLOR_ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color COLOR_PLATA = new Color(0.80f, 0.80f, 0.80f, 1f);

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
        modoComparar = false;
        seleccionComparar = -1;
        amigoComparado = null;
        mensaje = "";
        nivelReto = -1;
        usernameReto = "";
        recargarAmigos();
        solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
    }

    private void recargarAmigos() {
        amigos = juego.gestorUsuarios.obtenerAmigos();
    }

    @Override
    public void render(float delta) {
        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        limpiarPantalla();

        if (modoComparar) {
            renderComparar(W, H, cx);
        } else if (modoSolicitudes) {
            renderSolicitudes(W, H, cx);
        } else if (nivelReto > 0) {
            renderReto(W, H, cx);
        } else {
            renderLista(W, H, cx);
        }
    }

    private void renderSolicitudes(float W, float H, float cx) {
        float panelW = Math.min(500f, W - 40f);
        float panelH = Math.min(400f, H - 80f);
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f;
        float filaY0 = panelY + panelH - 60f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.solicitudesPendientes(juego.idiomaActual), cx, H - 42f);

        if (solicitudesPendientes.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.noSolicitudes(juego.idiomaActual), cx, filaY0 - 40f);
        } else {
            for (int i = 0; i < solicitudesPendientes.size(); i++) {
                float fy = filaY0 - i * 50f;
                if (fy < panelY + 50f) break;

                if (i % 2 == 0) {
                    juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    juego.shapeRenderer.setColor(new Color(0.11f, 0.11f, 0.17f, 1f));
                    juego.shapeRenderer.rect(panelX + 10, fy - 15f, panelW - 20, 44f);
                    juego.shapeRenderer.end();
                }

                fuente.setColor(COLOR_TEXTO);
                tc(fuente, solicitudesPendientes.get(i), cx - 80f, fy + 7f);

                fuenteSmall.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
                tc(fuenteSmall, "[" + Traductor.aceptar(juego.idiomaActual) + "]", cx + 40f, fy + 7f);
                fuenteSmall.setColor(new Color(0.90f, 0.25f, 0.25f, 1f));
                tc(fuenteSmall, "[" + Traductor.rechazar(juego.idiomaActual) + "]", cx + 120f, fy + 7f);
            }
        }

        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            modoSolicitudes = false;
            return;
        }
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();
            for (int i = 0; i < solicitudesPendientes.size(); i++) {
                float fy = filaY0 - i * 50f;
                if (fy < panelY + 50f) break;
                if (my >= fy - 15f && my <= fy + 25f
                        && mx >= cx + 10f && mx <= cx + 80f) {
                    boolean ok = juego.gestorUsuarios.aceptarSolicitud(solicitudesPendientes.get(i));
                    mensaje = ok ? Traductor.solicitudAceptada(juego.idiomaActual) + ": " + solicitudesPendientes.get(i) : Traductor.errorAceptar(juego.idiomaActual);
                    mensajeExito = ok;
                    recargarAmigos();
                    solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
                    if (solicitudesPendientes.isEmpty()) modoSolicitudes = false;
                    return;
                }
                if (my >= fy - 15f && my <= fy + 25f
                        && mx >= cx + 90f && mx <= cx + 160f) {
                    juego.gestorUsuarios.rechazarSolicitud(solicitudesPendientes.get(i));
                    mensaje = Traductor.solicitudRechazada(juego.idiomaActual) + ": " + solicitudesPendientes.get(i);
                    mensajeExito = true;
                    solicitudesPendientes = juego.gestorUsuarios.obtenerSolicitudesPendientes();
                    if (solicitudesPendientes.isEmpty()) modoSolicitudes = false;
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

        fuenteSmall.setColor(COLOR_TEXTO);
        tc(fuenteSmall, Traductor.retoInst(juego.idiomaActual), cx, 40f);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) nivelReto = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) nivelReto = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) nivelReto = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) nivelReto = 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) nivelReto = 5;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            nivelReto = -1;
            usernameReto = "";
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && nivelReto >= 1 && nivelReto <= 5) {
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
        float colUser = xCont + 10f;
        float colNivel = xCont + panelW - 220f;
        float colPts = xCont + panelW - 120f;
        float colAccion = xCont + panelW - 60f;
        tc(fuenteSmall, Traductor.amigo(juego.idiomaActual), colUser, yCont + 6f);
        tc(fuenteSmall, Traductor.nivelAbr(juego.idiomaActual), colNivel, yCont + 6f);
        tc(fuenteSmall, Traductor.puntos(juego.idiomaActual), colPts, yCont + 6f);
        tc(fuenteSmall, Traductor.comp(juego.idiomaActual), colAccion, yCont + 6f);

        yCont -= 30f;

        if (amigos.isEmpty()) {
            fuente.setColor(COLOR_TEXTO_GRIS);
            tc(fuente, Traductor.noAmigos(juego.idiomaActual), cx, yCont - 60f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteSmall, Traductor.usarAgregar(juego.idiomaActual), cx, yCont - 90f);
        } else {
            float filaH = 38f;
            for (int i = 0; i < amigos.size(); i++) {
                Usuario a = amigos.get(i);
                float fy = yCont - i * filaH;
                if (fy < panelY + 60f) break;

                if (i % 2 == 0) {
                    juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    juego.shapeRenderer.setColor(new Color(0.11f, 0.11f, 0.17f, 1f));
                    juego.shapeRenderer.rect(panelX + 10, fy, panelW - 20, filaH);
                    juego.shapeRenderer.end();
                }

                Color bar = i == 0 ? COLOR_ORO : i == 1 ? COLOR_PLATA : COLOR_BORDE;
                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                juego.shapeRenderer.setColor(bar);
                juego.shapeRenderer.rect(panelX + 10, fy, 4f, filaH);
                juego.shapeRenderer.end();

                fuente.setColor(COLOR_TEXTO);
                fuente.draw(juego.batch, a.getUsername(), colUser, fy + filaH / 2f + 8f);
                fuenteSmall.setColor(COLOR_TEXTO_GRIS);
                fuenteSmall.draw(juego.batch, "Nv." + a.getNivelMaxDesbloqueado(), colNivel, fy + filaH / 2f + 8f);
                fuente.setColor(bar);
                fuente.draw(juego.batch, a.getEstadisticas().getPuntajeTotal() + " pts", colPts, fy + filaH / 2f + 8f);

                String cmp = seleccionComparar == i ? "[-]" : "[C]";
                fuenteSmall.setColor(seleccionComparar == i ? COLOR_EXITO : COLOR_ACENTO);
                fuenteSmall.draw(juego.batch, cmp, colAccion, fy + filaH / 2f + 8f);

                fuenteSmall.setColor(new Color(0.90f, 0.65f, 0.10f, 1f));
                fuenteSmall.draw(juego.batch, "[R]", colAccion - 40f, fy + filaH / 2f + 8f);
            }
        }

        if (!mensaje.isEmpty()) {
            fuenteSmall.setColor(mensajeExito ? COLOR_EXITO : COLOR_ERROR);
            tc(fuenteSmall, mensaje, cx, panelY + 60f);
        }

        fuente.setColor(COLOR_TEXTO);
        tc(fuente, "< " + Traductor.volver(juego.idiomaActual), panelX + 15f + btnBaseW / 2f, panelY + 40f);
        fuente.setColor(Color.WHITE);
        tc(fuente, Traductor.solicitudes(juego.idiomaActual) + "(" + solicitudesPendientes.size() + ")", panelX + 20f + btnBaseW + btnBaseW / 2f, panelY + 40f);
        fuente.setColor(new Color(0.05f, 0.05f, 0.10f, 1f));
        tc(fuente, Traductor.agregarAmigo(juego.idiomaActual), btnAddX + btnBaseW / 2f, panelY + 40f);

        if (modoInput) {
            fuenteSmall.setColor(COLOR_TEXTO);
            tc(fuenteSmall, Traductor.usuario(juego.idiomaActual) + ": " + inputBuffer + (System.currentTimeMillis() / 500 % 2 == 0 ? "|" : ""), cx, panelY + 105f);
            fuenteSmall.setColor(COLOR_TEXTO_GRIS);
            tc(fuenteSmall, "[ENTER] Confirmar  [ESC] Cancelar", cx, panelY + 85f);
        }

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, Traductor.clickComparar(juego.idiomaActual) + "  [ESC] " + Traductor.volver(juego.idiomaActual), cx, 28f);
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
        float panelH = 360f;
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
        juego.shapeRenderer.rect(panelX + 20f, panelY + 14f, 140f, 40f);
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
            if (mx >= panelX + 20f && mx <= panelX + 160f && my >= panelY + 14f && my <= panelY + 54f) {
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
        for (int i = 0; i < amigos.size(); i++) {
            float fy = yCont - i * filaH;
            if (fy < panelY + 60f) break;

            float colAccion = xCont + panelW - 90f;
            float colRetar = colAccion - 50f;

            if (mx >= colAccion && mx <= colAccion + 40f && my >= fy && my <= fy + filaH) {
                if (seleccionComparar == i) {
                    amigoComparado = amigos.get(i);
                    modoComparar = true;
                } else {
                    seleccionComparar = i;
                }
                return;
            }

            if (mx >= colRetar && mx <= colRetar + 30f && my >= fy && my <= fy + filaH) {
                usernameReto = amigos.get(i).getUsername();
                nivelReto = 1;
                return;
            }

            if (mx >= panelX + 10f && mx <= panelX + panelW - 10f && my >= fy && my <= fy + filaH) {
                String uname = amigos.get(i).getUsername();
                juego.gestorUsuarios.eliminarAmigo(uname);
                mensaje = Traductor.amigoEliminado(juego.idiomaActual) + ": " + uname;
                mensajeExito = true;
                recargarAmigos();
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

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    @Override
    public void dispose() {
        if (fuente != null) fuente.dispose();
        if (fuenteGrande != null) fuenteGrande.dispose();
        if (fuenteSmall != null) fuenteSmall.dispose();
    }
}
