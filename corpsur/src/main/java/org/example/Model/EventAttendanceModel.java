package org.example.Model;

public class EventAttendanceModel {
    private int id;
    private int capacitacionId;
    private String fecha;
    private boolean cerrado;

    // Campos auxiliares para visualización
    private String codigoCapacitacion;
    private String nombreCapacitacion;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacitacionId() {
        return capacitacionId;
    }

    public void setCapacitacionId(int capacitacionId) {
        this.capacitacionId = capacitacionId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isCerrado() {
        return cerrado;
    }

    public void setCerrado(boolean dbValue) {
        this.cerrado = !dbValue; // 👈 INVERTIR porque 1 = ABIERTO
    }

    public String getCodigoCapacitacion() {
        return codigoCapacitacion;
    }

    public void setCodigoCapacitacion(String codigoCapacitacion) {
        this.codigoCapacitacion = codigoCapacitacion;
    }

    public String getNombreCapacitacion() {
        return nombreCapacitacion;
    }

    public void setNombreCapacitacion(String nombreCapacitacion) {
        this.nombreCapacitacion = nombreCapacitacion;
    }
}
