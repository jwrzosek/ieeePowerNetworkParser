package com.company.parser.model;

public class KseLine {

    private String fromNodeName;
    private String toNodeName;
    private Integer fromNodeNumber;
    private Integer toNodeNumber;
    private Integer lineCapacity;
    private Double admittance;
    private Integer voltageSource;
    private Integer voltageDestination;

    public String getFromNodeName() {
        return fromNodeName;
    }

    public void setFromNodeName(final String fromNodeName) {
        this.fromNodeName = fromNodeName;
    }

    public String getToNodeName() {
        return toNodeName;
    }

    public void setToNodeName(final String toNodeName) {
        this.toNodeName = toNodeName;
    }

    public Integer getFromNodeNumber() {
        return fromNodeNumber;
    }

    public void setFromNodeNumber(final Integer fromNodeNumber) {
        this.fromNodeNumber = fromNodeNumber;
    }

    public Integer getToNodeNumber() {
        return toNodeNumber;
    }

    public void setToNodeNumber(final Integer toNodeNumber) {
        this.toNodeNumber = toNodeNumber;
    }

    public Integer getLineCapacity() {
        return lineCapacity;
    }

    public void setLineCapacity(final Integer lineCapacity) {
        this.lineCapacity = lineCapacity;
    }

    public Double getAdmittance() {
        return admittance;
    }

    public void setAdmittance(final Double admittance) {
        this.admittance = admittance;
    }

    public Integer getVoltageSource() {
        return voltageSource;
    }

    public void setVoltageSource(final Integer voltageSource) {
        this.voltageSource = voltageSource;
    }

    public Integer getVoltageDestination() {
        return voltageDestination;
    }

    public void setVoltageDestination(final Integer voltageDestination) {
        this.voltageDestination = voltageDestination;
    }

    //-----
    public KseLine withFromNodeName(final String fromNodeName) {
        this.fromNodeName = fromNodeName;
        return this;
    }

    public KseLine withToNodeName(final String toNodeName) {
        this.toNodeName = toNodeName;
        return this;
    }

    public KseLine withFromNodeNumber(final Integer fromNodeNumber) {
        this.fromNodeNumber = fromNodeNumber;
        return this;
    }

    public KseLine withToNodeNumber(final Integer toNodeNumber) {
        this.toNodeNumber = toNodeNumber;
        return this;
    }

    public KseLine withLineCapacity(final Integer lineCapacity) {
        this.lineCapacity = lineCapacity;
        return this;
    }

    public KseLine withAdmittance(final Double admittance) {
        this.admittance = admittance;
        return this;
    }

    public KseLine withVoltageSource(final Integer voltageSource) {
        this.voltageSource = voltageSource;
        return this;
    }

    public KseLine withVoltageDestination(final Integer voltageDestination) {
        this.voltageDestination = voltageDestination;
        return this;
    }
}
