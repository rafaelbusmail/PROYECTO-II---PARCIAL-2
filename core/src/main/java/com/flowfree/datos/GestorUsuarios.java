/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.datos;

import com.flowfree.interfaces.IGestionable;
import com.flowfree.interfaces.IEstadisticas;
import com.flowfree.modelo.Estadisticas;
import com.flowfree.modelo.HistorialPartida;
import com.flowfree.modelo.Usuario;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GestorUsuarios implements IGestionable, IEstadisticas {

    private GestorArchivos gestorArchivos;
    private Usuario usuarioActual;

    public GestorUsuarios() {
        this.gestorArchivos = new GestorArchivos();
        this.usuarioActual = null;
    }

    @Override
    public boolean crearUsuario(String username, String password,
            String nombreCompleto) {
        try {
            if (buscarUsuario(username) != null) {
                return false;
            }

            if (username == null || username.trim().isEmpty()) {
                return false;
            }
            if (password == null || password.length() < 6) {
                return false;
            }
            if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
                return false;
            }

            String hash = hashPassword(password);

            Usuario nuevo = new Usuario(
                    username.trim(), hash, nombreCompleto.trim()
            );

            gestorArchivos.crearCarpetaUsuario(username);
            gestorArchivos.guardar(nuevo,
                    GestorArchivos.getRutaPerfil(username));
            gestorArchivos.guardar(nuevo.getEstadisticas(),
                    GestorArchivos.getRutaEstadisticas(username));
            gestorArchivos.guardar(nuevo.getPreferencias(),
                    GestorArchivos.getRutaPreferencias(username));

            return true;

        } catch (IOException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario buscarUsuario(String username) {
        try {
            String ruta = GestorArchivos.getRutaPerfil(username);
            if (!gestorArchivos.existeArchivo(ruta)) {
                return null;
            }
            return (Usuario) gestorArchivos.cargar(ruta);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validarLogin(String username, String password) {
        try {
            Usuario usuario = buscarUsuario(username);
            if (usuario == null) {
                return false;
            }

            String hash = hashPassword(password);
            if (!usuario.getPasswordHash().equals(hash)) {
                return false;
            }

            usuario.actualizarSesion();
            gestorArchivos.guardar(usuario,
                    GestorArchivos.getRutaPerfil(username));

            this.usuarioActual = usuario;
            return true;

        } catch (IOException e) {
            System.err.println("Error en login: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarUsuario(String username, String password) {
        if (!validarLogin(username, password)) {
            return false;
        }
        this.usuarioActual = null;
        return gestorArchivos.eliminarCarpetaUsuario(username);
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        try {
            gestorArchivos.guardar(usuario,
                    GestorArchivos.getRutaPerfil(usuario.getUsername()));
            return true;
        } catch (IOException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Usuario> obtenerRanking() {
        List<Usuario> lista = new ArrayList<>();
        String[] usernames = gestorArchivos.obtenerUsuariosRegistrados();

        for (String username : usernames) {
            Usuario u = buscarUsuario(username);
            if (u != null) {
                lista.add(u);
            }
        }

        Collections.sort(lista, new Comparator<Usuario>() {
            @Override
            public int compare(Usuario a, Usuario b) {
                return b.getEstadisticas().getPuntajeTotal()
                        - a.getEstadisticas().getPuntajeTotal();
            }
        });

        return lista;
    }

    @Override
    public void registrarPartida(String username,
            HistorialPartida partida) {
        try {
            Usuario usuario = buscarUsuario(username);
            if (usuario == null) {
                return;
            }

            usuario.agregarPartida(partida);

            if (partida.isCompletado()) {
                usuario.getEstadisticas().registrarNivelCompletado(
                        partida.getPuntajeObtenido(),
                        partida.getTiempoEmpleado(),
                        partida.getMovimientos()
                );
                usuario.desbloquearNivel(partida.getNivelJugado() + 1);
            } else {
                usuario.getEstadisticas().registrarFallo();
            }

            gestorArchivos.guardar(usuario,
                    GestorArchivos.getRutaPerfil(username));
            gestorArchivos.guardar(usuario.getEstadisticas(),
                    GestorArchivos.getRutaEstadisticas(username));

            if (usuarioActual != null
                    && usuarioActual.getUsername().equals(username)) {
                this.usuarioActual = usuario;
            }

        } catch (IOException e) {
            System.err.println("Error al registrar partida: "
                    + e.getMessage());
        }
    }

    @Override
    public void actualizarEstadisticas(String username,
            Estadisticas stats) {
        try {
            gestorArchivos.guardar(stats,
                    GestorArchivos.getRutaEstadisticas(username));
        } catch (IOException e) {
            System.err.println("Error al actualizar stats: "
                    + e.getMessage());
        }
    }

    @Override
    public Estadisticas obtenerEstadisticas(String username) {
        try {
            String ruta = GestorArchivos.getRutaEstadisticas(username);
            if (!gestorArchivos.existeArchivo(ruta)) {
                return null;
            }
            return (Estadisticas) gestorArchivos.cargar(ruta);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al obtener stats: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Estadisticas> obtenerRankingGlobal() {
        List<Estadisticas> lista = new ArrayList<>();
        String[] usernames = gestorArchivos.obtenerUsuariosRegistrados();

        for (String username : usernames) {
            Estadisticas e = obtenerEstadisticas(username);
            if (e != null) {
                lista.add(e);
            }
        }

        Collections.sort(lista, new Comparator<Estadisticas>() {
            @Override
            public int compare(Estadisticas a, Estadisticas b) {
                return b.getPuntajeTotal() - a.getPuntajeTotal();
            }
        });

        return lista;
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            return String.valueOf(password.hashCode());
        }
    }

  
    public static boolean validarPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean tieneMayuscula = false;
        boolean tieneNumero = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            }
            if (Character.isDigit(c)) {
                tieneNumero = true;
            }
        }
        return tieneMayuscula && tieneNumero;
    }

    
    public static String getMensajeValidacionPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña no puede estar vacía";
        }
        if (password.length() < 6) {
            return "Mínimo 6 caracteres";
        }
        boolean tieneMayuscula = false;
        boolean tieneNumero = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            }
            if (Character.isDigit(c)) {
                tieneNumero = true;
            }
        }
        if (!tieneMayuscula) {
            return "Debe contener al menos una mayúscula";
        }
        if (!tieneNumero) {
            return "Debe contener al menos un número";
        }
        return "OK";
    }

    // Getter del usuario logueado
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean hayUsuarioLogueado() {
        return usuarioActual != null;
    }
}
