/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.flowfree.FlowFreeGame;
import com.flowfree.datos.GestorUsuarios;
import com.flowfree.datos.Traductor;
import com.flowfree.modelo.Reto;
import java.util.List;

public class PantallaLogin extends PantallaBase {

    private Stage stage;
    private Skin skin;

    private TextField tfNombre;
    private TextField tfUsername;
    private TextField tfPassword;
    private TextField tfConfirm;

    private TextButton btnPrincipal;
    private TextButton btnSecundario;
    private TextButton btnTogglePass;
    private boolean passVisible = false;

    private Label lblMensaje;
    private Label lblSubtitulo;

    private boolean modoRegistro = false;

    private BitmapFont fuenteTitulo;
    private GlyphLayout layout;

    private static final Color COLOR_CAMPO_BG = new Color(0.14f, 0.14f, 0.20f, 1f);
    private static final Color COLOR_CAMPO_ACTIVO = new Color(0.18f, 0.18f, 0.28f, 1f);
    private static final Color COLOR_CURSOR = new Color(0.30f, 0.70f, 1.00f, 1f);

    public PantallaLogin(FlowFreeGame juego) {
        super(juego);
    }

    @Override
    public void show() {
        fuenteTitulo = crearFuente(34);
        layout = new GlyphLayout();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = construirSkin();

        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    if (modoRegistro) {
                        ejecutarRegistro();
                    } else {
                        ejecutarLogin();
                    }
                    return true;
                }
                return false;
            }
        });

        construirUI();
    }

    private Skin construirSkin() {
        Skin s = new Skin();

        BitmapFont fLabel = crearFuente(16);
        BitmapFont fCampo = crearFuente(17);
        BitmapFont fBoton = crearFuente(16);
        BitmapFont fMensaje = crearFuente(15);

        s.add("default", fLabel);
        s.add("campo", fCampo);
        s.add("boton", fBoton);
        s.add("mensaje", fMensaje);

        s.add("blanco", crearPixmapTexture(1, 1, Color.WHITE));
        s.add("negro", crearPixmapTexture(1, 1, Color.BLACK));
        s.add("campoBg", crearPixmapTexture(1, 1, COLOR_CAMPO_BG));
        s.add("acento", crearPixmapTexture(1, 1, COLOR_ACENTO));
        s.add("botonBg", crearPixmapTexture(1, 1, COLOR_BOTON));
        s.add("botonAcc", crearPixmapTexture(1, 1, COLOR_ACENTO));
        s.add("error", crearPixmapTexture(1, 1, new Color(1f, 0.2f, 0.2f, 1f)));
        s.add("exito", crearPixmapTexture(1, 1, COLOR_EXITO));

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = s.getFont("campo");
        tfStyle.fontColor = Color.WHITE;
        tfStyle.background = s.newDrawable("campoBg");
        tfStyle.focusedBackground = s.newDrawable("campoBg",
                new Color(0.22f, 0.22f, 0.32f, 1f));
        tfStyle.cursor = s.newDrawable("acento", 1f, 1f, 1f, 1f);
        tfStyle.selection = s.newDrawable("acento", 0.3f, 0.6f, 1f, 0.4f);
        tfStyle.messageFontColor = COLOR_TEXTO_GRIS;
        tfStyle.messageFont = s.getFont("campo");
        s.add("default", tfStyle);

        TextButton.TextButtonStyle btnAccStyle = new TextButton.TextButtonStyle();
        btnAccStyle.font = s.getFont("boton");
        btnAccStyle.fontColor = new Color(0.05f, 0.05f, 0.10f, 1f);
        btnAccStyle.up = s.newDrawable("botonAcc");
        btnAccStyle.down = s.newDrawable("botonAcc",
                new Color(0.6f, 0.6f, 0.6f, 1f));
        btnAccStyle.over = s.newDrawable("botonAcc",
                new Color(1.15f, 1.15f, 1.15f, 1f));
        s.add("acento", btnAccStyle);

        TextButton.TextButtonStyle btnSecStyle = new TextButton.TextButtonStyle();
        btnSecStyle.font = s.getFont("boton");
        btnSecStyle.fontColor = COLOR_TEXTO_GRIS;
        btnSecStyle.up = s.newDrawable("botonBg");
        btnSecStyle.down = s.newDrawable("botonBg",
                new Color(0.6f, 0.6f, 0.6f, 1f));
        btnSecStyle.over = s.newDrawable("botonBg",
                new Color(1.15f, 1.15f, 1.15f, 1f));
        s.add("secundario", btnSecStyle);

        Label.LabelStyle lsDefault = new Label.LabelStyle(
                s.getFont("default"), COLOR_TEXTO_GRIS);
        s.add("default", lsDefault);

        Label.LabelStyle lsError = new Label.LabelStyle(
                s.getFont("mensaje"), COLOR_ERROR);
        s.add("error", lsError);

        Label.LabelStyle lsExito = new Label.LabelStyle(
                s.getFont("mensaje"), COLOR_EXITO);
        s.add("exito", lsExito);

        Label.LabelStyle lsMensaje = new Label.LabelStyle(
                s.getFont("mensaje"), Color.WHITE);
        s.add("mensaje", lsMensaje);

        return s;
    }

    private com.badlogic.gdx.graphics.Texture crearPixmapTexture(
            int w, int h, Color c) {
        com.badlogic.gdx.graphics.Pixmap pm
                = new com.badlogic.gdx.graphics.Pixmap(w, h,
                        com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pm.setColor(c);
        pm.fill();
        com.badlogic.gdx.graphics.Texture tex
                = new com.badlogic.gdx.graphics.Texture(pm);
        pm.dispose();
        return tex;
    }
    
    private void configurarSiguienteCampo(final TextField actual, final TextField siguiente) {
    actual.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.DOWN && siguiente != null) {
                stage.setKeyboardFocus(siguiente);
                return true;
            }
            return false;
        }
    });
}

    private void construirUI() {
        stage.clear();

        tfNombre = crearTextField(Traductor.nombreCompleto(juego.idiomaActual), false);
        tfUsername = crearTextField(Traductor.usuario(juego.idiomaActual), false);
        tfPassword = crearTextField(Traductor.contrasena(juego.idiomaActual), true);
        tfConfirm = crearTextField(Traductor.confirmarContrasena(juego.idiomaActual), true);

        configurarSiguienteCampo(tfNombre, tfUsername);
        configurarSiguienteCampo(tfUsername, tfPassword);
        configurarSiguienteCampo(tfPassword, tfConfirm);
        configurarSiguienteCampo(tfConfirm, tfNombre); 

        btnTogglePass = new TextButton(Traductor.mostrar(juego.idiomaActual), skin, "secundario");
        btnTogglePass.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                alternarPassword();
                btnTogglePass.setText(passVisible ? Traductor.ocultar(juego.idiomaActual) : Traductor.mostrar(juego.idiomaActual));
            }
        });

        btnPrincipal = new TextButton(
                modoRegistro ? Traductor.registrarse(juego.idiomaActual) : Traductor.iniciarSesion(juego.idiomaActual),
                skin, "acento");
        btnSecundario = new TextButton(
                modoRegistro ? "Ya tienes cuenta? Iniciar sesion"
                        : "No tienes cuenta? Registrarse",
                skin, "secundario");

        btnPrincipal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (modoRegistro) {
                    ejecutarRegistro();
                } else {
                    ejecutarLogin();
                }
            }
        });
        btnSecundario.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                cambiarModo();
            }
        });

        lblSubtitulo = new Label(
                modoRegistro ? "Crear cuenta" : "Iniciar sesion",
                skin, "default");
        lblMensaje = new Label("", skin, "mensaje");

        Table root = new Table();
        root.setFillParent(true);  

        Table panel = new Table();
        panel.setBackground(skin.newDrawable("campoBg",
                new Color(0.12f, 0.12f, 0.18f, 1f)));

        float panelW = Math.min(460f, stage.getWidth() * 0.85f);
        float btnW = panelW - 80f;
        float campoW = panelW - 60f;
        float campoH = 44f;
        float gap = 14f;

        panel.add(lblSubtitulo).colspan(2).padTop(12f).padBottom(6f).row();

        if (modoRegistro) {
            panel.add(new Label(Traductor.nombreCompleto(juego.idiomaActual), skin)).left()
                    .padLeft(10f).padTop(gap).row();
            panel.add(tfNombre).width(campoW).height(campoH)
                    .padLeft(10f).padRight(10f).padBottom(4f).row();
        }

        panel.add(new Label(Traductor.usuario(juego.idiomaActual), skin)).left()
                .padLeft(10f).padTop(gap).row();
        panel.add(tfUsername).width(campoW).height(campoH)
                .padLeft(10f).padRight(10f).padBottom(4f).row();

        Table rowPass = new Table();
        rowPass.add(new Label(Traductor.contrasena(juego.idiomaActual), skin)).left().expandX();
        panel.add(rowPass).width(campoW).padLeft(10f).padTop(gap).row();
        Table rowPassField = new Table();
        rowPassField.add(tfPassword).width(campoW - 54f).height(campoH);
        rowPassField.add(btnTogglePass).width(50f).height(campoH).padLeft(4f);
        panel.add(rowPassField).width(campoW).padLeft(10f).padRight(10f).padBottom(4f).row();

        if (modoRegistro) {
            Label lblReq = new Label(
                    "Min 5 chars, 1 MAYUSCULA, 1 numero, 1 especial",
                    skin);
            lblReq.setColor(new Color(0.50f, 0.50f, 0.65f, 1f));
            panel.add(lblReq).left().padLeft(10f).padBottom(2f).row();

            panel.add(new Label(Traductor.confirmarContrasena(juego.idiomaActual), skin)).left()
                    .padLeft(10f).padTop(gap).row();
            panel.add(tfConfirm).width(campoW).height(campoH)
                    .padLeft(10f).padRight(10f).padBottom(4f).row();
        }

        panel.add(lblMensaje).colspan(2).padTop(8f).padBottom(4f).row();

        panel.add(btnPrincipal).width(btnW).height(50f)
                .padTop(8f).padBottom(6f).row();
        panel.add(btnSecundario).width(btnW).height(44f)
                .padBottom(16f).row();

        root.add(panel).width(panelW).pad(20f);
        stage.addActor(root);

        stage.setKeyboardFocus(modoRegistro ? tfNombre : tfUsername);
    }

    private TextField crearTextField(String placeholder, boolean password) {
        TextField tf = new TextField("", skin);
        tf.setMessageText(placeholder);
        if (password) {
            tf.setPasswordMode(true);
            tf.setPasswordCharacter('*');
        }
        return tf;
    }

    private void alternarPassword() {
        passVisible = !passVisible;
        tfPassword.setPasswordMode(!passVisible);
        tfConfirm.setPasswordMode(!passVisible);
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float cx = W / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(0, H - 4f, W, 4f);
        juego.shapeRenderer.end();

        juego.batch.begin();
        fuenteTitulo.setColor(COLOR_ACENTO);
        layout.setText(fuenteTitulo, "FLOW FREE");
        fuenteTitulo.draw(juego.batch, "FLOW FREE",
                cx - layout.width / 2f, H - 20f);
        juego.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        construirUI(); 
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

    private void ejecutarLogin() {
        String user = tfUsername.getText().trim();
        String pass = tfPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            setMensaje(Traductor.completarCampos(juego.idiomaActual), false);
            return;
        }
        if (juego.gestorUsuarios.validarLogin(user, pass)) {
            List<Reto> completados = juego.gestorUsuarios.obtenerRetosCompletados();
            StringBuilder msg = new StringBuilder();
            for (Reto r : completados) {
                String g = r.getGanador();
                if (r.getGanador() != null) {
                    if (r.getGanador().equals(juego.gestorUsuarios.getUsuarioActual().getUsername())) {
                        msg.append("Ganaste reto Nv.").append(r.getNivel())
                           .append(" contra ").append(r.getRemitente().equals(user.toUpperCase())
                                   ? r.getDestinatario() : r.getRemitente()).append("! ");
                    } else if (!r.getGanador().equals("EMPATE")) {
                        msg.append("Perdiste reto Nv.").append(r.getNivel())
                           .append(" contra ").append(r.getRemitente().equals(user.toUpperCase())
                                   ? r.getDestinatario() : r.getRemitente()).append(". ");
                    }
                }
            }
            String notif = msg.toString();
            if (!notif.isEmpty()) {
                setMensaje(notif, true);
            }

            List<String> sols = juego.gestorUsuarios.obtenerSolicitudesPendientes();
            if (!sols.isEmpty()) {
                if (!notif.isEmpty()) notif += " | ";
                notif += "Tienes " + sols.size() + " solicitud(es) de amistad pendiente(s)";
            }

            juego.setScreen(new PantallaMenu(juego));
        } else {
            setMensaje(Traductor.credencialesIncorrectas(juego.idiomaActual), false);
        }
    }

    private void ejecutarRegistro() {
        String nombre = tfNombre.getText().trim();
        String user = tfUsername.getText().trim();
        String pass = tfPassword.getText().trim();
        String confirm = tfConfirm.getText().trim();

        if (nombre.isEmpty() || user.isEmpty()
                || pass.isEmpty() || confirm.isEmpty()) {
            setMensaje(Traductor.completarCampos(juego.idiomaActual), false);
            return;
        }
        if (!GestorUsuarios.validarPassword(pass)) {
            setMensaje(GestorUsuarios.getMensajeValidacionPassword(pass), false);
            return;
        }
        if (!pass.equals(confirm)) {
            setMensaje("Las contrasenas no coinciden", false);
            return;
        }
        if (juego.gestorUsuarios.crearUsuario(user, pass, nombre)) {
            juego.gestorUsuarios.validarLogin(user, pass);
            juego.setScreen(new PantallaMenu(juego));
        } else {
            setMensaje("El usuario ya existe", false);
        }
    }

    private void cambiarModo() {
        modoRegistro = !modoRegistro;
        tfNombre.setText("");
        tfUsername.setText("");
        tfPassword.setText("");
        tfConfirm.setText("");
        lblMensaje.setText("");
        construirUI();
    }

    private void setMensaje(String texto, boolean esExito) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(skin.get(
                esExito ? "exito" : "error", Label.LabelStyle.class));
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (fuenteTitulo != null) {
            fuenteTitulo.dispose();
        }
    }
}
