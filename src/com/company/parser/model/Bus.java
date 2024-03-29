package com.company.parser.model;

public class Bus {
    private Integer busNumber;
    private String busName;
    private Integer loadFlowAreaNumber;
    private Integer lossZoneNumber;
    /**
     * 0 - Unregulated (load, PQ)
     * 1 - Hold MVAR generation within voltage limits, (PQ)
     * 2 - Hold voltage within VAR limits (gen, PV)
     * 3 - Hold voltage and angle (swing, V-Theta) (must always have one)
     */
    private Integer type;
    private Double finalVoltage;
    private Double finalAngle; //degrees
    private Double loadMW;
    private Double loadMVAR;
    private Double generationMW;
    private Double generationMVAR;
    private Double baseKV;
    /**
     *  This is desired remote voltage if this bus is controlling another bus
     */
    private Double desiredVolts;
    private Double maximumMVARorVoltageLimit;
    private Double minimumMVARorVoltageLimit;
    private Double shuntConductanceG;
    private Double shuntSusceptanceB;
    private Integer remoteControlledBusNumber;

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(final Integer busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(final String busName) {
        this.busName = busName;
    }

    public Integer getLoadFlowAreaNumber() {
        return loadFlowAreaNumber;
    }

    public void setLoadFlowAreaNumber(final Integer loadFlowAreaNumber) {
        this.loadFlowAreaNumber = loadFlowAreaNumber;
    }

    public Integer getLossZoneNumber() {
        return lossZoneNumber;
    }

    public void setLossZoneNumber(final Integer lossZoneNumber) {
        this.lossZoneNumber = lossZoneNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(final Integer type) {
        this.type = type;
    }

    public Double getFinalVoltage() {
        return finalVoltage;
    }

    public void setFinalVoltage(final Double finalVoltage) {
        this.finalVoltage = finalVoltage;
    }

    public Double getFinalAngle() {
        return finalAngle;
    }

    public void setFinalAngle(final Double finalAngle) {
        this.finalAngle = finalAngle;
    }

    public Double getLoadMW() {
        return loadMW;
    }

    public void setLoadMW(final Double loadMW) {
        this.loadMW = loadMW;
    }

    public Double getLoadMVAR() {
        return loadMVAR;
    }

    public void setLoadMVAR(final Double loadMVAR) {
        this.loadMVAR = loadMVAR;
    }

    public Double getGenerationMW() {
        return generationMW;
    }

    public void setGenerationMW(final Double generationMW) {
        this.generationMW = generationMW;
    }

    public Double getGenerationMVAR() {
        return generationMVAR;
    }

    public void setGenerationMVAR(final Double generationMVAR) {
        this.generationMVAR = generationMVAR;
    }

    public Double getBaseKV() {
        return baseKV;
    }

    public void setBaseKV(final Double baseKV) {
        this.baseKV = baseKV;
    }

    public Double getDesiredVolts() {
        return desiredVolts;
    }

    public void setDesiredVolts(final Double desiredVolts) {
        this.desiredVolts = desiredVolts;
    }

    public Double getMaximumMVARorVoltageLimit() {
        return maximumMVARorVoltageLimit;
    }

    public void setMaximumMVARorVoltageLimit(final Double maximumMVARorVoltageLimit) {
        this.maximumMVARorVoltageLimit = maximumMVARorVoltageLimit;
    }

    public Double getMinimumMVARorVoltageLimit() {
        return minimumMVARorVoltageLimit;
    }

    public void setMinimumMVARorVoltageLimit(final Double minimumMVARorVoltageLimit) {
        this.minimumMVARorVoltageLimit = minimumMVARorVoltageLimit;
    }

    public Double getShuntConductanceG() {
        return shuntConductanceG;
    }

    public void setShuntConductanceG(final Double shuntConductanceG) {
        this.shuntConductanceG = shuntConductanceG;
    }

    public Double getShuntSusceptanceB() {
        return shuntSusceptanceB;
    }

    public void setShuntSusceptanceB(final Double shuntSusceptanceB) {
        this.shuntSusceptanceB = shuntSusceptanceB;
    }

    public Integer getRemoteControlledBusNumber() {
        return remoteControlledBusNumber;
    }

    public Bus withBusNumber(final Integer busNumber) {
        this.busNumber = busNumber;
        return this;
    }

    public Bus withBusName(final String busName) {
        this.busName = busName;
        return this;
    }

    public Bus withLoadFlowAreaNumber(final Integer loadFlowAreaNumber) {
        this.loadFlowAreaNumber = loadFlowAreaNumber;
        return this;
    }

    public Bus withLossZoneNumber(final Integer lossZoneNumber) {
        this.lossZoneNumber = lossZoneNumber;
        return this;
    }

    public Bus withType(final Integer type) {
        this.type = type;
        return this;
    }

    public Bus withFinalVoltage(final Double finalVoltage) {
        this.finalVoltage = finalVoltage;
        return this;
    }

    public Bus withFinalAngle(final Double finalAngle) {
        this.finalAngle = finalAngle;
        return this;
    }

    public Bus withLoadMW(final Double loadMW) {
        this.loadMW = loadMW;
        return this;
    }

    public Bus withLoadMVAR(final Double loadMVAR) {
        this.loadMVAR = loadMVAR;
        return this;
    }

    public Bus withGenerationMW(final Double generationMW) {
        this.generationMW = generationMW;
        return this;
    }

    public Bus withGenerationMVAR(final Double generationMVAR) {
        this.generationMVAR = generationMVAR;
        return this;
    }

    public Bus withBaseKV(final Double baseKV) {
        this.baseKV = baseKV;
        return this;
    }

    public Bus withDesiredVolts(final Double desiredVolts) {
        this.desiredVolts = desiredVolts;
        return this;
    }

    public Bus withMaximumMVARorVoltageLimit(final Double maximumMVARorVoltageLimit) {
        this.maximumMVARorVoltageLimit = maximumMVARorVoltageLimit;
        return this;
    }

    public Bus withMinimumMVARorVoltageLimit(final Double minimumMVARorVoltageLimit) {
        this.minimumMVARorVoltageLimit = minimumMVARorVoltageLimit;
        return this;
    }

    public Bus withShuntConductanceG(final Double shuntConductanceG) {
        this.shuntConductanceG = shuntConductanceG;
        return this;
    }

    public Bus withShuntSusceptanceB(final Double shuntSusceptanceB) {
        this.shuntSusceptanceB = shuntSusceptanceB;
        return this;
    }

    public Bus withRemoteControlledBusNumber(final Integer remoteControlledBusNumber) {
        this.remoteControlledBusNumber = remoteControlledBusNumber;
        return this;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "busNumber=" + busNumber +
                ", busName='" + busName + '\'' +
                ", loadFlowAreaNumber=" + loadFlowAreaNumber +
                ", lossZoneNumber=" + lossZoneNumber +
                ", type=" + type +
                ", finalVoltage=" + finalVoltage +
                ", finalAngle=" + finalAngle +
                ", loadMW=" + loadMW +
                ", loadMVAR=" + loadMVAR +
                ", generationMW=" + generationMW +
                ", generationMVAR=" + generationMVAR +
                ", baseKV=" + baseKV +
                ", desiredVolts=" + desiredVolts +
                ", maximumMVARorVoltageLimit=" + maximumMVARorVoltageLimit +
                ", minimumMVARorVoltageLimit=" + minimumMVARorVoltageLimit +
                ", shuntConductanceG=" + shuntConductanceG +
                ", shuntSusceptanceB=" + shuntSusceptanceB +
                ", remoteControlledBusNumber=" + remoteControlledBusNumber +
                '}';
    }
}
