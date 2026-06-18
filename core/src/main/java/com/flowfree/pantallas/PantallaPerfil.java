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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.flowfree.FlowFreeGame;
import com.flowfree.datos.Traductor;
import com.flowfree.modelo.Estadisticas;
import com.flowfree.modelo.Usuario;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        new Color(1.00f, 0.84f, 0.00f, 1f),};

    private int avatarIdx = 0;
    private String mensajeAvatar = "";
    private int mensajeFrames = 0;
    private Texture avatarTexture = null;
    private String avatarPath = "";
    private boolean modoAvataresPreset = false;
    private String[] presetAvatars = new String[0];
    private Texture[] presetTextures = new Texture[0];
    private String errorCarga = "";

    public PantallaPerfil(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuente = crearFuente(19);
        fuenteGrande = crearFuente(34);
        fuenteSmall = crearFuente(14);
        layout = new GlyphLayout();
        mensajeAvatar = "";
        mensajeFrames = 0;
        avatarTexture = null;
        avatarIdx = 0;
        modoAvataresPreset = false;
        cargarPresetAvatars();
        cargarAvatarUsuario();
    }

    private void cargarAvatarUsuario() {
        Usuario u = juego.gestorUsuarios.getUsuarioActual();
        if (u == null) {
            return;
        }
        String ruta = u.getAvatarRuta();
        if (ruta == null || ruta.isEmpty()) {
            avatarIdx = 0;
            avatarPath = "";
            return;
        }
        avatarPath = ruta;
        avatarTexture = cargarTexturaCircular(ruta, 40f);
        if (avatarTexture != null) {
            return;
        }
        try {
            avatarIdx = Integer.parseInt(ruta);
        } catch (NumberFormatException e) {
            avatarIdx = 0;
        }
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

        float panelW = Math.min(540f, W - 40f);
        float panelH = 520f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        juego.shapeRenderer.rect(panelX + 4, panelY - 4, panelW, panelH);

        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        float avR = 40f;
        float avX = cx;
        float avY = panelY + panelH - 90f;

        if (avatarTexture != null) {
            juego.shapeRenderer.setColor(COLOR_BORDE);
            juego.shapeRenderer.circle(avX, avY, avR + 3);
            juego.shapeRenderer.end();
            juego.batch.begin();
            juego.batch.draw(avatarTexture, avX - avR, avY - avR, avR * 2, avR * 2);
            juego.batch.end();
            juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        } else {
            int colorIdx = avatarIdx >= 0 ? avatarIdx % COLORES_AVATAR.length : 0;
            Color ac = COLORES_AVATAR[colorIdx];
            juego.shapeRenderer.setColor(COLOR_BORDE);
            juego.shapeRenderer.circle(avX, avY, avR + 3);
            juego.shapeRenderer.setColor(ac);
            juego.shapeRenderer.circle(avX, avY, avR);
            juego.shapeRenderer.setColor(new Color(ac.r * 0.3f, ac.g * 0.3f, ac.b * 0.3f, 1f));
            juego.shapeRenderer.circle(avX, avY, avR - 5);
        }

        float cardW = (panelW - 60f) / 2f, cardH = 64f;
        float cardY1 = panelY + 195f;
        float cardY2 = panelY + 120f;
        float cardX1 = panelX + 20f, cardX2 = panelX + 30f + cardW;
        tarjeta(cardX1, cardY1, cardW, cardH, COLOR_ACENTO);
        tarjeta(cardX2, cardY1, cardW, cardH, new Color(0.25f, 0.55f, 0.90f, 1f));
        tarjeta(cardX1, cardY2, cardW, cardH, new Color(0.90f, 0.65f, 0.10f, 1f));
        tarjeta(cardX2, cardY2, cardW, cardH, new Color(0.55f, 0.10f, 0.80f, 1f));

        float btnW = (panelW - 60f) / 3f, btnH = 40f, btnY = panelY + 14f;
        juego.shapeRenderer.setColor(COLOR_BOTON);
        juego.shapeRenderer.rect(panelX + 20f, btnY, btnW, btnH);
        juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.40f, 1f));
        juego.shapeRenderer.rect(panelX + 30f + btnW, btnY, btnW, btnH);
        juego.shapeRenderer.setColor(new Color(0.70f, 0.30f, 0.70f, 1f));
        juego.shapeRenderer.rect(panelX + 40f + btnW * 2f, btnY, btnW, btnH);

        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_TEXTO);
        tc(fuenteGrande, Traductor.miPerfilTitulo(juego.idiomaActual), cx, H - 40f);

        if (avatarTexture == null) {
            String ini = u.getNombreCompleto().isEmpty() ? "?"
                    : String.valueOf(u.getNombreCompleto().charAt(0)).toUpperCase();
            fuenteGrande.setColor(Color.WHITE);
            tc(fuenteGrande, ini, avX, avY + 14f);
        }

        float nombreY = avY - avR - 22f;
        float userY = nombreY - 24f;
        float regY = userY - 22f;
        float sesionY = regY - 22f;
        float clickAvatarY = sesionY - 22f;

        fuente.setColor(COLOR_TEXTO);
        tc(fuente, u.getNombreCompleto(), cx, nombreY);

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "@" + u.getUsername(), cx, userY);

        String reg = u.getFechaRegistro() != null ? u.getFechaRegistro().format(FMT) : "-";
        tc(fuenteSmall, Traductor.registrado(juego.idiomaActual) + ": " + reg, cx, regY);

        String ses = u.getUltimaSesion() != null ? u.getUltimaSesion().format(FMT) : "-";
        tc(fuenteSmall, Traductor.ultimaSesion(juego.idiomaActual) + ": " + ses, cx, sesionY);

        fuenteSmall.setColor(new Color(0.55f, 0.55f, 0.65f, 1f));
        tc(fuenteSmall, Traductor.clickAvatar(juego.idiomaActual), cx, clickAvatarY);

        tarjetaTexto(cardX1, cardY1, cardW, String.valueOf(u.getNivelMaxDesbloqueado()),
                Traductor.nivelAlcanzado(juego.idiomaActual));
        tarjetaTexto(cardX2, cardY1, cardW, String.valueOf(stats.getPartidasJugadas()),
                Traductor.partidasJugadas(juego.idiomaActual));
        tarjetaTexto(cardX1, cardY2, cardW, stats.getMejorPuntaje() + " pts",
                Traductor.mejorPuntaje(juego.idiomaActual));
        tarjetaTexto(cardX2, cardY2, cardW, stats.getPuntajeTotal() + " pts",
                Traductor.puntajeTotal(juego.idiomaActual));

        float btnMidY = btnY + btnH / 2f + 7f;
        fuente.setColor(COLOR_TEXTO);
        tc(fuente, "< " + Traductor.volver(juego.idiomaActual), panelX + 20f + btnW / 2f, btnMidY);
        fuente.setColor(Color.WHITE);
        tc(fuente, Traductor.preferencias(juego.idiomaActual), panelX + 30f + btnW + btnW / 2f, btnMidY);
        tc(fuente, Traductor.amigos(juego.idiomaActual), panelX + 40f + btnW * 2f + btnW / 2f, btnMidY);

        if (mensajeFrames > 0) {
            mensajeFrames--;
            if (mensajeFrames == 0) mensajeAvatar = "";
        }
        if (mensajeFrames > 0 && !mensajeAvatar.isEmpty()) {
            fuenteSmall.setColor(COLOR_EXITO);
            tc(fuenteSmall, mensajeAvatar, cx, panelY + 135f);
        }

        juego.batch.end();

        float desacW = panelW - 40f, desacH = 38f;
        float desacX = panelX + 20f, desacY = panelY + 65f;
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (u.isActivo()) {
            juego.shapeRenderer.setColor(new Color(0.70f, 0.20f, 0.20f, 1f));
        } else {
            juego.shapeRenderer.setColor(new Color(0.20f, 0.70f, 0.30f, 1f));
        }
        juego.shapeRenderer.rect(desacX, desacY, desacW, desacH);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteSmall.setColor(Color.WHITE);
        String btnTexto = u.isActivo() ? Traductor.desactivarCuenta(juego.idiomaActual)
                : Traductor.reactivarCuenta(juego.idiomaActual);
        tc(fuenteSmall, btnTexto, cx, desacY + desacH / 2f + 6f);

        if (!u.isActivo()) {
            fuenteSmall.setColor(new Color(1f, 0.8f, 0.2f, 1f));
            tc(fuenteSmall, Traductor.cuentaInactiva(juego.idiomaActual), cx, desacY + desacH + 20f);
        }

        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] " + Traductor.volver(juego.idiomaActual) + " al menu", cx, 28f);

        juego.batch.end();

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX(), my = H - Gdx.input.getY();

            float dx = mx - avX, dy = my - avY;
            if (dx * dx + dy * dy <= (avR + 5) * (avR + 5)) {
                if (presetAvatars.length > 0) {
                    modoAvataresPreset = true;
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
            float desacW2 = panelW - 40f, desacH2 = 38f;
            float desacX2 = panelX + 20f, desacY2 = panelY + 65f;
            if (mx >= desacX2 && mx <= desacX2 + desacW2 && my >= desacY2 && my <= desacY2 + desacH2) {
                if (u.isActivo()) {
                    juego.detenerMusica();
                    if (juego.gestorUsuarios.desactivarCuenta()) {
                        juego.setScreen(new PantallaLogin(juego));
                        return;
                    }
                } else {
                    if (juego.gestorUsuarios.reactivarCuenta()) {
                        mensajeAvatar = Traductor.cuentaInactiva(juego.idiomaActual) + " - "
                                + (juego.idiomaActual == com.flowfree.enums.Idioma.ES ? "Reactiva" : "Active");
                        mensajeFrames = 180;
                    }
                }
            }
        }
    }

    private void renderPresetSelector(float W, float H, float cx) {
        int cols = Math.min(4, presetAvatars.length);
        int rows = (int) Math.ceil((double) presetAvatars.length / cols);
        float cS = 72f, gap = 14f;
        float gridW = cols * cS + (cols - 1) * gap;
        float gridH = rows * cS + (rows - 1) * gap + 90f;
        float gx = cx - gridW / 2f;
        float gy = H / 2f + gridH / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(0f, 0f, 0f, 0.80f);
        juego.shapeRenderer.rect(0, 0, W, H);
        juego.shapeRenderer.setColor(0.13f, 0.13f, 0.20f, 1f);
        juego.shapeRenderer.rect(gx - 24f, gy - gridH - 24f, gridW + 48f, gridH + 48f);
        juego.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        juego.batch.begin();
        fuente.setColor(Color.WHITE);
        tc(fuente, Traductor.seleccionaAvatar(juego.idiomaActual), cx, gy + 8f);
        fuenteSmall.setColor(COLOR_TEXTO_GRIS);
        tc(fuenteSmall, "[ESC] Cancelar", cx, gy - 14f);

        for (int i = 0; i < presetAvatars.length; i++) {
            int col = i % cols, row = i / cols;
            float ix = gx + col * (cS + gap);
            float iy = gy - 36f - row * (cS + gap) - cS;
            if (presetTextures[i] != null) {
                juego.batch.draw(presetTextures[i], ix, iy, cS, cS);
            } else {
                juego.batch.end();
                juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                Color ac = COLORES_AVATAR[i % COLORES_AVATAR.length];
                juego.shapeRenderer.setColor(ac);
                juego.shapeRenderer.ellipse(ix, iy, cS, cS);
                juego.shapeRenderer.end();
                juego.batch.begin();
            }
        }

        float discoY = gy - gridH - 10f;
        tc(fuenteSmall, Traductor.elegirDisco(juego.idiomaActual), cx, discoY);

        if (!errorCarga.isEmpty()) {
            fuenteSmall.setColor(1f, 0.3f, 0.3f, 1f);
            tc(fuenteSmall, errorCarga, cx, discoY - 18f);
        }

        juego.batch.end();
        manejarInputPreset(gx, gy, cols, cS, gap, W, H);
    }

    private void manejarInputPreset(float gx, float gy, int cols,
            float cS, float gap, float W, float H) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            modoAvataresPreset = false;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            modoAvataresPreset = false;
            Usuario u = juego.gestorUsuarios.getUsuarioActual();
            if (u != null) seleccionarAvatarArchivo(u);
            return;
        }
        if (!Gdx.input.justTouched()) {
            return;
        }
        float mx = Gdx.input.getX(), my = H - Gdx.input.getY();

        for (int i = 0; i < presetAvatars.length; i++) {
            int col = i % cols, row = i / cols;
            float ix = gx + col * (cS + gap);
            float iy = gy - 36f - row * (cS + gap) - cS;
            if (mx >= ix && mx <= ix + cS && my >= iy && my <= iy + cS) {
                try {
                    Usuario u = juego.gestorUsuarios.getUsuarioActual();
                    if (u == null) { modoAvataresPreset = false; return; }
                    File srcFile = new File(presetAvatars[i]);
                    String avatarDir = "assets/datos/usuarios/" + u.getUsername() + "/Imagen de perfil";
                    new File(avatarDir).mkdirs();
                    String nombreBase = srcFile.getName().replaceFirst("\\.[^.]+$", "");
                    String ext = srcFile.getName().substring(srcFile.getName().lastIndexOf('.'));
                    File dst = new File(avatarDir, nombreBase + "_" + System.currentTimeMillis() + ext);
                    java.nio.file.Files.copy(srcFile.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    avatarPath = dst.getAbsolutePath();
                    if (avatarTexture != null) {
                        avatarTexture.dispose();
                        avatarTexture = null;
                    }
                    avatarTexture = cargarTexturaCircular(avatarPath, 40f);
                    if (avatarTexture != null) {
                        u.setAvatarRuta(avatarPath);
                        juego.gestorUsuarios.actualizarUsuario(u);
                        mensajeAvatar = Traductor.avatarActualizado(juego.idiomaActual);
                        mensajeFrames = 180;
                    } else {
                        mensajeAvatar = "Error al cargar avatar";
                    }
                } catch (Exception e) {
                    mensajeAvatar = "Error al copiar avatar";
                }
                modoAvataresPreset = false;
                return;
            }
        }

        int rows = (int) Math.ceil((double) presetAvatars.length / cols);
        float gridH = rows * cS + (rows - 1) * gap + 90f;
        float discoY = gy - gridH - 10f;
        float cx = W / 2f;
        if (my >= discoY - 18f && my <= discoY + 4f && mx >= cx - 120f && mx <= cx + 120f) {
            modoAvataresPreset = false;
            Usuario u = juego.gestorUsuarios.getUsuarioActual();
            if (u != null) seleccionarAvatarArchivo(u);
            return;
        }

        modoAvataresPreset = false;
    }

    private void cargarPresetAvatars() {
        File dir = null;
        for (String p : new String[]{"avatars", "assets/avatars"}) {
            File d = new File(p);
            if (d.isDirectory()) {
                dir = d;
                break;
            }
        }
        if (dir == null) {
            presetAvatars = new String[0];
            presetTextures = new Texture[0];
            return;
        }
        File[] files = dir.listFiles((d, name) -> {
            String l = name.toLowerCase();
            return l.endsWith(".png") || l.endsWith(".jpg") || l.endsWith(".jpeg");
        });
        if (files == null || files.length == 0) {
            presetAvatars = new String[0];
            presetTextures = new Texture[0];
            return;
        }
        presetAvatars = new String[files.length];
        presetTextures = new Texture[files.length];
        for (int i = 0; i < files.length; i++) {
            presetAvatars[i] = files[i].getAbsolutePath();
            presetTextures[i] = cargarTexturaCircular(presetAvatars[i], 36f);
        }
    }

    private void seleccionarAvatarArchivo(Usuario u) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar avatar");
        fc.setFileFilter(new FileNameExtensionFilter("Imagenes (PNG, JPG)", "png", "jpg", "jpeg"));
        fc.setAcceptAllFileFilterUsed(false);
        int res = fc.showOpenDialog(null);
        if (res != JFileChooser.APPROVE_OPTION) {
            mensajeAvatar = "";
            mensajeFrames = 0;
            return;
        }
        File src = fc.getSelectedFile();
        String avatarDir = "assets/datos/usuarios/" + u.getUsername() + "/Imagen de perfil";
        new File(avatarDir).mkdirs();
        String nombreBase = src.getName().replaceFirst("\\.[^.]+$", "");
        String ext = src.getName().substring(src.getName().lastIndexOf('.'));
        File dst = new File(avatarDir, nombreBase + "_" + System.currentTimeMillis() + ext);
        try {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
            u.setAvatarRuta(dst.getAbsolutePath());
            juego.gestorUsuarios.actualizarUsuario(u);
            if (avatarTexture != null) {
                avatarTexture.dispose();
                avatarTexture = null;
            }
            avatarTexture = cargarTexturaCircular(dst.getAbsolutePath(), 40f);
            avatarIdx = -1;
            mensajeAvatar = Traductor.avatarActualizado(juego.idiomaActual);
            mensajeFrames = 180;
        } catch (IOException ex) {
            mensajeAvatar = "Error al copiar archivo";
        }
    }


    private Pixmap crearPixmapDesdeArchivo(String ruta) {
        try {
            Pixmap px = new Pixmap(Gdx.files.absolute(ruta));
            System.out.println("crearPixmapDesdeArchivo: native OK " + px.getWidth() + "x" + px.getHeight());
            return px;
        } catch (Exception e) {
            System.out.println("crearPixmapDesdeArchivo: native falló (" + e.getClass().getSimpleName()
                    + ": " + e.getMessage() + "), probando ImageIO...");
        }
        try {
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
                System.out.println("crearPixmapDesdeArchivo: ImageIO OK " + w + "x" + h);
                return px;
            }
            System.out.println("crearPixmapDesdeArchivo: ImageIO devolvió null, probando AWT...");
        } catch (Exception e) {
            System.out.println("crearPixmapDesdeArchivo: ImageIO falló (" + e.getClass().getSimpleName()
                    + ": " + e.getMessage() + "), probando AWT...");
        }
        try {
            java.awt.Image awtImg = java.awt.Toolkit.getDefaultToolkit().createImage(ruta);
            java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(awtImg, 0, 0, -1, -1, true);
            pg.grabPixels();
            int w = pg.getWidth(), h = pg.getHeight();
            if (w <= 0 || h <= 0) {
                errorCarga = "AWT PixelGrabber devolvió dimensiones inválidas: " + w + "x" + h;
                return null;
            }
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
            System.out.println("crearPixmapDesdeArchivo: AWT OK " + w + "x" + h);
            return px;
        } catch (Exception e) {
            errorCarga = "crearPixmap error (todos los métodos fallaron): "
                    + e.getClass().getSimpleName() + ": " + e.getMessage();
            System.err.println(errorCarga);
            return null;
        }
    }

    /** Load an image file, crop it to a circle at the Pixmap level, and return the circular Texture.
     *  Uses crearPixmapDesdeArchivo which tries native decoder then ImageIO fallback. */
    private Texture cargarTexturaCircular(String ruta, float radius) {
        try {
            java.io.File imgFile = new java.io.File(ruta);
            if (!imgFile.exists()) {
                errorCarga = "No existe: " + ruta;
                System.err.println(errorCarga);
                return null;
            }
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
            System.out.println("cargarTexturaCircular OK -> Texture@" + Integer.toHexString(tex.hashCode()));
            return tex;
        } catch (Exception e) {
            errorCarga = "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage();
            System.err.println("cargarTexturaCircular: " + errorCarga);
            return null;
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
        if (avatarTexture != null) {
            avatarTexture.dispose();
        }
        if (presetTextures != null) {
            for (Texture t : presetTextures) {
                if (t != null) {
                    t.dispose();
                }
            }
        }
    }
}
