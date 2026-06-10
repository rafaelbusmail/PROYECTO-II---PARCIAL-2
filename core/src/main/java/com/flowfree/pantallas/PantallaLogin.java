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

        anchoVentana = Gdx.graphics.getWidth();
        altoVentana = Gdx.graphics.getHeight();
        anchoCampo = 380f;
        altoCampo = 42f;
        xCentro = anchoVentana / 2f;
        calcularPosiciones();
    }

    private void calcularPosiciones() {
        float cy = altoVentana / 2f;
        if (modoRegistro) {
            yNombre = cy + 110f;
            yUsername = cy + 40f;
            yPassword = cy - 30f;
            yConfirm = cy - 100f;
            yBotonPrincipal = cy - 175f;
            yBotonSecundario = cy - 225f;
        } else {
            yUsername = cy + 50f;
            yPassword = cy - 20f;
            yBotonPrincipal = cy - 95f;
            yBotonSecundario = cy - 145f;
        }
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();
        manejarInput();

        float panelW = 460f;
        float panelH = modoRegistro ? 520f : 380f;
        float panelX = xCentro - panelW / 2f;
        float panelY = altoVentana / 2f - panelH / 2f;

        juego.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        juego.shapeRenderer.setColor(COLOR_PANEL);
        juego.shapeRenderer.rect(panelX, panelY, panelW, panelH);
        juego.shapeRenderer.setColor(COLOR_ACENTO);
        juego.shapeRenderer.rect(panelX + 20, panelY + panelH - 4, panelW - 40, 3);

        if (modoRegistro) {
            dibujarCampo(xCentro - anchoCampo / 2f, yNombre, anchoCampo, altoCampo, campoActivo == 2);
        }
        dibujarCampo(xCentro - anchoCampo / 2f, yUsername, anchoCampo, altoCampo, campoActivo == 0);
        dibujarCampo(xCentro - anchoCampo / 2f, yPassword, anchoCampo, altoCampo, campoActivo == 1);
        if (modoRegistro) {
            dibujarCampo(xCentro - anchoCampo / 2f, yConfirm, anchoCampo, altoCampo, campoActivo == 3);
        }

        dibujarBoton(xCentro - 150f, yBotonPrincipal, 300f, 44f, COLOR_ACENTO);
        dibujarBoton(xCentro - 150f, yBotonSecundario, 300f, 44f, COLOR_BOTON);
        juego.shapeRenderer.end();

        juego.batch.begin();

        fuenteGrande.setColor(COLOR_ACENTO);
        dibujarTextoCentrado(fuenteGrande, "FLOW FREE",
                xCentro, altoVentana / 2f + (modoRegistro ? 295f : 235f));

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente, modoRegistro ? "Crear cuenta" : "Iniciar sesión",
                xCentro, altoVentana / 2f + (modoRegistro ? 255f : 195f));

        if (modoRegistro) {
            fuente.draw(juego.batch, "Nombre completo", xCentro - anchoCampo / 2f, yNombre + altoCampo + 18f);
        }
        fuente.draw(juego.batch, "Usuario", xCentro - anchoCampo / 2f, yUsername + altoCampo + 18f);
        fuente.draw(juego.batch, "Contraseña", xCentro - anchoCampo / 2f, yPassword + altoCampo + 18f);
        if (modoRegistro) {
            fuente.draw(juego.batch, "Confirmar contraseña", xCentro - anchoCampo / 2f, yConfirm + altoCampo + 18f);
        }

        fuente.setColor(COLOR_TEXTO);
        if (modoRegistro) {
            fuente.draw(juego.batch, campoNombre.toString(),
                    xCentro - anchoCampo / 2f + 10f, yNombre + altoCampo / 2f + 7f);
        }
        fuente.draw(juego.batch, campoUsername.toString(),
                xCentro - anchoCampo / 2f + 10f, yUsername + altoCampo / 2f + 7f);

        String passVisible = mostrarPassword ? campoPassword.toString()
                : "*".repeat(campoPassword.length());
        fuente.draw(juego.batch, passVisible,
                xCentro - anchoCampo / 2f + 10f, yPassword + altoCampo / 2f + 7f);

        if (modoRegistro) {
            String confirmVisible = mostrarPassword ? campoPasswordConfirm.toString()
                    : "*".repeat(campoPasswordConfirm.length());
            fuente.draw(juego.batch, confirmVisible,
                    xCentro - anchoCampo / 2f + 10f, yConfirm + altoCampo / 2f + 7f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        fuente.draw(juego.batch, mostrarPassword ? "[TAB] Ocultar" : "[TAB] Mostrar",
                xCentro + anchoCampo / 2f - 130f, yPassword + altoCampo + 18f);

        fuente.setColor(COLOR_FONDO);
        dibujarTextoCentrado(fuente,
                modoRegistro ? "REGISTRARSE" : "INICIAR SESIÓN",
                xCentro, yBotonPrincipal + 28f);

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente,
                modoRegistro ? "¿Ya tienes cuenta? Iniciar sesión"
                        : "¿No tienes cuenta? Registrarse",
                xCentro, yBotonSecundario + 28f);

        if (!mensajeError.isEmpty()) {
            fuente.setColor(COLOR_ERROR);
            dibujarTextoCentrado(fuente, mensajeError, xCentro, yBotonPrincipal - 25f);
        }
        if (!mensajeExito.isEmpty()) {
            fuente.setColor(COLOR_ACENTO);
            dibujarTextoCentrado(fuente, mensajeExito, xCentro, yBotonPrincipal - 25f);
        }

        fuente.setColor(COLOR_TEXTO_GRIS);
        dibujarTextoCentrado(fuente, "[ENTER] Confirmar  [ESC] Limpiar", xCentro, 30f);

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            campoActivo = Math.min(campoActivo + 1, modoRegistro ? 3 : 1);
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
        }
        for (int i = 0; i < 256; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = keyToChar(i);
                if (c != 0 && campo.length() < 30) {
                    campo.append(c);
                    mensajeError = "";
                }
            }
        }
    }

    private StringBuilder getCampoActual() {
        switch (campoActivo) {
            case 0:
                return campoUsername;
            case 1:
                return campoPassword;
            case 2:
                return modoRegistro ? campoNombre : null;
            case 3:
                return modoRegistro ? campoPasswordConfirm : null;
            default:
                return null;
        }
    }

    private void detectarCampoClickeado(float mx, float my) {
        float x = xCentro - anchoCampo / 2f;
        if (modoRegistro && dentroDeRect(mx, my, x, yNombre, anchoCampo, altoCampo)) {
            campoActivo = 2;
        }
        if (dentroDeRect(mx, my, x, yUsername, anchoCampo, altoCampo)) {
            campoActivo = 0;
        }
        if (dentroDeRect(mx, my, x, yPassword, anchoCampo, altoCampo)) {
            campoActivo = 1;
        }
        if (modoRegistro && dentroDeRect(mx, my, x, yConfirm, anchoCampo, altoCampo)) {
            campoActivo = 3;
        }
    }

    private void detectarBotonClickeado(float mx, float my) {
        if (dentroDeRect(mx, my, xCentro - 150f, yBotonPrincipal, 300f, 44f)) {
            if (modoRegistro) {
                ejecutarRegistro();
            } else {
                ejecutarLogin();
            }
        }
        if (dentroDeRect(mx, my, xCentro - 150f, yBotonSecundario, 300f, 44f)) {
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
        if (nombre.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            mensajeError = "Completa todos los campos";
            return;
        }
        if (!pass.equals(confirm)) {
            mensajeError = "Las contraseñas no coinciden";
            return;
        }
        if (!GestorUsuarios.validarPassword(pass)) {
            mensajeError = GestorUsuarios.getMensajeValidacionPassword(pass);
            return;
        }
        if (juego.gestorUsuarios.crearUsuario(user, pass, nombre)) {
            mensajeExito = "¡Cuenta creada! Inicia sesión";
            cambiarModo();
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
                ? new Color(0.18f, 0.18f, 0.26f, 1f)
                : new Color(0.15f, 0.15f, 0.20f, 1f));
        juego.shapeRenderer.rect(x, y, w, h);
        juego.shapeRenderer.setColor(activo ? COLOR_ACENTO : COLOR_BORDE);
        juego.shapeRenderer.rect(x, y, w, activo ? 2f : 1f);
    }

    private void dibujarBoton(float x, float y, float w, float h, Color color) {
        juego.shapeRenderer.setColor(color);
        juego.shapeRenderer.rect(x, y, w, h);
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto, float cx, float y) {
        layout.setText(font, texto);
        font.draw(juego.batch, texto, cx - layout.width / 2f, y);
    }

    private boolean dentroDeRect(float mx, float my, float rx, float ry, float rw, float rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    private char keyToChar(int keycode) {
        String s = Input.Keys.toString(keycode);
        return (s != null && s.length() == 1) ? s.charAt(0) : 0;
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
}
