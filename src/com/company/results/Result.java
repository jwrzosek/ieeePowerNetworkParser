package com.company.results;

public class Result {
    private String resultName;
    private Double objective;
    private Integer uniformPrice;
    private Double totalBalancingCost;

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

    public Double getTotalBalancingCost() {
        return totalBalancingCost;
    }

    public void setTotalBalancingCost(final Double totalBalancingCost) {
        this.totalBalancingCost = totalBalancingCost;
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
    public Result withTotalBalancingCost(final Double totalBalancingCost) {
        this.totalBalancingCost = totalBalancingCost;
        return this;
    }
}
