package com.company.results;

public class SummaryResult {
    private String dataSetName;

    //---------- Uniform Price Stuff
    private Integer uniformPriceUnconstrained;
    private Integer uniformPriceConstrained;


    //---------- LMP Stuff
    private Double minLmpPrice;
    /**
     * Arithmetic average lmp price
     */
    private Double averageLmpPrice;
    private Double medianLmpPrice;
    private Double maxLmpPrice;
    /**
     * Average price that is payed by buyer in lmp prices system (totalSystemIncomeLMP / totalLoad)
     */
    private Double averageBuyerLmpPrice;


    //---------- LP+ Stuff
    private Double minLpPlusPrice;
    /**
     * Arithmetic average lmp price
     */
    private Double averageLpPlusPrice;
    private Double medianLpPlusPrice;
    private Double maxLpPlusPrice;
    /**
     * Average price that is payed by buyer in lmp prices system (totalSystemIncomeLMP / totalLoad)
     */
    private Double averageBuyerLpPlusPrice;

    private Double totalLoad;

    private Double totalBalancingCostUnconstrained;
    private Double totalBalancingCostConstrained;
    /**
     * Cost of getting constraints into account while balancing the system
     */
    private Double constraintsCost;

    private Double totalSystemIncomeLMP;
    private Double totalSystemIncomeUP;
    private Double totalSystemIncomeLpPlus;

    // nadwyżki w sysemie
    private Double systemSurplusUP;
    private Double systemSurplusLMP;
    private Double systemSurplusLpPlus;

    // zysk dla systemu
    private Double systemProfitUP;
    private Double systemProfitLMP;
    private Double systemProfitLpPlus;

    // zysk dla wytwórców
    private Double suppliersProfitUP;
    private Double suppliersProfitLMP;
    private Double suppliersProfitLpPlus;


    /**
     * Total money difference between uniform prices and lmp
     */
    private Double totalLMPProfitOverUP;
    /**
     * Total money difference between uniform prices and lp+
     */
    private Double totalLpPlusProfitOverUP;

    private Integer numberOfCompetitiveNodes;

    public Integer getNumberOfCompetitiveNodes() {
        return numberOfCompetitiveNodes;
    }

    public void setNumberOfCompetitiveNodes(final Integer numberOfCompetitiveNodes) {
        this.numberOfCompetitiveNodes = numberOfCompetitiveNodes;
    }

    public SummaryResult withNumberOfCompetitiveNodes(final Integer numberOfCompetitiveNodes) {
        this.numberOfCompetitiveNodes = numberOfCompetitiveNodes;
        return this;

    }

    public Double getSystemProfitUP() {
        return systemProfitUP;
    }

    public void setSystemProfitUP(final Double systemProfitUP) {
        this.systemProfitUP = systemProfitUP;
    }

    public Double getSystemProfitLMP() {
        return systemProfitLMP;
    }

    public void setSystemProfitLMP(final Double systemProfitLMP) {
        this.systemProfitLMP = systemProfitLMP;
    }

    public Double getSystemProfitLpPlus() {
        return systemProfitLpPlus;
    }

    public void setSystemProfitLpPlus(final Double systemProfitLpPlus) {
        this.systemProfitLpPlus = systemProfitLpPlus;
    }

    public Double getSuppliersProfitUP() {
        return suppliersProfitUP;
    }

    public void setSuppliersProfitUP(final Double suppliersProfitUP) {
        this.suppliersProfitUP = suppliersProfitUP;
    }

    public Double getSuppliersProfitLMP() {
        return suppliersProfitLMP;
    }

    public void setSuppliersProfitLMP(final Double suppliersProfitLMP) {
        this.suppliersProfitLMP = suppliersProfitLMP;
    }

    public Double getSuppliersProfitLpPlus() {
        return suppliersProfitLpPlus;
    }

    public void setSuppliersProfitLpPlus(final Double suppliersProfitLpPlus) {
        this.suppliersProfitLpPlus = suppliersProfitLpPlus;
    }

    public Double getSystemSurplusUP() {
        return systemSurplusUP;
    }

    public void setSystemSurplusUP(final Double systemSurplusUP) {
        this.systemSurplusUP = systemSurplusUP;
    }

    public Double getSystemSurplusLMP() {
        return systemSurplusLMP;
    }

    public void setSystemSurplusLMP(final Double systemSurplusLMP) {
        this.systemSurplusLMP = systemSurplusLMP;
    }

    public Double getSystemSurplusLpPlus() {
        return systemSurplusLpPlus;
    }

    public void setSystemSurplusLpPlus(final Double systemSurplusLpPlus) {
        this.systemSurplusLpPlus = systemSurplusLpPlus;
    }

    public Double getMinLpPlusPrice() {
        return minLpPlusPrice;
    }

    public void setMinLpPlusPrice(final Double minLpPlusPrice) {
        this.minLpPlusPrice = minLpPlusPrice;
    }

    public Double getAverageLpPlusPrice() {
        return averageLpPlusPrice;
    }

    public void setAverageLpPlusPrice(final Double averageLpPlusPrice) {
        this.averageLpPlusPrice = averageLpPlusPrice;
    }

