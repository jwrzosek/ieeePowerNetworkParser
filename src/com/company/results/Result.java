package com.company.results;

public class Result {
    private Integer nodeNumber;
    private String resultName;
    private Double objective;
    private Integer uniformPrice;
    private Double lmpPrice;
    private Double totalBalancingCost;
    private Double load;

    public Integer getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(final Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
    }


    public Double getLoad() {
        return load;
    }

    public void setLoad(final Double load) {
        this.load = load;
    }
    public void withLoad(final Double load) {
        this.load = load;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(final String resultName) {
        this.resultName = resultName;
    }

    public Double getObjective() {
        return objective;
    }

    public void setObjective(final Double objective) {
        this.objective = objective;
    }

    public Integer getUniformPrice() {
        return uniformPrice;
    }

    public void setUniformPrice(final Integer uniformPrice) {
        this.uniformPrice = uniformPrice;
    }

    public Double getLmpPrice() {
        return lmpPrice;
    }

    public void setLmpPrice(final Double lmpPrice) {
        this.lmpPrice = lmpPrice;
    }

    public Double getTotalBalancingCost() {
        return totalBalancingCost;
    }

    public void setTotalBalancingCost(final Double totalBalancingCost) {
        this.totalBalancingCost = totalBalancingCost;
    }

    public Result withNodeNumber(final Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
        return this;
    }
    public Result withResultName(final String resultName) {
        this.resultName = resultName;
        return this;
    }
    public Result withObjective(final Double objective) {
        this.objective = objective;
        return this;
    }
    public Result withUniformPrice(final Integer uniformPrice) {
        this.uniformPrice = uniformPrice;
        return this;
    }

    public Result withLmpPrice(final Double lmpPrice) {
        this.lmpPrice = lmpPrice;
        return this;
    }

    public Result withTotalBalancingCost(final Double totalBalancingCost) {
        this.totalBalancingCost = totalBalancingCost;
        return this;
    }
}
