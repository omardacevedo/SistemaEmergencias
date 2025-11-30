
package com.iud.emergencias.tareas;

import com.iud.emergencias.core.SistemaGestion;
import com.iud.emergencias.modelo.Ambulancia;
import com.iud.emergencias.modelo.Emergencia;

/**
 * Simula el proceso de viaje, atención y retorno de la ambulancia.
 */
public class AtencionMedicaTask implements Runnable {
    private final Ambulancia ambulancia;
    private final Emergencia emergencia;

    public AtencionMedicaTask(Ambulancia amb, Emergencia emerg) {
        this.ambulancia = amb;
        this.emergencia = emerg;
    }

    @Override
    public void run() {
        try {
            // Calcular tiempos simulados
            double distancia = ambulancia.getUbicacionActual().calcularDistancia(emergencia.getUbicacion());
            int tiempoViaje = (int) (distancia * 10); // Factor arbitrario
            int tiempoAtencion = emergencia.getGravedad().getValor() * 300; // Más grave = más tiempo

            System.out.printf("   --> %s viajando hacia %s (Tiempo est: %dms)%n", 
                    ambulancia, emergencia, (tiempoViaje + tiempoAtencion));

            // Simular el tiempo de operación (Limitado a 5s para no hacer la demo eterna)
            Thread.sleep(Math.min(tiempoViaje + tiempoAtencion, 5000));

            System.out.printf("   <-- %s terminó %s. Disponible nuevamente.%n", ambulancia, "EMG-" + emergencia.getId());
            
            // Actualizar la posición de la ambulancia al lugar del incidente
            ambulancia.setUbicacionActual(emergencia.getUbicacion());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // CRÍTICO: Siempre liberar el recurso al terminar
            SistemaGestion.getInstancia().liberarAmbulancia(ambulancia);
        }
    }
}