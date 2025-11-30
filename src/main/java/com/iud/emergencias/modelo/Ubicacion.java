/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iud.emergencias.modelo;

public class Ubicacion {
    private int x;
    private int y;

    public Ubicacion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Calcula la distancia x hacia otra ubicación y
    public double calcularDistancia(Ubicacion otra) {
        return Math.sqrt(Math.pow(this.x - otra.x, 2) + Math.pow(this.y - otra.y, 2));
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}