package com.flowfree.datos;

import com.flowfree.enums.Idioma;


public class Traductor {

    public static String t(Idioma idioma, String es, String en) {
        return idioma == Idioma.EN ? en : es;
    }

    public static String jugar(Idioma i) { return t(i, "JUGAR", "PLAY"); }
    public static String miPerfil(Idioma i) { return t(i, "MI PERFIL", "MY PROFILE"); }
    public static String ranking(Idioma i) { return t(i, "RANKING", "RANKING"); }
    public static String cerrarSesion(Idioma i) { return t(i, "CERRAR SESION", "LOG OUT"); }
    public static String salir(Idioma i) { return t(i, "SALIR", "EXIT"); }
    public static String hola(Idioma i) { return t(i, "Hola", "Hello"); }
    public static String nivel(Idioma i) { return t(i, "Nivel", "Level"); }
    public static String partidas(Idioma i) { return t(i, "Partidas", "Games"); }
    public static String tiempo(Idioma i) { return t(i, "Tiempo", "Time"); }
    public static String mejor(Idioma i) { return t(i, "Mejor", "Best"); }

    public static String miPerfilTitulo(Idioma i) { return t(i, "MI PERFIL", "MY PROFILE"); }
    public static String clickAvatar(Idioma i) { return t(i, "Click avatar para cambiar", "Click avatar to change"); }
    public static String registrado(Idioma i) { return t(i, "Registrado", "Registered"); }
    public static String ultimaSesion(Idioma i) { return t(i, "Ultima sesion", "Last session"); }
    public static String nivelAlcanzado(Idioma i) { return t(i, "Nivel alcanzado", "Level reached"); }
    public static String partidasJugadas(Idioma i) { return t(i, "Partidas jugadas", "Games played"); }
    public static String mejorPuntaje(Idioma i) { return t(i, "Mejor puntaje", "Best score"); }
    public static String puntajeTotal(Idioma i) { return t(i, "Puntaje total", "Total score"); }
    public static String preferencias(Idioma i) { return t(i, "PREFERENCIAS", "PREFERENCES"); }
    public static String amigos(Idioma i) { return t(i, "AMIGOS", "FRIENDS"); }
    public static String desactivarCuenta(Idioma i) { return t(i, "DESACTIVAR CUENTA", "DEACTIVATE ACCOUNT"); }
    public static String avatarActualizado(Idioma i) { return t(i, "Avatar actualizado", "Avatar updated"); }
    public static String volver(Idioma i) { return t(i, "VOLVER", "BACK"); }
    public static String seleccionaAvatar(Idioma i) { return t(i, "SELECCIONA AVATAR", "SELECT AVATAR"); }
    public static String clickFueraArchivo(Idioma i) { return t(i, "Click fuera para abrir archivo", "Click outside to open file"); }

    public static String preferenciasTitulo(Idioma i) { return t(i, "PREFERENCIAS", "PREFERENCES"); }
    public static String volumenMusica(Idioma i) { return t(i, "Volumen Musica", "Music Volume"); }
    public static String idioma(Idioma i) { return t(i, "Idioma", "Language"); }
    public static String guardarYVolver(Idioma i) { return t(i, "GUARDAR Y VOLVER", "SAVE AND BACK"); }

    public static String misAmigos(Idioma i) { return t(i, "MIS AMIGOS", "MY FRIENDS"); }
    public static String solicitudes(Idioma i) { return t(i, "SOLICITUDES", "REQUESTS"); }
    public static String agregarAmigo(Idioma i) { return t(i, "AGREGAR AMIGO", "ADD FRIEND"); }
    public static String solicitudesPendientes(Idioma i) { return t(i, "SOLICITUDES PENDIENTES", "PENDING REQUESTS"); }
    public static String noSolicitudes(Idioma i) { return t(i, "No tienes solicitudes pendientes", "No pending requests"); }
    public static String clickAceptar(Idioma i) { return t(i, "Click en un username para aceptar", "Click a username to accept"); }
    public static String compararEstadisticas(Idioma i) { return t(i, "COMPARAR ESTADISTICAS", "COMPARE STATS"); }
    public static String amigo(Idioma i) { return t(i, "AMIGO", "FRIEND"); }
    public static String nivelAbr(Idioma i) { return t(i, "NIVEL", "LEVEL"); }
    public static String puntos(Idioma i) { return t(i, "PUNTOS", "POINTS"); }
    public static String comp(Idioma i) { return t(i, "COMP", "CMP"); }
    public static String noAmigos(Idioma i) { return t(i, "No tienes amigos agregados", "No friends added"); }
    public static String usarAgregar(Idioma i) { return t(i, "Usa el boton AGREGAR para anadir amigos por username", "Use ADD button to add friends by username"); }
    public static String clickComparar(Idioma i) { return t(i, "Click [C] para comparar", "Click [C] to compare"); }
    public static String retar(Idioma i) { return t(i, "RETAR", "CHALLENGE"); }
    public static String retarA(Idioma i) { return t(i, "RETAR A", "CHALLENGE"); }
    public static String seleccionaNivelReto(Idioma i) { return t(i, "Selecciona el nivel para el reto (1-5):", "Select level for challenge (1-5):"); }
    public static String nivelActual(Idioma i) { return t(i, "Nivel actual", "Current level"); }
    public static String retoInst(Idioma i) { return t(i, "Usa [1]-[5] para elegir nivel, [ENTER] para enviar, [ESC] cancelar", "Use [1]-[5] to select level, [ENTER] to send, [ESC] cancel"); }
    public static String solicitudEnviada(Idioma i) { return t(i, "Solicitud enviada a", "Request sent to"); }
    public static String usuarioNoEncontrado(Idioma i) { return t(i, "Usuario no encontrado o solicitud ya enviada", "User not found or request already sent"); }
    public static String aceptar(Idioma i) { return t(i, "ACEPTAR", "ACCEPT"); }
    public static String rechazar(Idioma i) { return t(i, "RECHAZAR", "REJECT"); }
    public static String solicitudAceptada(Idioma i) { return t(i, "Solicitud aceptada", "Request accepted"); }
    public static String solicitudRechazada(Idioma i) { return t(i, "Solicitud rechazada", "Request rejected"); }
    public static String errorAceptar(Idioma i) { return t(i, "Error al aceptar", "Error accepting"); }
    public static String amigoEliminado(Idioma i) { return t(i, "Amigo eliminado", "Friend removed"); }

