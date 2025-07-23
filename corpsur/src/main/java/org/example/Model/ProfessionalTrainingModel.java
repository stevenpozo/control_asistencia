package org.example.Model;

public class ProfessionalTrainingModel {
    private int id;
    private int professionalId;
    private int trainingId;

    public ProfessionalTrainingModel(int id, int professionalId, int trainingId) {
        this.id = id;
        this.professionalId = professionalId;
        this.trainingId = trainingId;
    }

    public ProfessionalTrainingModel(int professionalId, int trainingId) {
        this.professionalId = professionalId;
        this.trainingId = trainingId;
    }

    public int getId() {
        return id;
    }

    public int getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(int professionalId) {
        this.professionalId = professionalId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }
}
