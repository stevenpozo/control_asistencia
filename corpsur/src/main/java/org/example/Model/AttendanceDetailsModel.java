package org.example.Model;

import java.time.LocalTime;

public class AttendanceDetailsModel {
    private int id;
    private int eventId;
    private int professionalId;
    private LocalTime entryTime;
    private LocalTime lunchOutTime;
    private LocalTime lunchReturnTime;
    private LocalTime finalExitTime;
    private int estado; // 1 = abierto, 0 = cerrado

    public AttendanceDetailsModel(int id, int eventId, int professionalId, LocalTime entryTime,
                                  LocalTime lunchOutTime, LocalTime lunchReturnTime,
                                  LocalTime finalExitTime, int estado) {
        this.id = id;
        this.eventId = eventId;
        this.professionalId = professionalId;
        this.entryTime = entryTime;
        this.lunchOutTime = lunchOutTime;
        this.lunchReturnTime = lunchReturnTime;
        this.finalExitTime = finalExitTime;
        this.estado = estado;
    }

    public AttendanceDetailsModel(int eventId, int professionalId) {
        this.eventId = eventId;
        this.professionalId = professionalId;
    }

    public int getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(int professionalId) {
        this.professionalId = professionalId;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalTime getLunchOutTime() {
        return lunchOutTime;
    }

    public void setLunchOutTime(LocalTime lunchOutTime) {
        this.lunchOutTime = lunchOutTime;
    }

    public LocalTime getLunchReturnTime() {
        return lunchReturnTime;
    }

    public void setLunchReturnTime(LocalTime lunchReturnTime) {
        this.lunchReturnTime = lunchReturnTime;
    }

    public LocalTime getFinalExitTime() {
        return finalExitTime;
    }

    public void setFinalExitTime(LocalTime finalExitTime) {
        this.finalExitTime = finalExitTime;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
