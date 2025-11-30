
package com.iud.emergencias.tareas;

import com.iud.emergencias.core.SistemaGestion;
import com.iud.emergencias.modelo.Ambulancia;
import com.iud.emergencias.modelo.Emergencia;


/**
 * Simula un despachador que gestiona la asignación de recursos.
 * Actúa como CONSUMIDOR en el modelo Producer-Consumer.
 */
public class DespachadorTask implements Runnable {
    @Override
    public void run() {
        SistemaGestion sistema = SistemaGestion.getInstancia();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 1. Obtener emergencia (Bloquea si no hay)
                Emergencia emergencia = sistema.tomarEmergencia();

                System.out.println("  [PROCESANDO] Despachador buscando ambulancia para " + emergencia);

                // 2. Obtener ambulancia (Bloquea si no hay disponibles)
                Ambulancia ambulancia = sistema.solicitarAmbulancia();

                // 3. Iniciar la atención médica en un hilo separado
                // Esto libera al despachador para atender la siguiente llamada inmediatamente
                Thread hiloAtencion = new Thread(new AtencionMedicaTask(ambulancia, emergencia));
                hiloAtencion.start();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}