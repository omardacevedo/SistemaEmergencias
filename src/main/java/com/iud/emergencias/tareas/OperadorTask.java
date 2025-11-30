package com.iud.emergencias.tareas;
import com.iud.emergencias.core.SistemaGestion;
import com.iud.emergencias.modelo.Emergencia;
import com.iud.emergencias.modelo.NivelGravedad;
import com.iud.emergencias.modelo.Ubicacion;
import java.util.Random;

/**
 * Simula un operador telefónico que recibe llamadas y crea emergencias.
 * Actúa como PRODUCTOR en el modelo Producer-Consumer.
 */
public class OperadorTask implements Runnable {
    private final Random random = new Random();

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Simular tiempo aleatorio entre llamadas (1 a 3 segundos)
                Thread.sleep(random.nextInt(2000) + 1000);

                // Generar datos aleatorios
                NivelGravedad gravedad = NivelGravedad.values()[random.nextInt(NivelGravedad.values().length)];
                Ubicacion ubicacion = new Ubicacion(random.nextInt(100), random.nextInt(100));
                
                Emergencia nuevaEmergencia = new Emergencia(gravedad, ubicacion);
                
                // Enviar al sistema
                SistemaGestion.getInstancia().reportarEmergencia(nuevaEmergencia);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}