    public Double getMedianLpPlusPrice() {
        return medianLpPlusPrice;
    }

    public void setMedianLpPlusPrice(final Double medianLpPlusPrice) {
        this.medianLpPlusPrice = medianLpPlusPrice;
    }

    public Double getMaxLpPlusPrice() {
        return maxLpPlusPrice;
    }

    public void setMaxLpPlusPrice(final Double maxLpPlusPrice) {
        this.maxLpPlusPrice = maxLpPlusPrice;
    }

    public Double getAverageBuyerLpPlusPrice() {
        return averageBuyerLpPlusPrice;
    }

    public void setAverageBuyerLpPlusPrice(final Double averageBuyerLpPlusPrice) {
        this.averageBuyerLpPlusPrice = averageBuyerLpPlusPrice;
    }

    public Double getTotalSystemIncomeLpPlus() {
        return totalSystemIncomeLpPlus;
    }

    public void setTotalSystemIncomeLpPlus(final Double totalSystemIncomeLpPlus) {
        this.totalSystemIncomeLpPlus = totalSystemIncomeLpPlus;
    }

    public Double getTotalLpPlusProfitOverUP() {
        return totalLpPlusProfitOverUP;
    }

    public void setTotalLpPlusProfitOverUP(final Double totalLpPlusProfitOverUP) {
        this.totalLpPlusProfitOverUP = totalLpPlusProfitOverUP;
    }

    public Double getConstraintsCost() {
        return constraintsCost;
    }

    public void setConstraintsCost(final Double constraintsCost) {
        this.constraintsCost = constraintsCost;
    }

    public Double getAverageBuyerLmpPrice() {
        return averageBuyerLmpPrice;
    }

    public void setAverageBuyerLmpPrice(final Double averageBuyerLmpPrice) {
        this.averageBuyerLmpPrice = averageBuyerLmpPrice;
    }

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

    public SummaryResult withAverageBuyerLmpPrice(final Double averageBuyerLmpPrice) {
        this.averageBuyerLmpPrice = averageBuyerLmpPrice;
        return this;
    }

    public SummaryResult withConstraintsCost(final Double constraintsCost) {
        this.constraintsCost = constraintsCost;
        return this;
    }

    public SummaryResult withMinLpPlusPrice(final Double minLpPlusPrice) {
        this.minLpPlusPrice = minLpPlusPrice;
        return this;
    }

    public SummaryResult withAverageLpPlusPrice(final Double averageLpPlusPrice) {
        this.averageLpPlusPrice = averageLpPlusPrice;
        return this;
    }

    public SummaryResult withMedianLpPlusPrice(final Double medianLpPlusPrice) {
        this.medianLpPlusPrice = medianLpPlusPrice;
        return this;
    }

    public SummaryResult withMaxLpPlusPrice(final Double maxLpPlusPrice) {
        this.maxLpPlusPrice = maxLpPlusPrice;
        return this;
    }

    public SummaryResult withAverageBuyerLpPlusPrice(final Double averageBuyerLpPlusPrice) {
        this.averageBuyerLpPlusPrice = averageBuyerLpPlusPrice;
        return this;
    }

    public SummaryResult withTotalSystemIncomeLpPlus(final Double totalSystemIncomeLpPlus) {
        this.totalSystemIncomeLpPlus = totalSystemIncomeLpPlus;
        return this;
    }

    public SummaryResult withTotalLpPlusProfitOverUP(final Double totalLpPlusProfitOverUP) {
        this.totalLpPlusProfitOverUP = totalLpPlusProfitOverUP;
        return this;
    }

    public SummaryResult withSystemSurplusUP(final Double systemSurplusUP) {
        this.systemSurplusUP = systemSurplusUP;
        return this;
    }
    public SummaryResult withSystemSurplusLMP(final Double systemSurplusLMP) {
        this.systemSurplusLMP = systemSurplusLMP;
        return this;
    }
    public SummaryResult withSystemSurplusLpPlus(final Double systemSurplusLpPlus) {
        this.systemSurplusLpPlus = systemSurplusLpPlus;
        return this;
    }

    public SummaryResult withSystemProfitUP(final Double systemProfitUP) {
        this.systemProfitUP = systemProfitUP;
        return this;
    }

    public SummaryResult withSystemProfitLMP(final Double systemProfitLMP) {
        this.systemProfitLMP = systemProfitLMP;
        return this;
    }

    public SummaryResult withSystemProfitLpPlus(final Double systemProfitLpPlus) {
        this.systemProfitLpPlus = systemProfitLpPlus;
        return this;
    }

    public SummaryResult withSuppliersProfitUP(final Double suppliersProfitUP) {
        this.suppliersProfitUP = suppliersProfitUP;
        return this;
    }

    public SummaryResult withSuppliersProfitLMP(final Double suppliersProfitLMP) {
        this.suppliersProfitLMP = suppliersProfitLMP;
        return this;
    }

    public SummaryResult withSuppliersProfitLpPlus(final Double suppliersProfitLpPlus) {
        this.suppliersProfitLpPlus = suppliersProfitLpPlus;
        return this;
    }
}
