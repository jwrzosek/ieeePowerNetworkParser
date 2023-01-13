package com.company.parser.model;

public class Line {
    /**
     * For transformers or phase shifters, the side of the model the non-unity tap is on
     */
    private Integer tapBusNumber;
    /**
     * For transformers and phase shifters, the side of the model the device impedance is on.
     */
    private Integer zBusNumber;
    private Integer loadFlowArea;
    private Integer lossZone;
    /**
     * Use 1 for single lines
     */
    private Integer circuit;
    /**
     * 0 - Transmission line
     * 1 - Fixed tap
     * 2 - Variable tap for voltage control (TCUL, LTC)
     * 3 - Variable tap (turns ratio) for MVAR control
     * 4 - Variable phase angle for MW control (phase shifter)
     */
    private Integer type;
    private Double branchResistanceR;
    private Double branchReactanceX;
    private Double lineChargingB;
    private Integer lineMVARatingNo1;
    private Integer lineMVARatingNo2;
    private Integer lineMVARatingNo3;
    private Integer controlBusNubmer;
    /**
     * 0 - Controlled bus is one of the terminals
     * 1 - Controlled bus is near the tap side
     * 2 - Controlled bus is near the impedance side (Z bus)
     */
    private Integer side;
    private Double transformerFinalTurnsRatio;
    private Double transformerPhaseShifterFinalAngle;
    private Double minimumTapOrPhaseShift;
    private Double maximumTapOrPhaseShift;
    private Double stepSize;
    private Double minimumVoltageMVARorMWLimit;
    private Double maximumVoltageMVARorMWLimit;

    private Double admittance;

    public Integer getTapBusNumber() {
        return tapBusNumber;
    }

    public void setTapBusNumber(final Integer tapBusNumber) {
        this.tapBusNumber = tapBusNumber;
    }

    public Integer getzBusNumber() {
        return zBusNumber;
    }

    public void setzBusNumber(final Integer zBusNumber) {
        this.zBusNumber = zBusNumber;
    }

    public Integer getLoadFlowArea() {
        return loadFlowArea;
    }

    public void setLoadFlowArea(final Integer loadFlowArea) {
        this.loadFlowArea = loadFlowArea;
    }

    public Integer getLossZone() {
        return lossZone;
    }

    public void setLossZone(final Integer lossZone) {
        this.lossZone = lossZone;
    }

    public Integer getCircuit() {
        return circuit;
    }

    public void setCircuit(final Integer circuit) {
        this.circuit = circuit;
    }

    public Integer getType() {
        return type;
    }

    public void setType(final Integer type) {
        this.type = type;
    }

    public Double getBranchResistanceR() {
        return branchResistanceR;
    }

    public void setBranchResistanceR(final Double branchResistanceR) {
        this.branchResistanceR = branchResistanceR;
    }

    public Double getBranchReactanceX() {
        return branchReactanceX;
    }

    public void setBranchReactanceX(final Double branchReactanceX) {
        this.branchReactanceX = branchReactanceX;
    }

    public Double getLineChargingB() {
        return lineChargingB;
    }

    public void setLineChargingB(final Double lineChargingB) {
        this.lineChargingB = lineChargingB;
    }

    public Integer getLineMVARatingNo1() {
        return lineMVARatingNo1;
    }

    public void setLineMVARatingNo1(final Integer lineMVARatingNo1) {
        this.lineMVARatingNo1 = lineMVARatingNo1;
    }

    public Integer getLineMVARatingNo2() {
        return lineMVARatingNo2;
    }

    public void setLineMVARatingNo2(final Integer lineMVARatingNo2) {
        this.lineMVARatingNo2 = lineMVARatingNo2;
    }

    public Integer getLineMVARatingNo3() {
        return lineMVARatingNo3;
    }

    public void setLineMVARatingNo3(final Integer lineMVARatingNo3) {
        this.lineMVARatingNo3 = lineMVARatingNo3;
    }

    public Integer getControlBusNubmer() {
        return controlBusNubmer;
    }

