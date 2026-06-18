package com.flowfree.datos;

import com.flowfree.interfaces.IGestionable;
import com.flowfree.modelo.Estadisticas;
import com.flowfree.modelo.HistorialPartida;
import com.flowfree.modelo.Preferencias;
import com.flowfree.modelo.Reto;
import com.flowfree.modelo.Usuario;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class GestorUsuarios implements IGestionable {

    private static final String BASE_DIR = "assets/datos/usuarios/";
    private static final String DATOS_SUBDIR = "datos del usuario";
    private Usuario usuarioActual;
    private final GestorArchivos gestorArchivos;

    private static String rutaDatos(String username) {
        return BASE_DIR + username.toUpperCase() + "/" + DATOS_SUBDIR + "/";
    }

    public GestorUsuarios() {
        this.gestorArchivos = new GestorArchivos();
        this.usuarioActual = null;
    }

    @Override
    public boolean crearUsuario(String username, String password, String nombreCompleto) {
        if (username == null || username.isBlank()) {
            return false;
        }

        String datosDir = rutaDatos(username);

        if (gestorArchivos.existeArchivo(datosDir + "perfil.bin")) {
            return false;
        }

        new File(datosDir).mkdirs();

        Usuario nuevoUsuario = new Usuario(
                username.toUpperCase(),
                password,
                nombreCompleto,
                LocalDate.now()
        );
        try {
            guardarPerfil(nuevoUsuario);
            gestorArchivos.guardar(new Estadisticas(username.toUpperCase()),
                    datosDir + "estadisticas.bin");
            gestorArchivos.guardar(new Preferencias(),
                    datosDir + "preferencias.bin");
            usuarioActual = nuevoUsuario;
            return true;
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario buscarUsuario(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String ruta = rutaDatos(username) + "perfil.bin";
        if (!gestorArchivos.existeArchivo(ruta)) {
            return null;
        }
        try {
            return (Usuario) gestorArchivos.cargar(ruta);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validarLogin(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        String datosDir = rutaDatos(username);
        if (!gestorArchivos.existeArchivo(datosDir + "perfil.bin")) {
            return false;
        }

        try {
            Usuario usuario = (Usuario) gestorArchivos.cargar(datosDir + "perfil.bin");
            if (usuario != null && usuario.getPassword().equals(password)) {
                usuarioActual = usuario;
                if (!usuarioActual.isActivo()) {
                    usuarioActual.setActivo(true);
                }
                usuarioActual.setUltimaConexion(LocalDate.now());
                guardarPerfil(usuarioActual);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar perfil: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminarUsuario(String username, String password) {
        Usuario u = buscarUsuario(username);
        if (u == null || !u.getPassword().equals(password)) {
            return false;
        }
        String carpeta = BASE_DIR + username.toUpperCase() + "/";
        File dir = new File(carpeta);
        if (dir.exists()) {
            eliminarRecursivo(dir);
        }
        if (usuarioActual != null
                && usuarioActual.getUsername().equals(username.toUpperCase())) {
            usuarioActual = null;
        }
        return true;
    }

    private void eliminarRecursivo(File f) {
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                eliminarRecursivo(child);
            }
        }
        f.delete();
    }

    public boolean desactivarCuenta() {
        if (usuarioActual == null) return false;
        usuarioActual.setActivo(false);
        try {
            guardarPerfil(usuarioActual);
            usuarioActual = null;
            return true;
        } catch (Exception e) {
            System.err.println("Error al desactivar cuenta: " + e.getMessage());
            return false;
        }
    }

    public boolean reactivarCuenta() {
        if (usuarioActual == null) return false;
        usuarioActual.setActivo(true);
        try {
            guardarPerfil(usuarioActual);
            return true;
        } catch (Exception e) {
            System.err.println("Error al reactivar cuenta: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarSolicitudAmistad(String usernameDestino) {
        if (usuarioActual == null || usernameDestino == null) return false;
        String up = usernameDestino.toUpperCase();
        if (up.equals(usuarioActual.getUsername())) return false;
        if (usuarioActual.getAmigos().contains(up)) return false;

        Usuario destino = buscarUsuario(up);
        if (destino == null) return false;
        if (!destino.isActivo()) return false;

        if (destino.agregarSolicitud(usuarioActual.getUsername())) {
            try { guardarPerfilOtro(destino, up); } catch (Exception e) { return false; }
            return true;
        }
        return false;
    }

    private boolean guardarPerfilOtro(Usuario usuario, String username) {
        try {
            String path = rutaDatos(username) + "perfil.bin";
            new File(rutaDatos(username)).mkdirs();
            gestorArchivos.guardar(usuario, path);
            return true;
        } catch (Exception e) {
            System.err.println("Error guardando perfil de " + username + ": " + e.getMessage());
            return false;
        }
    }

    public boolean aceptarSolicitud(String usernameRemitente) {
        if (usuarioActual == null) return false;
        String up = usernameRemitente.toUpperCase();
        if (!usuarioActual.getSolicitudesPendientes().contains(up)) return false;

        usuarioActual.eliminarSolicitud(up);
        usuarioActual.agregarAmigo(up);

        Usuario remitente = buscarUsuario(up);
        if (remitente != null) {
            remitente.agregarAmigo(usuarioActual.getUsername());
            guardarPerfilOtro(remitente, up);
        }

        actualizarUsuario(usuarioActual);
        return true;
    }

    public boolean rechazarSolicitud(String usernameRemitente) {
        if (usuarioActual == null) return false;
        usuarioActual.eliminarSolicitud(usernameRemitente);
        actualizarUsuario(usuarioActual);
        return true;
    }

    public List<String> obtenerSolicitudesPendientes() {
        if (usuarioActual == null) return new ArrayList<>();
        return new ArrayList<>(usuarioActual.getSolicitudesPendientes());
    }

    public boolean enviarReto(String usernameDestino, int nivel,
                              long tiempoRemitente, int puntajeRemitente) {
        if (usuarioActual == null) return false;
        String up = usernameDestino.toUpperCase();
        if (!usuarioActual.getAmigos().contains(up)) return false;

        Usuario destino = buscarUsuario(up);
        if (destino == null) return false;
        if (!destino.isActivo()) return false;

        List<Reto> retos = cargarRetos(up);
        retos.add(new Reto(usuarioActual.getUsername(), up, nivel,
                tiempoRemitente, puntajeRemitente));
        guardarRetos(up, retos);
        return true;
    }

    public boolean aceptarReto(String usernameRemitente, int nivel,
                               long tiempoDestinatario, int puntajeDestinatario) {
        if (usuarioActual == null) return false;
        List<Reto> retos = cargarRetos(usuarioActual.getUsername());
        for (Reto r : retos) {
            if (r.getRemitente().equals(usernameRemitente.toUpperCase())
                    && r.getNivel() == nivel
                    && r.getEstado() == Reto.EstadoReto.PENDIENTE) {
                r.completarReto(tiempoDestinatario, puntajeDestinatario);
                guardarRetos(usuarioActual.getUsername(), retos);

                List<Reto> retosRem = cargarRetos(usernameRemitente.toUpperCase());
                retosRem.add(r);
                guardarRetos(usernameRemitente.toUpperCase(), retosRem);
                return true;
            }
        }
        return false;
    }

    public boolean rechazarReto(String usernameRemitente, int nivel) {
        if (usuarioActual == null) return false;
        List<Reto> retos = cargarRetos(usuarioActual.getUsername());
        for (Reto r : retos) {
            if (r.getRemitente().equals(usernameRemitente.toUpperCase())
                    && r.getNivel() == nivel
                    && r.getEstado() == Reto.EstadoReto.PENDIENTE) {
                r.rechazarReto();
                guardarRetos(usuarioActual.getUsername(), retos);
                return true;
            }
        }
        return false;
    }

    public List<Reto> obtenerRetosPendientes() {
        if (usuarioActual == null) return new ArrayList<>();
        List<Reto> todos = cargarRetos(usuarioActual.getUsername());
        List<Reto> pendientes = new ArrayList<>();
        for (Reto r : todos) {
            if (r.getEstado() == Reto.EstadoReto.PENDIENTE) {
                pendientes.add(r);
            }
        }
        return pendientes;
    }

    public List<Reto> obtenerRetosCompletados() {
        if (usuarioActual == null) return new ArrayList<>();
        List<Reto> todos = cargarRetos(usuarioActual.getUsername());
        List<Reto> comp = new ArrayList<>();
        for (Reto r : todos) {
            if (r.getEstado() == Reto.EstadoReto.COMPLETADO
                    || r.getEstado() == Reto.EstadoReto.RECHAZADO) {
                comp.add(r);
            }
        }
        return comp;
    }

    private List<Reto> cargarRetos(String username) {
        String ruta = rutaDatos(username) + "retos.bin";
        if (!gestorArchivos.existeArchivo(ruta)) return new ArrayList<>();
        try {
            Object obj = gestorArchivos.cargar(ruta);
            if (obj instanceof List) return (List<Reto>) obj;
        } catch (Exception e) {
            System.err.println("Error cargando retos: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void guardarRetos(String username, List<Reto> retos) {
        String ruta = rutaDatos(username) + "retos.bin";
        try {
            gestorArchivos.guardar(retos, ruta);
        } catch (Exception e) {
            System.err.println("Error guardando retos: " + e.getMessage());
        }
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        try {
            guardarPerfil(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Usuario> obtenerRanking() {
        List<Usuario> lista = new ArrayList<>();
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return lista;
        }

        File[] carpetas = baseDir.listFiles(File::isDirectory);
        if (carpetas == null) {
            return lista;
        }

        for (File carpeta : carpetas) {
            File perfil = new File(carpeta, DATOS_SUBDIR + "/perfil.bin");
            if (perfil.exists()) {
                try {
                    Usuario u = (Usuario) gestorArchivos.cargar(perfil.getPath());
                    if (u != null) {
                        lista.add(u);
                    }
                } catch (Exception e) {
                    System.err.println("Error cargando ranking: " + e.getMessage());
                }
            }
        }

        lista.sort(Comparator.comparingInt(
                (Usuario u) -> u.getEstadisticas().getPuntajeTotal()).reversed());
        return lista;
    }

    public void registrarPartida(String username, HistorialPartida partida) {
        Usuario u = (usuarioActual != null
                && usuarioActual.getUsername().equals(username))
                ? usuarioActual
                : buscarUsuario(username);
        if (u == null) {
            return;
        }

        u.registrarPartida(partida);
        actualizarUsuario(u);

        if (usuarioActual != null
                && usuarioActual.getUsername().equals(username)) {
            usuarioActual = u;
        }
    }

    public static boolean validarPassword(String password) {
        if (password == null || password.length() < 5) {
            return false;
        }
        if (!tieneMayuscula(password)) {
            return false;
        }
        if (!tieneDigito(password)) {
            return false;
        }
        if (!tieneCaracterEspecial(password)) {
            return false;
        }
        return true;
    }

    public static String getMensajeValidacionPassword(String password) {
        if (password == null || password.length() < 5) {
            return "La contrasena debe tener al menos 5 caracteres";
        }
        if (!tieneMayuscula(password)) {
            return "La contrasena debe tener al menos una mayuscula";
        }
        if (!tieneDigito(password)) {
            return "La contrasena debe tener al menos un numero";
        }
        if (!tieneCaracterEspecial(password)) {
            return "La contrasena debe tener al menos un caracter especial";
        }
        return "Contrasena invalida";
    }

    private static boolean tieneMayuscula(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tieneDigito(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tieneCaracterEspecial(String s) {
        String especiales = "!@#$%^&*_\\-+=?/\\\\.,;:'\"~`|<>()[]{}";
        for (char c : s.toCharArray()) {
            if (especiales.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void guardarPerfil(Usuario usuario) throws Exception {
        String path = rutaDatos(usuario.getUsername()) + "perfil.bin";
        new File(rutaDatos(usuario.getUsername())).mkdirs();
        gestorArchivos.guardar(usuario, path);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean hayUsuarioActivo() {
        return usuarioActual != null;
    }


    public boolean agregarAmigo(String usernameAmigo) {
        if (usuarioActual == null || usernameAmigo == null) {
            return false;
        }
        String upper = usernameAmigo.toUpperCase();
        if (upper.equals(usuarioActual.getUsername())) {
            return false;
        }
        Usuario amigo = buscarUsuario(upper);
        if (amigo == null) {
            return false;
        }
        if (usuarioActual.agregarAmigo(upper)) {
            actualizarUsuario(usuarioActual);
            return true;
        }
        return false;
    }

    public boolean eliminarAmigo(String usernameAmigo) {
        if (usuarioActual == null) {
            return false;
        }
        if (usuarioActual.eliminarAmigo(usernameAmigo)) {
            actualizarUsuario(usuarioActual);
            return true;
        }
        return false;
    }

    public List<Usuario> obtenerAmigos() {
        List<Usuario> lista = new ArrayList<>();
        if (usuarioActual == null) {
            return lista;
        }
        List<String> amigosAEliminar = new ArrayList<>();
        for (String u : usuarioActual.getAmigos()) {
            Usuario amigo = buscarUsuario(u);
            if (amigo != null && amigo.isActivo()) {
                if (!amigo.getAmigos().contains(usuarioActual.getUsername())) {
                    amigosAEliminar.add(u);
                    continue;
                }
                lista.add(amigo);
            }
        }
        for (String u : amigosAEliminar) {
            usuarioActual.eliminarAmigo(u);
        }
        if (!amigosAEliminar.isEmpty()) {
            actualizarUsuario(usuarioActual);
        }
        lista.sort(Comparator.comparingInt(
                (Usuario u) -> u.getEstadisticas().getPuntajeTotal()).reversed());
        return lista;
    }

    public Preferencias cargarPreferencias(String username) {
        String ruta = rutaDatos(username) + "preferencias.bin";
        if (!gestorArchivos.existeArchivo(ruta)) {
            Preferencias p = new Preferencias();
            guardarPreferencias(username, p);
            return p;
        }
        try {
            return (Preferencias) gestorArchivos.cargar(ruta);
        } catch (Exception e) {
            System.err.println("Error cargando preferencias: " + e.getMessage());
            return new Preferencias();
        }
    }

    public void guardarPreferencias(String username, Preferencias pref) {
        String ruta = rutaDatos(username) + "preferencias.bin";
        try {
            gestorArchivos.guardar(pref, ruta);
        } catch (Exception e) {
            System.err.println("Error guardando preferencias: " + e.getMessage());
        }
    }

    private static final String GLOBAL_CONFIG = "assets/datos/config.bin";

    public void guardarIdiomaGlobal(com.flowfree.enums.Idioma idioma) {
        try {
            gestorArchivos.guardar(idioma, GLOBAL_CONFIG);
        } catch (Exception e) {
            System.err.println("Error guardando idioma global: " + e.getMessage());
        }
    }

    public com.flowfree.enums.Idioma cargarIdiomaGlobal() {
        try {
            if (!gestorArchivos.existeArchivo(GLOBAL_CONFIG)) return com.flowfree.enums.Idioma.ES;
            Object obj = gestorArchivos.cargar(GLOBAL_CONFIG);
            if (obj instanceof com.flowfree.enums.Idioma) return (com.flowfree.enums.Idioma) obj;
        } catch (Exception e) {
            System.err.println("Error cargando idioma global: " + e.getMessage());
        }
        return com.flowfree.enums.Idioma.ES;
    }
}
