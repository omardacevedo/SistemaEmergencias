package com.iud.emergencias.modelo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa una solicitud de emergencia.
 * Implementa Comparable para que la Cola de Prioridad sepa cómo ordenarlas automáticamente.
 */
public class Emergencia implements Comparable<Emergencia> {
    private static final AtomicInteger contadorIds = new AtomicInteger(1);

    private final int id;
    private final NivelGravedad gravedad;
    private final Ubicacion ubicacion;
    private final long tiempoCreacion;

    public Emergencia(NivelGravedad gravedad, Ubicacion ubicacion) {
        this.id = contadorIds.getAndIncrement();
        this.gravedad = gravedad;
        this.ubicacion = ubicacion;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public NivelGravedad getGravedad() { return gravedad; }
    public Ubicacion getUbicacion() { return ubicacion; }

    /**
     * ALGORITMO DE PRIORIZACIÓN:
     * 1. La gravedad es el factor principal (Mayor gravedad va primero).
     * 2. Si la gravedad es igual, se atiende al que llegó primero (FIFO por tiempo).
     */
    @Override
    public int compareTo(Emergencia otra) {
        // Comparación descendente por valor de gravedad (10 > 1)
        int comparacionGravedad = Integer.compare(otra.gravedad.getValor(), this.gravedad.getValor());

        if (comparacionGravedad != 0) {
            return comparacionGravedad;
        }

        // Si la gravedad es igual, comparación ascendente por tiempo (el más antiguo primero)
        return Long.compare(this.tiempoCreacion, otra.tiempoCreacion);
    }

    @Override
    public String toString() {
        return String.format("EMG-%d [%s] Loc%s", id, gravedad, ubicacion);
    }
}