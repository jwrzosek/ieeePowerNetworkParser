package com.company.results;

public class NodeGeneration {

    private String nodeName;
    private Integer nodeNumber;
    private String periodName;
    private Double generationValue;
    private Double nodalGenerationCost;

    public Integer getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(final Integer nodeNumber) {
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

    public Double getNodalGenerationCost() {
        return nodalGenerationCost;
    }

    public void setNodalGenerationCost(final Double nodalGenerationCost) {
        this.nodalGenerationCost = nodalGenerationCost;
    }

    public NodeGeneration withNodalGenerationCost(final Double nodalGenerationCost) {
        this.nodalGenerationCost = nodalGenerationCost;
        return this;
    }

    public NodeGeneration withNodeName(final String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public NodeGeneration withPeriodName(final String periodName) {
        this.periodName = periodName;
        return this;
    }

    public NodeGeneration withGenerationValue(final Double generationValue) {
        this.generationValue = generationValue;
        return this;
    }
    public NodeGeneration withNodeNumber(final Integer nodeNumber) {
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
