/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.flowfree.datos.Traductor;
import com.flowfree.FlowFreeGame;
import com.flowfree.modelo.Estadisticas;
import com.flowfree.modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PantallaPerfil extends PantallaBase {

    private BitmapFont fuente, fuenteGrande, fuenteSmall;
    private GlyphLayout layout;

    private static final Color COLOR_ORO = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    private int avatarIdx = 0;
    private String mensajeAvatar = "";
    private Texture avatarTexture = null;
    private String avatarPath = "";
    private boolean modoAvataresPreset = false;
    private String[] presetAvatars;
    private Texture[] presetTextures;

    public PantallaPerfil(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        fuenteSmall = crearFuente(15);
        layout = new GlyphLayout();
        mensajeAvatar = "";
        avatarTexture = null;
        avatarIdx = 0;
        modoAvataresPreset = false;
        cargarPresetAvatars();

        Usuario u = juego.gestorUsuarios.getUsuarioActual();
        if (u != null) {
            String ruta = u.getAvatarRuta();
            if (ruta != null && !ruta.isEmpty()) {
                avatarPath = ruta;
                File f = new File(ruta);
                if (f.exists()) {
                    try {
                        Texture tex = new Texture(ruta);
                        avatarTexture = recortarCircular(tex, 38f);
                        tex.dispose();
                    } catch (Exception ex) {
                        avatarTexture = null;
                    }
                }
                if (avatarTexture == null) {
                    try {
                        avatarIdx = Integer.parseInt(ruta);
                    } catch (NumberFormatException ex) {
                        avatarIdx = 0;
                    }
                }
            } else {
                avatarIdx = 0;
                avatarPath = "";
            }
        }
    }

    private void renderPresetSelector(float W, float H, float cx) {
        int cols = Math.min(4, presetAvatars.length);
        int rows = (int) Math.ceil((double) presetAvatars.length / cols);
        float cellSize = 70f;
        float gap = 12f;
        float gridW = cols * cellSize + (cols - 1) * gap;
        float gridH = rows * cellSize + (rows - 1) * gap + 50f;
        float gx = cx - gridW / 2f;
        float gy = H / 2f + gridH / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f);
        juego.shapeRenderer.rect(gx - 20f, gy - gridH - 20f, gridW + 40f, gridH + 40f);
        juego.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        juego.batch.begin();
        fuente.setColor(Color.WHITE);
        tc(fuente, Traductor.seleccionaAvatar(juego.idiomaActual), cx, gy + 10f);

        fuenteSmall.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
        tc(fuenteSmall, Traductor.clickFueraArchivo(juego.idiomaActual), cx, gy - 15f);

        for (int i = 0; i < presetAvatars.length; i++) {
            int col = i % cols;
            int row = i / cols;
            float ix = gx + col * (cellSize + gap);
            float iy = gy - 30f - row * (cellSize + gap) - cellSize;

            if (presetTextures[i] != null) {
                juego.batch.draw(presetTextures[i], ix, iy, cellSize, cellSize);
            } else {
                float r = ((i * 50 + 100) % 256) / 255f;
                float g = ((i * 80 + 150) % 256) / 255f;
                float b = ((i * 110 + 50) % 256) / 255f;
                juego.batch.end();
                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                juego.shapeRenderer.setColor(r, g, b, 1f);
                juego.shapeRenderer.ellipse(ix, iy, cellSize, cellSize);
                juego.shapeRenderer.end();
                juego.batch.begin();
            }
        }
        juego.batch.end();
        manejarInputPreset(gx, gy, cols, rows, cellSize, gap, W, H);
    }

    private void manejarInputPreset(float gx, float gy, int cols, int rows, float cellSize, float gap, float W, float H) {
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = H - Gdx.input.getY();

            boolean clickedInside = false;
            for (int i = 0; i < presetAvatars.length; i++) {
                int col = i % cols;
                int row = i / cols;
                float ix = gx + col * (cellSize + gap);
                float iy = gy - 30f - row * (cellSize + gap) - cellSize;

                if (mx >= ix && mx <= ix + cellSize && my >= iy && my <= iy + cellSize) {
                    try {
                        avatarTexture = new Texture(presetAvatars[i]);
                        avatarPath = presetAvatars[i];
                        Usuario u = juego.gestorUsuarios.getUsuarioActual();
                        if (u != null) {
                            u.setAvatarRuta(avatarPath);
                            juego.gestorUsuarios.actualizarUsuario(u);
                        }
                        mensajeAvatar = Traductor.avatarActualizado(juego.idiomaActual);
                    } catch (Exception e) {
                        mensajeAvatar = "Error al cargar avatar";
                    }
                    modoAvataresPreset = false;
                    clickedInside = true;
                    break;
                }
            }
            if (!clickedInside) {
                modoAvataresPreset = false;
                seleccionarAvatarArchivo(juego.gestorUsuarios.getUsuarioActual());
            }
        }
    }

    @Override
    public void resize(int w, int h) {
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

        if (modoAvataresPreset && presetAvatars.length > 0) {
            renderPresetSelector(W, H, cx);
            return;
        }

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

        float panelW = Math.min(560f, W - 40f);
        float panelH = 460f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f - 10f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        float avR = 38f, avX = cx, avY = panelY + panelH - 70f;
        boolean tieneImagen = (avatarTexture != null);
        if (tieneImagen) {
            juego.shapeRenderer.setColor(COLOR_BORDE);
            juego.shapeRenderer.circle(avX, avY, avR + 3);
            juego.shapeRenderer.end();
            juego.batch.begin();
            juego.batch.draw(avatarTexture, avX - avR, avY - avR, avR*2, avR*2);
            juego.batch.end();
            juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        } else {
            Color avatarColor = COLORES_AVATAR[avatarIdx % COLORES_AVATAR.length];
            juego.shapeRenderer.setColor(COLOR_BORDE);
            juego.shapeRenderer.circle(avX, avY, avR + 3);
            juego.shapeRenderer.setColor(avatarColor);
            juego.shapeRenderer.circle(avX, avY, avR);
            juego.shapeRenderer.setColor(new Color(avatarColor.r * 0.3f, avatarColor.g * 0.3f, avatarColor.b * 0.3f, 1f));
            juego.shapeRenderer.circle(avX, avY, avR - 4);
        }

        float cardW = (panelW - 60f) / 2f, cardH = 64f;
        float cardY1 = panelY + 170f, cardY2 = panelY + 95f;
        float cardX1 = panelX + 20f, cardX2 = panelX + 30f + cardW;
        tarjeta(cardX1, cardY1, cardW, cardH, COLOR_ACENTO);
        tarjeta(cardX2, cardY1, cardW, cardH, new Color(0.25f, 0.55f, 0.90f, 1f));
        tarjeta(cardX1, cardY2, cardW, cardH, new Color(0.90f, 0.65f, 0.10f, 1f));
        tarjeta(cardX2, cardY2, cardW, cardH, new Color(0.55f, 0.10f, 0.80f, 1f));

        float btnW = (panelW - 60f) / 3f;
        float btnY = panelY + 14f;
        float btnH = 40f;
        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 20f, btnY, btnW, btnH);
        juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
        juego.shapeRenderer.rect(panelX + 30f + btnW, btnY, btnW, btnH);
        juego.shapeRenderer.setColor(new Color(0.70f, 0.30f, 0.70f, 1f));
        juego.shapeRenderer.rect(panelX + 40f + btnW * 2f, btnY, btnW, btnH);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.miPerfilTitulo(juego.idiomaActual), cx, H - 42f);

        if (avatarTexture == null) {
            String ini = u.getNombreCompleto().isEmpty() ? "?"
                    : String.valueOf(u.getNombreCompleto().charAt(0)).toUpperCase();
            fuenteGrande.setColor(Color.WHITE);
            tc(fuenteGrande, ini, avX, avY + 14f);
        }

        fuente.setColor(COLOR_TEXTO);
        tc(fuente, u.getNombreCompleto(), cx, panelY + panelH - 78f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "@" + u.getUsername(), cx, panelY + panelH - 100f);

        String reg = u.getFechaRegistro() != null ? u.getFechaRegistro().format(FMT) : "-";
        tc(fuenteSmall, Traductor.registrado(juego.idiomaActual) + ": " + reg, cx, panelY + panelH - 128f);
        String ses = u.getUltimaSesion() != null ? u.getUltimaSesion().format(FMT) : "-";
        tc(fuenteSmall, Traductor.ultimaSesion(juego.idiomaActual) + ": " + ses, cx, panelY + panelH - 155f);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, Traductor.clickAvatar(juego.idiomaActual), avX, panelY + panelH - 185f);

        tarjetaTexto(cardX1, cardY1, cardW, String.valueOf(u.getNivelMaxDesbloqueado()), Traductor.nivelAlcanzado(juego.idiomaActual));
        tarjetaTexto(cardX2, cardY1, cardW, String.valueOf(stats.getPartidasJugadas()), Traductor.partidasJugadas(juego.idiomaActual));
        tarjetaTexto(cardX1, cardY2, cardW, stats.getMejorPuntaje() + " pts", Traductor.mejorPuntaje(juego.idiomaActual));
        tarjetaTexto(cardX2, cardY2, cardW, stats.getPuntajeTotal() + " pts", Traductor.puntajeTotal(juego.idiomaActual));

        float btnMidY = btnY + btnH / 2f + 8f;
        fuente.setColor(COLOR_TEXTO);
        tc(fuente, "< " + Traductor.volver(juego.idiomaActual), panelX + 20f + btnW / 2f, btnMidY);
        fuente.setColor(Color.WHITE);
        tc(fuente, Traductor.preferencias(juego.idiomaActual), panelX + 30f + btnW + btnW / 2f, btnMidY);
        tc(fuente, Traductor.amigos(juego.idiomaActual), panelX + 40f + btnW * 2f + btnW / 2f, btnMidY);

        if (!mensajeAvatar.isEmpty()) {
            fuenteSmall.setColor(COLOR_EXITO);
            tc(fuenteSmall, mensajeAvatar, cx, panelY + 65f);
        }

        float desacY = panelY + 70f;
        fuenteSmall.setColor(new Color(0.90f, 0.25f, 0.25f, 1f));
        tc(fuenteSmall, Traductor.desactivarCuenta(juego.idiomaActual), cx, desacY);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual) + " al menu", cx, 28f);
        juego.batch.end();

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();

            float dx = mx - avX, dy = my - avY;
            if (dx * dx + dy * dy <= (avR + 5) * (avR + 5)) {
                if (presetAvatars.length > 0) {
                    modoAvataresPreset = !modoAvataresPreset;
                } else {
                    seleccionarAvatarArchivo(u);
                }
                return;
            }

            if (mx >= panelX + 20f && mx <= panelX + 20f + btnW && my >= btnY && my <= btnY + btnH) {
                juego.setScreen(new PantallaMenu(juego));
                return;
            }
            if (mx >= panelX + 30f + btnW && mx <= panelX + 30f + btnW * 2f && my >= btnY && my <= btnY + btnH) {
                juego.setScreen(new PantallaPreferencias(juego));
                return;
            }
            if (mx >= panelX + 40f + btnW * 2f && mx <= panelX + 40f + btnW * 3f && my >= btnY && my <= btnY + btnH) {
                juego.setScreen(new PantallaAmigos(juego));
                return;
            }

            layout.setText(fuenteSmall, Traductor.desactivarCuenta(juego.idiomaActual));
            float tw = layout.width;
            if (my >= desacY - 15f && my <= desacY + 5f
                    && mx >= cx - tw / 2f - 10f && mx <= cx + tw / 2f + 10f) {
                boolean ok = juego.gestorUsuarios.desactivarCuenta();
                if (ok) {
                    juego.setScreen(new PantallaLogin(juego));
                    return;
                }
            }
        }
    }

    private void tarjeta(float x, float y, float w, float h, Color a) {
        juego.shapeRenderer.setColor(new Color(a.r * 0.15f + 0.10f, a.g * 0.15f + 0.10f, a.b * 0.15f + 0.10f, 1f));
        juego.shapeRenderer.rect(x, y, w, h);
        juego.shapeRenderer.setColor(a);
        juego.shapeRenderer.rect(x, y, w, 3f);
    }

    private void tarjetaTexto(float x, float y, float w, String val, String label) {
        float cx = x + w / 2f;
        fuente.setColor(COLOR_TEXTO);
        tc(fuente, val, cx, y + 48f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, label, cx, y + 20f);
    }

    private void tc(BitmapFont f, String t, float cx, float y) {
        layout.setText(f, t);
        f.draw(juego.batch, t, cx - layout.width / 2f, y);
    }

    private void cargarPresetAvatars() {
        File dir = new File("assets/avatars");
        File[] files = dir.listFiles((d, name) ->
                name.toLowerCase().endsWith(".png")
                || name.toLowerCase().endsWith(".jpg")
                || name.toLowerCase().endsWith(".jpeg")
                || name.toLowerCase().endsWith(".gif"));
        if (files == null || files.length == 0) {
            presetAvatars = new String[0];
            presetTextures = new Texture[0];
            return;
        }
        presetAvatars = new String[files.length];
        presetTextures = new Texture[files.length];
        for (int i = 0; i < files.length; i++) {
            presetAvatars[i] = files[i].getAbsolutePath();
            try {
                Texture tex = new Texture(files[i].getAbsolutePath());
                presetTextures[i] = recortarCircular(tex, 32f);
                tex.dispose();
            } catch (Exception e) {
                presetTextures[i] = null;
            }
        }
    }

    private void seleccionarAvatarArchivo(Usuario u) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar avatar");
        fc.setFileFilter(new FileNameExtensionFilter("Imágenes (PNG, JPG, GIF)", "png", "jpg", "jpeg", "gif"));
        fc.setAcceptAllFileFilterUsed(false);
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File src = fc.getSelectedFile();
            String dataDir = "assets/datos/usuarios/" + u.getUsername();
            new File(dataDir).mkdirs();
            File dst = new File(dataDir, "avatar" + src.getName().substring(src.getName().lastIndexOf('.')));
            try {
                Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
                u.setAvatarRuta(dst.getAbsolutePath());
                juego.gestorUsuarios.actualizarUsuario(u);
                if (avatarTexture != null) {
                    avatarTexture.dispose();
                    avatarTexture = null;
                }
                Texture tex = new Texture(dst.getAbsolutePath());
                avatarTexture = recortarCircular(tex, 38f);
                tex.dispose();
                avatarIdx = -1;
                mensajeAvatar = Traductor.avatarActualizado(juego.idiomaActual);
            } catch (IOException ex) {
                mensajeAvatar = "Error al copiar archivo";
            }
        } else {
            mensajeAvatar = "";
        }
    }

    private Texture recortarCircular(Texture tex, float radius) {
        if (!tex.getTextureData().isPrepared()) {
            tex.getTextureData().prepare();
        }
        Pixmap srcPm = tex.getTextureData().consumePixmap();
        int r = (int) radius;
        int diam = r * 2;
        Pixmap dstPm = new Pixmap(diam, diam, Pixmap.Format.RGBA8888);
        dstPm.setColor(0, 0, 0, 0);
        dstPm.fill();
        for (int py = 0; py < diam; py++) {
            for (int px = 0; px < diam; px++) {
                float dx = px - r, dy = py - r;
                if (dx * dx + dy * dy <= r * r) {
                    int sx = srcPm.getWidth() / 2 - r + px;
                    int sy = srcPm.getHeight() / 2 - r + py;
                    if (sx >= 0 && sx < srcPm.getWidth() && sy >= 0 && sy < srcPm.getHeight()) {
                        dstPm.drawPixel(px, py, srcPm.getPixel(sx, sy));
                    }
                }
            }
        }
        Texture result = new Texture(dstPm);
        dstPm.dispose();
        return result;
    }

    @Override
    public void dispose() {
        if (fuente != null) fuente.dispose();
        if (fuenteGrande != null) fuenteGrande.dispose();
        if (fuenteSmall != null) fuenteSmall.dispose();
        if (avatarTexture != null) avatarTexture.dispose();
        if (presetTextures != null) {
            for (Texture t : presetTextures) {
                if (t != null) t.dispose();
            }
        }
    }
}
