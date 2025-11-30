
package com.iud.emergencias.tareas;

import com.iud.emergencias.core.SistemaGestion;
import com.iud.emergencias.modelo.Ambulancia;
import com.iud.emergencias.modelo.EstadoAmbulancia;
import java.util.List;

/**
 * Hilo de monitoreo que imprime el estado del sistema periódicamente.
 */
public class MonitorTask implements Runnable {
    @Override
    public void run() {
        SistemaGestion sys = SistemaGestion.getInstancia();
        List<Ambulancia> flota = sys.getEstadoFlota();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ MONITOR DE ESTADO ================\n");
        sb.append("Emergencias Pendientes: ").append(sys.getCantidadPendientes()).append("\n");
        sb.append("Estado de Flota:\n");
        
        for (Ambulancia a : flota) {
            String estadoStr = (a.getEstado() == EstadoAmbulancia.DISPONIBLE) ? "[LIBRE]" : "[OCUPADA]";
            sb.append(String.format("  - AMB-%02d: %-10s (Atendidas: %d)%n", 
                    a.getId(), estadoStr, a.getEmergenciasAtendidas()));
        }
        sb.append("===================================================\n");
        
        System.out.println(sb.toString());
    }
}