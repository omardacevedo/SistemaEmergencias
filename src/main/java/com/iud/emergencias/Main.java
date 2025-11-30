

package com.iud.emergencias;

import com.iud.emergencias.core.SistemaGestion;

/**
 * Clase principal para ejecutar la simulación del Sistema de Emergencias.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SIMULACIÓN DEL SISTEMA DE EMERGENCIAS ===");
        
        // Obtener instancia y configurar
        SistemaGestion sistema = SistemaGestion.getInstancia();
        
        // Iniciar con 2 operadores y 2 despachadores
        sistema.iniciarComponentes(2, 2);

        // Correr la simulación por 30 segundos
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=== TIEMPO FINALIZADO: DETENIENDO SISTEMA ===");
        sistema.detenerComponentes();
    }
}