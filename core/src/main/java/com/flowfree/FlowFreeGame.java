package com.flowfree;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.flowfree.datos.GestorUsuarios;
import com.flowfree.enums.Idioma;
import com.flowfree.hilos.HiloAutoguardado;
import com.flowfree.pantallas.PantallaLogin;

public class FlowFreeGame extends Game {

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;

    public GestorUsuarios gestorUsuarios;

    private HiloAutoguardado hiloAutoguardado;
    private Music musicaFondo;

    public String retoDestinatario = null;
    public String retoRemitente = null;
    public int retoNivel = -1;
    public long retoTiempoRemitente = 0;
    public int retoPuntajeRemitente = 0;

    public Idioma idiomaActual = Idioma.ES;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        gestorUsuarios = new GestorUsuarios();

        idiomaActual = gestorUsuarios.cargarIdiomaGlobal();

        hiloAutoguardado = new HiloAutoguardado(gestorUsuarios);
        hiloAutoguardado.start();

        cargarMusica();

        setScreen(new PantallaLogin(this));
    }

    public void cargarMusica() {
        try {
            musicaFondo = Gdx.audio.newMusic(
                    Gdx.files.internal("music/nocopyrightsound633-arcade-beat-323176.mp3"));
            musicaFondo.setLooping(true);
            musicaFondo.setVolume(0.5f);
        } catch (Exception e) {
            System.err.println("No se pudo cargar musica: " + e.getMessage());
            musicaFondo = null;
        }
    }

    public void iniciarMusica() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.stop();
        }
    }

    public void ajustarVolumenMusica(float vol) {
        if (musicaFondo != null) {
            musicaFondo.setVolume(vol);
        }
    }

    public void actualizarIdioma() {
        if (gestorUsuarios.getUsuarioActual() != null) {
            String user = gestorUsuarios.getUsuarioActual().getUsername();
            com.flowfree.modelo.Preferencias pref = gestorUsuarios.cargarPreferencias(user);
            this.idiomaActual = pref.getIdioma();
            this.ajustarVolumenMusica(pref.getVolumenMusica());
        }
        gestorUsuarios.guardarIdiomaGlobal(this.idiomaActual);
    }

    @Override
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
            musicaFondo = null;
        }
        batch.dispose();
        shapeRenderer.dispose();
        if (hiloAutoguardado != null) {
            hiloAutoguardado.detener();
        }
    }
}
