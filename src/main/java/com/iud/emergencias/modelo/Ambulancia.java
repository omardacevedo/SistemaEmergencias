
package com.iud.emergencias.modelo;

/**
 * Recurso compartido que será gestionado por el sistema.
 */
public class Ambulancia {
    private final int id;
    private EstadoAmbulancia estado;
    private Ubicacion ubicacionActual;
    private int emergenciasAtendidas;

    public Ambulancia(int id) {
        this.id = id;
        this.estado = EstadoAmbulancia.DISPONIBLE;
        this.ubicacionActual = new Ubicacion(0, 0); // Base central
        this.emergenciasAtendidas = 0;
    }

    public void asignarEmergencia() {
        this.estado = EstadoAmbulancia.EN_CAMINO; // Inicia viaje
        this.emergenciasAtendidas++;
    }

    public void liberar() {
        this.estado = EstadoAmbulancia.DISPONIBLE;
    }

    // Getters y Setters necesarios
    public int getId() { return id; }
    public EstadoAmbulancia getEstado() { return estado; }
    public Ubicacion getUbicacionActual() { return ubicacionActual; }
    public void setUbicacionActual(Ubicacion ubicacionActual) { this.ubicacionActual = ubicacionActual; }
    public int getEmergenciasAtendidas() { return emergenciasAtendidas; }

    @Override
    public String toString() {
        return String.format("AMB-%02d (%s)", id, estado);
    }
}