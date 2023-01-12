package com.company.results;

public class PowerGeneration {

    private String nodeName;
    private String nodeNumber;
    private String periodName;
    private Double generationValue;

    public String getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(final String nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(final String periodName) {
        this.periodName = periodName;
    }

    public Double getGenerationValue() {
        return generationValue;
    }

    public void setGenerationValue(final Double generationValue) {
        this.generationValue = generationValue;
    }

    public PowerGeneration withNodeName(final String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public PowerGeneration withPeriodName(final String periodName) {
        this.periodName = periodName;
        return this;
    }

    public PowerGeneration withGenerationValue(final Double generationValue) {
        this.generationValue = generationValue;
        return this;
    }
    public PowerGeneration withNodeNumber(final String nodeNumber) {
        this.nodeNumber = nodeNumber;
        return this;
    }

    @Override
    public String toString() {
        return "PowerGeneration{" +
                "nodeName='" + nodeName + '\'' +
                ", nodeNumber='" + nodeNumber + '\'' +
                ", periodName='" + periodName + '\'' +
                ", generationValue=" + generationValue +
                '}';
    }
}
