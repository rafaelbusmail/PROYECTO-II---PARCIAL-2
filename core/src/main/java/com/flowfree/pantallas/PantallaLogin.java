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
import com.flowfree.datos.GestorUsuarios;

public class PantallaLogin extends PantallaBase {

    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private GlyphLayout layout;

    private StringBuilder campoUsername;
    private StringBuilder campoPassword;
    private StringBuilder campoNombre;
    private StringBuilder campoPasswordConfirm;

    private boolean modoRegistro;
    private int campoActivo;
    private String mensajeError;
    private String mensajeExito;
    private boolean mostrarPassword;

    private float anchoVentana, altoVentana;
    private float anchoCampo, altoCampo, xCentro;
    private float yUsername, yPassword, yNombre, yConfirm;
    private float yBotonPrincipal, yBotonSecundario;
    private float yMensaje;   

    private static final int MAX_CAMPO = 30;
    private static final float PANEL_W = 480f;
    private static final float BTN_W = 320f;
    private static final float BTN_H = 46f;
    private static final float CAMPO_H = 44f;
    private static final float LABEL_H = 20f; 

    public PantallaLogin(FlowFreeGame juego) {
        super(juego);
        campoUsername = new StringBuilder();
        campoPassword = new StringBuilder();
        campoNombre = new StringBuilder();
        campoPasswordConfirm = new StringBuilder();
        modoRegistro = false;
        campoActivo = 0;
        mensajeError = "";
        mensajeExito = "";
        mostrarPassword = false;
    }

    @Override
    public void show() {
        fuente = crearFuente(20);
        fuenteGrande = crearFuente(36);
        layout = new GlyphLayout();
        recalcularLayout();
    }

    @Override
    public void resize(int width, int height) {
        recalcularLayout();
    }

    private void recalcularLayout() {
        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        anchoCampo = Math.min(400f, anchoVentana * 0.72f);
        altoCampo = CAMPO_H;
        xCentro = anchoVentana / 2f;
        calcularPosiciones();
    }

