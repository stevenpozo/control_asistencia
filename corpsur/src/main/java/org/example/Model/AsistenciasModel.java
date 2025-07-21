// AsistenciasModel.java
package org.example.Model;

import java.time.LocalDate;

public class AsistenciasModel {
    private int id;
    private LocalDate fecha;
    private String tipo;
    private boolean estado;  // true = abierta, false = cerrada

    public AsistenciasModel(int id, LocalDate fecha, String tipo, boolean estado) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.estado = estado;
    }

    public AsistenciasModel(LocalDate fecha, String tipo, boolean estado) {
        this(0, fecha, tipo, estado);
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "AsistenciasModel{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", tipo='" + tipo + '\'' +
                ", estado=" + (estado ? "abierta" : "cerrada") +
                '}';
    }
}
