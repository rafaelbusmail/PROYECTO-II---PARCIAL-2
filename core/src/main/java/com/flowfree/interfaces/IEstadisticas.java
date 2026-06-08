/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowfree.interfaces;

import com.flowfree.modelo.Estadisticas;
import com.flowfree.modelo.HistorialPartida;
import java.util.List;

public interface IEstadisticas {

    void registrarPartida(String username, HistorialPartida partida);

    void actualizarEstadisticas(String username, Estadisticas stats);

    Estadisticas obtenerEstadisticas(String username);

    List<Estadisticas> obtenerRankingGlobal();
}
