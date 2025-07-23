package org.example.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceControlModel {
    private int asistenciaDetalleId;
    private int eventoAsistenciaId;
    private String profesionalNombreCompleto;
    private String capacitacionNombre;
    private LocalDate fechaEvento;

    private LocalTime horaEntrada;
    private LocalTime horaSalidaAlmuerzo;
    private LocalTime horaRegresoAlmuerzo;
    private LocalTime horaSalidaFinal;

    private int estado; // 1 = activo, 0 = inactivo

    public AttendanceControlModel(int asistenciaDetalleId, int eventoAsistenciaId, String profesionalNombreCompleto, String capacitacionNombre, LocalDate fechaEvento,
                                  LocalTime horaEntrada, LocalTime horaSalidaAlmuerzo, LocalTime horaRegresoAlmuerzo, LocalTime horaSalidaFinal,
                                  int estado) {
        this.asistenciaDetalleId = asistenciaDetalleId;
        this.eventoAsistenciaId = eventoAsistenciaId;
        this.profesionalNombreCompleto = profesionalNombreCompleto;
        this.capacitacionNombre = capacitacionNombre;
        this.fechaEvento = fechaEvento;
        this.horaEntrada = horaEntrada;
        this.horaSalidaAlmuerzo = horaSalidaAlmuerzo;
        this.horaRegresoAlmuerzo = horaRegresoAlmuerzo;
        this.horaSalidaFinal = horaSalidaFinal;
        this.estado = estado;
    }

    public int getAsistenciaDetalleId() {
        return asistenciaDetalleId;
    }

    public int getEventoAsistenciaId() {
        return eventoAsistenciaId;
    }

    public String getProfesionalNombreCompleto() {
        return profesionalNombreCompleto;
    }

    public String getCapacitacionNombre() {
        return capacitacionNombre;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalTime getHoraSalidaAlmuerzo() {
        return horaSalidaAlmuerzo;
    }

    public LocalTime getHoraRegresoAlmuerzo() {
        return horaRegresoAlmuerzo;
    }

    public LocalTime getHoraSalidaFinal() {
        return horaSalidaFinal;
    }

    public int getEstado() {
        return estado;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public void setHoraSalidaAlmuerzo(LocalTime horaSalidaAlmuerzo) {
        this.horaSalidaAlmuerzo = horaSalidaAlmuerzo;
    }

    public void setHoraRegresoAlmuerzo(LocalTime horaRegresoAlmuerzo) {
        this.horaRegresoAlmuerzo = horaRegresoAlmuerzo;
    }

    public void setHoraSalidaFinal(LocalTime horaSalidaFinal) {
        this.horaSalidaFinal = horaSalidaFinal;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