    private void calcularPosiciones() {
        float gap = 78f;   
        float cy = altoVentana / 2f;

        if (modoRegistro) {
            yNombre = cy + 145f;
            yUsername = yNombre - gap;
            yPassword = yUsername - gap;
            yConfirm = yPassword - gap;
            yBotonPrincipal = yConfirm - 85f;
            yBotonSecundario = yBotonPrincipal - BTN_H - 14f;
        } else {
            yUsername = cy + 60f;
            yPassword = yUsername - gap;
            yBotonPrincipal = yPassword - 90f;
            yBotonSecundario = yBotonPrincipal - BTN_H - 14f;
        }

        yMensaje = yBotonSecundario - 38f;
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();
        manejarInput();

        float panelH = modoRegistro ? 570f : 400f;
        float panelX = xCentro - PANEL_W / 2f;
        float panelY = altoVentana / 2f - panelH / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, PANEL_W, panelH);

        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, PANEL_W - 40, 3);

        if (modoRegistro) {
            dibujarCampo(xCentro - anchoCampo / 2f, yNombre, anchoCampo, altoCampo, campoActivo == 0);
        }
        dibujarCampo(xCentro - anchoCampo / 2f, yUsername, anchoCampo, altoCampo, campoActivo == (modoRegistro ? 1 : 0));
        dibujarCampo(xCentro - anchoCampo / 2f, yPassword, anchoCampo, altoCampo, campoActivo == (modoRegistro ? 2 : 1));
        if (modoRegistro) {
            dibujarCampo(xCentro - anchoCampo / 2f, yConfirm, anchoCampo, altoCampo, campoActivo == 3);
        }

        dibujarBoton(xCentro - BTN_W / 2f, yBotonPrincipal, BTN_W, BTN_H, COLOR_ACENTO);
        dibujarBoton(xCentro - BTN_W / 2f, yBotonSecundario, BTN_W, BTN_H, COLOR_BOTON);

        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_ACENTO);
        dibujarTextoCentrado(fuenteGrande, "FLOW FREE",
                xCentro, panelY + panelH - 18f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente, modoRegistro ? "Crear cuenta" : "Iniciar sesión",
                xCentro, panelY + panelH - 58f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        if (modoRegistro) {
            fuente.draw(juego.batch, "Nombre completo",
                    xCentro - anchoCampo / 2f, yNombre + altoCampo + LABEL_H + 2f);
        }
        fuente.draw(juego.batch, "Usuario",
                xCentro - anchoCampo / 2f, yUsername + altoCampo + LABEL_H + 2f);
        fuente.draw(juego.batch, "Contraseña",
                xCentro - anchoCampo / 2f, yPassword + altoCampo + LABEL_H + 2f);
        if (modoRegistro) {
            fuente.draw(juego.batch, "Confirmar contraseña",
                    xCentro - anchoCampo / 2f, yConfirm + altoCampo + LABEL_H + 2f);
        }

        fuente.setColor(COLOR_TEXTO);
        if (modoRegistro) {
            fuente.draw(juego.batch, campoNombre.toString(),
                    xCentro - anchoCampo / 2f + 10f, yNombre + altoCampo / 2f + 7f);
        }
        fuente.draw(juego.batch, campoUsername.toString(),
                xCentro - anchoCampo / 2f + 10f, yUsername + altoCampo / 2f + 7f);

        String passVisible = mostrarPassword
                ? campoPassword.toString()
                : "*".repeat(campoPassword.length());
        fuente.draw(juego.batch, passVisible,
                xCentro - anchoCampo / 2f + 10f, yPassword + altoCampo / 2f + 7f);

        if (modoRegistro) {
            String confirmVisible = mostrarPassword
                    ? campoPasswordConfirm.toString()
                    : "*".repeat(campoPasswordConfirm.length());
            fuente.draw(juego.batch, confirmVisible,
                    xCentro - anchoCampo / 2f + 10f, yConfirm + altoCampo / 2f + 7f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        String toggleHint = mostrarPassword ? "[TAB] Ocultar" : "[TAB] Mostrar";
        layout.setText(fuente, toggleHint);
        fuente.draw(juego.batch, toggleHint,
                xCentro + anchoCampo / 2f - layout.width - 4f,
                yPassword + altoCampo + LABEL_H + 2f);

        fuente.setColor(COLOR_FONDO);
        dibujarTextoCentrado(fuente,
                modoRegistro ? "REGISTRARSE" : "INICIAR SESIÓN",
                xCentro, yBotonPrincipal + BTN_H / 2f + 7f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente,
                modoRegistro ? "¿Ya tienes cuenta? Iniciar sesión"
                        : "¿No tienes cuenta? Registrarse",
                xCentro, yBotonSecundario + BTN_H / 2f + 7f);

        if (!mensajeError.isEmpty()) {
            fuente.setColor(COLOR_ERROR);
            dibujarTextoCentrado(fuente, mensajeError, xCentro, yMensaje);
        } else if (!mensajeExito.isEmpty()) {
            fuente.setColor(COLOR_ACENTO);
            dibujarTextoCentrado(fuente, mensajeExito, xCentro, yMensaje);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente,
                "[ENTER] Confirmar  [ESC] Limpiar  [↑↓] Cambiar campo",
                xCentro, 30f);

        juego.batch.end();
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            mostrarPassword = !mostrarPassword;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            limpiarCampos();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (modoRegistro) {
                ejecutarRegistro();
            } else {
                ejecutarLogin();
            }
        }

        int maxCampo = modoRegistro ? 3 : 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            campoActivo = Math.min(campoActivo + 1, maxCampo);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            campoActivo = Math.max(campoActivo - 1, 0);
        }

        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = altoVentana - Gdx.input.getY();
            detectarCampoClickeado(mx, my);
            detectarBotonClickeado(mx, my);
        }

        manejarEscritura();
    }

    private void manejarEscritura() {
        StringBuilder campo = getCampoActual();
        if (campo == null) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (campo.length() > 0) {
                campo.deleteCharAt(campo.length() - 1);
            }
            mensajeError = "";
            return;
        }

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        for (int i = 0; i < 256; i++) {
            if (!Gdx.input.isKeyJustPressed(i)) {
                continue;
            }
            char c = keyToChar(i, shift);
            if (c != 0 && campo.length() < MAX_CAMPO) {
                if (c == ' ' && !(modoRegistro && campoActivo == 0)) {
                    continue;
                }
                campo.append(c);
                mensajeError = "";
            }
        }
    }

    private StringBuilder getCampoActual() {
        if (modoRegistro) {
            switch (campoActivo) {
                case 0:
                    return campoNombre;
                case 1:
                    return campoUsername;
                case 2:
                    return campoPassword;
                case 3:
                    return campoPasswordConfirm;
            }
        } else {
            switch (campoActivo) {
                case 0:
                    return campoUsername;
                case 1:
                    return campoPassword;
            }
        }
        return null;
    }

    private void detectarCampoClickeado(float mx, float my) {
        float x = xCentro - anchoCampo / 2f;
        if (modoRegistro) {
            if (dentroDeRect(mx, my, x, yNombre, anchoCampo, altoCampo)) {
                campoActivo = 0;
                return;
            }
            if (dentroDeRect(mx, my, x, yUsername, anchoCampo, altoCampo)) {
                campoActivo = 1;
                return;
            }
            if (dentroDeRect(mx, my, x, yPassword, anchoCampo, altoCampo)) {
                campoActivo = 2;
                return;
            }
            if (dentroDeRect(mx, my, x, yConfirm, anchoCampo, altoCampo)) {
                campoActivo = 3;
                return;
            }
        } else {
            if (dentroDeRect(mx, my, x, yUsername, anchoCampo, altoCampo)) {
                campoActivo = 0;
                return;
            }
            if (dentroDeRect(mx, my, x, yPassword, anchoCampo, altoCampo)) {
                campoActivo = 1;
                return;
            }
        }
    }

    private void detectarBotonClickeado(float mx, float my) {
        if (dentroDeRect(mx, my, xCentro - BTN_W / 2f, yBotonPrincipal, BTN_W, BTN_H)) {
            if (modoRegistro) {
                ejecutarRegistro();
            } else {
                ejecutarLogin();
            }
        }
        if (dentroDeRect(mx, my, xCentro - BTN_W / 2f, yBotonSecundario, BTN_W, BTN_H)) {
            cambiarModo();
        }
    }

    private void ejecutarLogin() {
        String user = campoUsername.toString().trim();
        String pass = campoPassword.toString().trim();
        if (user.isEmpty() || pass.isEmpty()) {
            mensajeError = "Completa todos los campos";
            return;
        }
        if (juego.gestorUsuarios.validarLogin(user, pass)) {
            mensajeError = "";
            mensajeExito = "¡Bienvenido, " + user + "!";
            juego.setScreen(new PantallaMenu(juego));
        } else {
            mensajeError = "Usuario o contraseña incorrectos";
        }
    }

    private void ejecutarRegistro() {
        String nombre = campoNombre.toString().trim();
        String user = campoUsername.toString().trim();
        String pass = campoPassword.toString().trim();
        String confirm = campoPasswordConfirm.toString().trim();

        if (nombre.isEmpty() || user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            mensajeError = "Completa todos los campos";
            return;
        }
        if (!GestorUsuarios.validarPassword(pass)) {
            mensajeError = GestorUsuarios.getMensajeValidacionPassword(pass);
            return;
        }
        if (!pass.equals(confirm)) {
            mensajeError = "Las contraseñas no coinciden";
            return;
        }

        if (juego.gestorUsuarios.crearUsuario(user, pass, nombre)) {
            juego.gestorUsuarios.validarLogin(user, pass); // sets current user in gestor
            mensajeError = "";
            mensajeExito = "¡Cuenta creada! Bienvenido, " + nombre + "!";
            juego.setScreen(new PantallaMenu(juego));
        } else {
            mensajeError = "El usuario ya existe";
        }
    }

    private void cambiarModo() {
        modoRegistro = !modoRegistro;
        limpiarCampos();
        campoActivo = 0;
        calcularPosiciones();
    }

    private void limpiarCampos() {
        campoUsername.setLength(0);
        campoPassword.setLength(0);
        campoNombre.setLength(0);
        campoPasswordConfirm.setLength(0);
        mensajeError = "";
        mensajeExito = "";
    }

    private void dibujarCampo(float x, float y, float w, float h, boolean activo) {
        juego.shapeRenderer.setColor(activo
                ? new Color(0.18f, 0.18f, 0.28f, 1f)
                : new Color(0.14f, 0.14f, 0.20f, 1f));
        juego.shapeRenderer.rect(x, y, w, h);
        juego.shapeRenderer.setColor(activo ? COLOR_ACENTO : COLOR_BORDE);
        juego.shapeRenderer.rect(x, y, w, activo ? 2.5f : 1f);
    }

    private void dibujarBoton(float x, float y, float w, float h, Color color) {
        juego.shapeRenderer.setColor(color);
        juego.shapeRenderer.rect(x, y, w, h);
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto, float cx, float y) {
        layout.setText(font, texto);
        font.draw(juego.batch, texto, cx - layout.width / 2f, y);
    }

    private boolean dentroDeRect(float mx, float my,
            float rx, float ry, float rw, float rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    private char keyToChar(int keycode, boolean shift) {
        if (keycode >= Input.Keys.A && keycode <= Input.Keys.Z) {
            char base = (char) ('A' + (keycode - Input.Keys.A));
            return shift ? base : Character.toLowerCase(base);
        }
        if (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9) {
            if (!shift) {
                return (char) ('0' + (keycode - Input.Keys.NUM_0));
            }
            char[] shiftDigits = {')', '!', '@', '#', '$', '%', '^', '&', '*', '('};
            return shiftDigits[keycode - Input.Keys.NUM_0];
        }
        if (keycode >= Input.Keys.NUMPAD_0 && keycode <= Input.Keys.NUMPAD_9) {
            return (char) ('0' + (keycode - Input.Keys.NUMPAD_0));
        }
        if (keycode == Input.Keys.SPACE) {
            return ' ';
        }
        switch (keycode) {
            case Input.Keys.PERIOD:
                return shift ? '>' : '.';
            case Input.Keys.COMMA:
                return shift ? '<' : ',';
            case Input.Keys.MINUS:
                return shift ? '_' : '-';
            case Input.Keys.EQUALS:
                return shift ? '+' : '=';
            case Input.Keys.SEMICOLON:
                return shift ? ':' : ';';
            case Input.Keys.APOSTROPHE:
                return shift ? '"' : '\'';
            case Input.Keys.SLASH:
                return shift ? '?' : '/';
            case Input.Keys.BACKSLASH:
                return shift ? '|' : '\\';
            case Input.Keys.LEFT_BRACKET:
                return shift ? '{' : '[';
            case Input.Keys.RIGHT_BRACKET:
                return shift ? '}' : ']';
            case Input.Keys.GRAVE:
                return shift ? '~' : '`';
            case Input.Keys.AT:
                return '@';
            case Input.Keys.STAR:
                return '*';
            case Input.Keys.POUND:
                return '#';
            default:
                return 0;
        }
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

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
