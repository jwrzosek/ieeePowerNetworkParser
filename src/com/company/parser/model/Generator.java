package com.company.parser.model;

public class Generator {
    private Integer busNumber;
    private Double generationMW;
    private Integer variableCost;

    public Integer getVariableCost() {
        return variableCost;
    }

    public void setVariableCost(final Integer variableCost) {
        this.variableCost = variableCost;
    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(final Integer busNumber) {
        this.busNumber = busNumber;
    }

    public Double getGenerationMW() {
        return generationMW;
    }

    public void setGenerationMW(final Double generationMW) {
        this.generationMW = generationMW;
    }

    public Generator withBusNumber(final Integer busNumber) {
        this.busNumber = busNumber;
        return this;
    }

    public Generator withGenerationMW(final Double generationMW) {
        this.generationMW = generationMW;
        return this;
    }

    public Generator withVariableCost(final Integer variableCost) {
        this.variableCost = variableCost;
        return this;
    }
}
