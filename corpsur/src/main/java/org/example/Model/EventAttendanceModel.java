package org.example.Model;

public class EventAttendanceModel {
    private int id;
    private int capacitacionId;
    private String fecha;
    private boolean activo; // ahora refleja la columna "estado"

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

    // Devuelve true si el evento está activo (estado = 1)
    public boolean isActivo() {
        return activo;
    }

    // El valor de la BD es 1 = activo, 0 = inactivo
    public void setEstado(int dbValue) {
        this.activo = (dbValue == 1);
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