    public void setControlBusNubmer(final Integer controlBusNubmer) {
        this.controlBusNubmer = controlBusNubmer;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(final Integer side) {
        this.side = side;
    }

    public Double getTransformerFinalTurnsRatio() {
        return transformerFinalTurnsRatio;
    }

    public void setTransformerFinalTurnsRatio(final Double transformerFinalTurnsRatio) {
        this.transformerFinalTurnsRatio = transformerFinalTurnsRatio;
    }

    public Double getTransformerPhaseShifterFinalAngle() {
        return transformerPhaseShifterFinalAngle;
    }

    public void setTransformerPhaseShifterFinalAngle(final Double transformerPhaseShifterFinalAngle) {
        this.transformerPhaseShifterFinalAngle = transformerPhaseShifterFinalAngle;
    }

    public Double getMinimumTapOrPhaseShift() {
        return minimumTapOrPhaseShift;
    }

    public void setMinimumTapOrPhaseShift(final Double minimumTapOrPhaseShift) {
        this.minimumTapOrPhaseShift = minimumTapOrPhaseShift;
    }

    public Double getMaximumTapOrPhaseShift() {
        return maximumTapOrPhaseShift;
    }

    public void setMaximumTapOrPhaseShift(final Double maximumTapOrPhaseShift) {
        this.maximumTapOrPhaseShift = maximumTapOrPhaseShift;
    }

    public Double getStepSize() {
        return stepSize;
    }

    public void setStepSize(final Double stepSize) {
        this.stepSize = stepSize;
    }

    public Double getMinimumVoltageMVARorMWLimit() {
        return minimumVoltageMVARorMWLimit;
    }

    public void setMinimumVoltageMVARorMWLimit(final Double minimumVoltageMVARorMWLimit) {
        this.minimumVoltageMVARorMWLimit = minimumVoltageMVARorMWLimit;
    }

    public Double getMaximumVoltageMVARorMWLimit() {
        return maximumVoltageMVARorMWLimit;
    }

    public void setMaximumVoltageMVARorMWLimit(final Double maximumVoltageMVARorMWLimit) {
        this.maximumVoltageMVARorMWLimit = maximumVoltageMVARorMWLimit;
    }

    public Double getAdmittance() {
        return admittance;
    }

    public void setAdmittance(final Double admittance) {
        this.admittance = admittance;
    }

    public Line withTapBusNumber(final Integer tapBusNumber) {
        this.tapBusNumber = tapBusNumber;
        return this;
    }

    public Line withzBusNumber(final Integer zBusNumber) {
        this.zBusNumber = zBusNumber;
        return this;
    }

    public Line withLoadFlowArea(final Integer loadFlowArea) {
        this.loadFlowArea = loadFlowArea;
        return this;
    }

    public Line withLossZone(final Integer lossZone) {
        this.lossZone = lossZone;
        return this;
    }

    public Line withCircuit(final Integer circuit) {
        this.circuit = circuit;
        return this;
    }

    public Line withType(final Integer type) {
        this.type = type;
        return this;
    }

    public Line withBranchResistanceR(final Double branchResistanceR) {
        this.branchResistanceR = branchResistanceR;
        return this;
    }

    public Line withBranchReactanceX(final Double branchReactanceX) {
        this.branchReactanceX = branchReactanceX;
        return this;
    }

    public Line withLineChargingB(final Double lineChargingB) {
        this.lineChargingB = lineChargingB;
        return this;
    }

    public Line withLineMVARatingNo1(final Integer lineMVARatingNo1) {
        this.lineMVARatingNo1 = lineMVARatingNo1;
        return this;
    }

    public Line withLineMVARatingNo2(final Integer lineMVARatingNo2) {
        this.lineMVARatingNo2 = lineMVARatingNo2;
        return this;
    }

    public Line withLineMVARatingNo3(final Integer lineMVARatingNo3) {
        this.lineMVARatingNo3 = lineMVARatingNo3;
        return this;
    }

    public Line withControlBusNubmer(final Integer controlBusNubmer) {
        this.controlBusNubmer = controlBusNubmer;
        return this;
    }

    public Line withSide(final Integer side) {
        this.side = side;
        return this;
    }

    public Line withTransformerFinalTurnsRatio(final Double transformerFinalTurnsRatio) {
        this.transformerFinalTurnsRatio = transformerFinalTurnsRatio;
        return this;
    }

    public Line withTransformerPhaseShifterFinalAngle(final Double transformerPhaseShifterFinalAngle) {
        this.transformerPhaseShifterFinalAngle = transformerPhaseShifterFinalAngle;
        return this;
    }

    public Line withMinimumTapOrPhaseShift(final Double minimumTapOrPhaseShift) {
        this.minimumTapOrPhaseShift = minimumTapOrPhaseShift;
        return this;
    }

    public Line withMaximumTapOrPhaseShift(final Double maximumTapOrPhaseShift) {
        this.maximumTapOrPhaseShift = maximumTapOrPhaseShift;
        return this;
    }

    public Line withStepSize(final Double stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public Line withMinimumVoltageMVARorMWLimit(final Double minimumVoltageMVARorMWLimit) {
        this.minimumVoltageMVARorMWLimit = minimumVoltageMVARorMWLimit;
        return this;
    }

    public Line withMaximumVoltageMVARorMWLimit(final Double maximumVoltageMVARorMWLimit) {
        this.maximumVoltageMVARorMWLimit = maximumVoltageMVARorMWLimit;
        return this;
    }

    public Line withAdmittance(final Double admittance) {
        this.admittance = admittance;
        return this;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "tapBusNumber=" + tapBusNumber +
                ", zBusNumber=" + zBusNumber +
                ", loadFlowArea=" + loadFlowArea +
                ", lossZone=" + lossZone +
                ", circuit=" + circuit +
                ", type=" + type +
                ", branchResistanceR=" + branchResistanceR +
                ", branchReactanceX=" + branchReactanceX +
                ", lineChargingB=" + lineChargingB +
                ", lineMVARatingNo1=" + lineMVARatingNo1 +
                ", lineMVARatingNo2=" + lineMVARatingNo2 +
                ", lineMVARatingNo3=" + lineMVARatingNo3 +
                ", controlBusNubmer=" + controlBusNubmer +
                ", side=" + side +
                ", transformerFinalTurnsRatio=" + transformerFinalTurnsRatio +
                ", transformerPhaseShifterFinalAngle=" + transformerPhaseShifterFinalAngle +
                ", minimumTapOrPhaseShift=" + minimumTapOrPhaseShift +
                ", maximumTapOrPhaseShift=" + maximumTapOrPhaseShift +
                ", stepSize=" + stepSize +
                ", minimumVoltageMVARorMWLimit=" + minimumVoltageMVARorMWLimit +
                ", maximumVoltageMVARorMWLimit=" + maximumVoltageMVARorMWLimit +
                '}';
    }
}
