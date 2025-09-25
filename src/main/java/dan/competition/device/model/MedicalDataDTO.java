package dan.competition.device.model;

public class MedicalDataDTO {
    private double timeSec;
    private Double uterus;
    private double bpm;

    public MedicalDataDTO() {}

    public MedicalDataDTO(double timeSec, Double uterus, double bpm) {
        this.timeSec = timeSec;
        this.uterus = uterus;
        this.bpm = bpm;
    }

    public double getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(double timeSec) {
        this.timeSec = timeSec;
    }

    public Double getUterus() {
        return uterus;
    }

    public void setUterus(Double uterus) {
        this.uterus = uterus;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
    }
}
