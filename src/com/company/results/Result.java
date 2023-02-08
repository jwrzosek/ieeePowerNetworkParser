package com.company.results;

public class Result {
    private Integer nodeNumber;
    private String resultName;
    private Double objective;
    private Integer uniformPrice;
    private Double lmpPrice;
    private Double lpPlusPrice;
    private Double totalBalancingCost;
    private Double load;
    private Double totalLoad;
    private Double totalUniformPriceProfit;
    private boolean competitive;

    public boolean isCompetitive() {
        return competitive;
    }

    public void setCompetitive(final boolean competitive) {
        this.competitive = competitive;
    }

    public Double getLpPlusPrice() {
        return lpPlusPrice;
    }

    public void setLpPlusPrice(final Double lpPlusPrice) {
        this.lpPlusPrice = lpPlusPrice;
    }

    public Double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(final Double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public Result withTotalLoad(final Double totalLoad) {
        this.totalLoad = totalLoad;
        return this;
    }

    public Double getTotalUniformPriceProfit() {
        return totalUniformPriceProfit;
    }

    public void setTotalUniformPriceProfit(final Double totalUniformPriceProfit) {
        this.totalUniformPriceProfit = totalUniformPriceProfit;
    }

    public Result withTotalUniformPriceProfit(final Double totalUniformPriceProfit) {
        this.totalUniformPriceProfit = totalUniformPriceProfit;
        return this;
    }

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
    public Result withLpPlusPrice(final Double lpPlusPrice) {
        this.lpPlusPrice = lpPlusPrice;
        return this;
    }
}