    public static String nivelCompletado(Idioma i) { return t(i, "NIVEL COMPLETADO", "LEVEL COMPLETED"); }
    public static String tiempoAgotado(Idioma i) { return t(i, "TIEMPO AGOTADO", "TIME'S UP"); }
    public static String siguiente(Idioma i) { return t(i, "Siguiente", "Next"); }
    public static String reiniciar(Idioma i) { return t(i, "Reiniciar", "Restart"); }
    public static String menu(Idioma i) { return t(i, "Menu", "Menu"); }
    public static String relleno(Idioma i) { return t(i, "Relleno", "Fill"); }
    public static String vidas(Idioma i) { return t(i, "Vidas", "Lives"); }
    public static String movimientos(Idioma i) { return t(i, "Mov", "Mov"); }
    public static String retoEnviadoA(Idioma i) { return t(i, "RETO ENVIADO a", "CHALLENGE SENT to"); }
    public static String retos(Idioma i) { return t(i, "RETOS", "CHALLENGES"); }
    public static String retosPendientes(Idioma i) { return t(i, "RETOS PENDIENTES", "PENDING CHALLENGES"); }
    public static String noRetos(Idioma i) { return t(i, "No tienes retos pendientes", "No pending challenges"); }
    public static String retoCompletado(Idioma i) { return t(i, "RETO COMPLETADO", "CHALLENGE COMPLETED"); }
    public static String ganador(Idioma i) { return t(i, "GANADOR", "WINNER"); }
    public static String empate(Idioma i) { return t(i, "EMPATE", "TIE"); }

    public static String rankingGlobal(Idioma i) { return t(i, "RANKING GLOBAL", "GLOBAL RANKING"); }
    public static String jugador(Idioma i) { return t(i, "JUGADOR", "PLAYER"); }
    public static String puntosRank(Idioma i) { return t(i, "PUNTOS", "POINTS"); }
    public static String sinJugadores(Idioma i) { return t(i, "Sin jugadores registrados aun", "No players registered yet"); }

    public static String usuario(Idioma i) { return t(i, "Usuario", "Username"); }
    public static String contrasena(Idioma i) { return t(i, "Contrasena", "Password"); }
    public static String iniciarSesion(Idioma i) { return t(i, "INICIAR SESION", "LOG IN"); }
    public static String registrarse(Idioma i) { return t(i, "REGISTRARSE", "SIGN UP"); }
    public static String confirmarContrasena(Idioma i) { return t(i, "Confirmar contrasena", "Confirm password"); }
    public static String nombreCompleto(Idioma i) { return t(i, "Nombre completo", "Full name"); }
    public static String completarCampos(Idioma i) { return t(i, "Completa todos los campos", "Fill all fields"); }
    public static String credencialesIncorrectas(Idioma i) { return t(i, "Usuario o contrasena incorrectos", "Invalid username or password"); }
    public static String mostrar(Idioma i) { return t(i, "Mostrar", "Show"); }
    public static String ocultar(Idioma i) { return t(i, "Ocultar", "Hide"); }
    public static String elegirDisco(Idioma i) { return t(i, "[F] Elegir desde disco", "[F] Choose from disk"); }
    public static String reactivarCuenta(Idioma i) { return t(i, "REACTIVAR CUENTA", "REACTIVATE ACCOUNT"); }
    public static String cuentaInactiva(Idioma i) { return t(i, "Cuenta desactivada", "Deactivated account"); }
}
