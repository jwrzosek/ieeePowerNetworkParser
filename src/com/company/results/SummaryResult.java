package com.company.results;

public class SummaryResult {
    private String dataSetName;

    private Integer uniformPriceUnconstrained;
    private Integer uniformPriceConstrained;

    private Double minLmpPrice;
    /**
     * Arithmetic average lmp price
     */
    private Double averageLmpPrice;
    private Double medianLmpPrice;
    private Double maxLmpPrice;

    private Double totalLoad;

    private Double totalBalancingCostUnconstrained;
    private Double totalBalancingCostConstrained;

    private Double totalSystemIncomeLMP;
    private Double totalSystemIncomeUP;

    /**
     * Total money difference between uniform prices and lmp 
     */
    private Double totalLMPProfitOverUP;


    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(final String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public Double getAverageLmpPrice() {
        return averageLmpPrice;
    }

    public void setAverageLmpPrice(final Double averageLmpPrice) {
        this.averageLmpPrice = averageLmpPrice;
    }

    public Double getMinLmpPrice() {
        return minLmpPrice;
    }

    public void setMinLmpPrice(final Double minLmpPrice) {
        this.minLmpPrice = minLmpPrice;
    }

    public Double getMedianLmpPrice() {
        return medianLmpPrice;
    }

    public void setMedianLmpPrice(final Double medianLmpPrice) {
        this.medianLmpPrice = medianLmpPrice;
    }

    public Double getMaxLmpPrice() {
        return maxLmpPrice;
    }

    public void setMaxLmpPrice(final Double maxLmpPrice) {
        this.maxLmpPrice = maxLmpPrice;
    }

    public Double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(final Double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public Integer getUniformPriceUnconstrained() {
        return uniformPriceUnconstrained;
    }

    public void setUniformPriceUnconstrained(final Integer uniformPriceUnconstrained) {
        this.uniformPriceUnconstrained = uniformPriceUnconstrained;
    }

    public Integer getUniformPriceConstrained() {
        return uniformPriceConstrained;
    }

    public void setUniformPriceConstrained(final Integer uniformPriceConstrained) {
        this.uniformPriceConstrained = uniformPriceConstrained;
    }

    public Double getTotalSystemIncomeUP() {
        return totalSystemIncomeUP;
    }

    public void setTotalSystemIncomeUP(final Double totalSystemIncomeUP) {
        this.totalSystemIncomeUP = totalSystemIncomeUP;
    }

    public Double getTotalSystemIncomeLMP() {
        return totalSystemIncomeLMP;
    }

    public void setTotalSystemIncomeLMP(final Double totalSystemIncomeLMP) {
        this.totalSystemIncomeLMP = totalSystemIncomeLMP;
    }

    public Double getTotalBalancingCostConstrained() {
        return totalBalancingCostConstrained;
    }

    public void setTotalBalancingCostConstrained(final Double totalBalancingCostConstrained) {
        this.totalBalancingCostConstrained = totalBalancingCostConstrained;
    }

    public Double getTotalBalancingCostUnconstrained() {
        return totalBalancingCostUnconstrained;
    }

    public void setTotalBalancingCostUnconstrained(final Double totalBalancingCostUnconstrained) {
        this.totalBalancingCostUnconstrained = totalBalancingCostUnconstrained;
    }

    public Double getTotalLMPProfitOverUP() {
        return totalLMPProfitOverUP;
    }

    public void setTotalLMPProfitOverUP(final Double totalLMPProfitOverUP) {
        this.totalLMPProfitOverUP = totalLMPProfitOverUP;
    }

    //---------
    public SummaryResult withUniformPriceUnconstrained(final Integer uniformPriceUnconstrained) {
        this.uniformPriceUnconstrained = uniformPriceUnconstrained;
        return this;
    }

    public SummaryResult withUniformPriceConstrained(final Integer uniformPriceConstrained) {
        this.uniformPriceConstrained = uniformPriceConstrained;
        return this;
    }

    public SummaryResult withTotalSystemIncomeUP(final Double totalSystemIncomeUP) {
        this.totalSystemIncomeUP = totalSystemIncomeUP;
        return this;
    }

    public SummaryResult withTotalSystemIncomeLMP(final Double totalSystemIncomeLMP) {
        this.totalSystemIncomeLMP = totalSystemIncomeLMP;
        return this;
    }

    public SummaryResult withTotalBalancingCostConstrained(final Double totalBalancingCostConstrained) {
        this.totalBalancingCostConstrained = totalBalancingCostConstrained;
        return this;
    }

    public SummaryResult withTotalBalancingCostUnconstrained(final Double totalBalancingCostUnconstrained) {
        this.totalBalancingCostUnconstrained = totalBalancingCostUnconstrained;
        return this;
    }

    public SummaryResult withTotalLMPProfitOverUP(final Double totalLMPProfitOverUP) {
        this.totalLMPProfitOverUP = totalLMPProfitOverUP;
        return this;
    }

    public SummaryResult withTotalLoad(final Double totalLoad) {
        this.totalLoad = totalLoad;
        return this;
    }

    public SummaryResult withMinLmpPrice(final Double minLmpPrice) {
        this.minLmpPrice = minLmpPrice;
        return this;
    }
    public SummaryResult withMedianLmpPrice(final Double medianLmpPrice) {
        this.medianLmpPrice = medianLmpPrice;
        return this;
    }
    public SummaryResult withMaxLmpPrice(final Double maxLmpPrice) {
        this.maxLmpPrice = maxLmpPrice;
        return this;
    }

    public SummaryResult withAverageLmpPrice(final Double averageLmpPrice) {
        this.averageLmpPrice = averageLmpPrice;
        return this;
    }

    public SummaryResult withDataSetName(final String dataSetName) {
        this.dataSetName = dataSetName;
        return this;
    }
}
