
package com.iud.emergencias.modelo;
/**
 *Define los niveles de severidad de una emergencia.
 *El valor numérico ayuda a calcular prioridades y tiempos.
 */
public enum NivelGravedad {
    LEVE(1),
    MODERADA(2),
    GRAVE(5),
    CRITICA(10);

    private final int valor;

    NivelGravedad(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}