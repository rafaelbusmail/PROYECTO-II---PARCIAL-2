/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.interfaces;

import com.flowfree.modelo.Usuario;
import java.util.List;

public interface IGestionable {

    boolean crearUsuario(String username, String password,
            String nombreCompleto);

    Usuario buscarUsuario(String username);

    boolean validarLogin(String username, String password);

    boolean eliminarUsuario(String username, String password);

    boolean actualizarUsuario(Usuario usuario);

    List<Usuario> obtenerRanking();
}
