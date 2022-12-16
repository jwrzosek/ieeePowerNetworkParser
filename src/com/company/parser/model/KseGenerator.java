package com.company.parser.model;

public class KseGenerator {

    private Integer nodeNumber;
    private String nodeName;
    private Integer voltage;
    private Integer generationMin;
    private Integer generationMax;
    private String blockName;
    private String powerPlantName;
    private String generatorCompany;

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

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(final Integer voltage) {
        this.voltage = voltage;
    }

    public Integer getGenerationMin() {
        return generationMin;
    }

    public void setGenerationMin(final Integer generationMin) {
        this.generationMin = generationMin;
    }

    public Integer getGenerationMax() {
        return generationMax;
    }

    public void setGenerationMax(final Integer generationMax) {
        this.generationMax = generationMax;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(final String blockName) {
        this.blockName = blockName;
    }

    public String getPowerPlantName() {
        return powerPlantName;
    }

    public void setPowerPlantName(final String powerPlantName) {
        this.powerPlantName = powerPlantName;
    }

    public String getGeneratorCompany() {
        return generatorCompany;
    }

    public void setGeneratorCompany(final String generatorCompany) {
        this.generatorCompany = generatorCompany;
    }

    // ----
    public KseGenerator withNodeNumber(final Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
        return this;
    }

    public KseGenerator withNodeName(final String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public KseGenerator withVoltage(final Integer voltage) {
        this.voltage = voltage;
        return this;
    }

    public KseGenerator withGenerationMin(final Integer generationMin) {
        this.generationMin = generationMin;
        return this;
    }

    public KseGenerator withGenerationMax(final Integer generationMax) {
        this.generationMax = generationMax;
        return this;
    }

    public KseGenerator withBlockName(final String blockName) {
        this.blockName = blockName;
        return this;
    }

    public KseGenerator withPowerPlantName(final String powerPlantName) {
        this.powerPlantName = powerPlantName;
        return this;
    }

    public KseGenerator withGeneratorCompany(final String generatorCompany) {
        this.generatorCompany = generatorCompany;
        return this;
    }
}
