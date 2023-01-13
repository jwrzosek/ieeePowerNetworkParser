package com.company.parser.model;

public class HourlyLoad {
    private String period;
    private Integer hour;
    private Double summerPeakLoadPercentageWkdy;
    private Double summerPeakLoadPercentageWknd;
    private Double winterPeakLoadPercentageWkdy;
    private Double winterPeakLoadPercentageWknd;
    private Double springFallPeakLoadPercentageWkdy;
    private Double springFallPeakLoadPercentageWknd;

    public String getPeriod() {
        return period;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(final Integer hour) {
        this.hour = hour;
    }

    public void setPeriod(final String period) {
        this.period = period;
    }

    public Double getSummerPeakLoadPercentageWkdy() {
        return summerPeakLoadPercentageWkdy;
    }

    public void setSummerPeakLoadPercentageWkdy(final Double summerPeakLoadPercentageWkdy) {
        this.summerPeakLoadPercentageWkdy = summerPeakLoadPercentageWkdy;
    }

    public Double getSummerPeakLoadPercentageWknd() {
        return summerPeakLoadPercentageWknd;
    }

    public void setSummerPeakLoadPercentageWknd(final Double summerPeakLoadPercentageWknd) {
        this.summerPeakLoadPercentageWknd = summerPeakLoadPercentageWknd;
    }

    public Double getWinterPeakLoadPercentageWkdy() {
        return winterPeakLoadPercentageWkdy;
    }

    public void setWinterPeakLoadPercentageWkdy(final Double winterPeakLoadPercentageWkdy) {
        this.winterPeakLoadPercentageWkdy = winterPeakLoadPercentageWkdy;
    }

    public Double getWinterPeakLoadPercentageWknd() {
        return winterPeakLoadPercentageWknd;
    }

    public void setWinterPeakLoadPercentageWknd(final Double winterPeakLoadPercentageWknd) {
        this.winterPeakLoadPercentageWknd = winterPeakLoadPercentageWknd;
    }

    public Double getSpringFallPeakLoadPercentageWkdy() {
        return springFallPeakLoadPercentageWkdy;
    }

    public void setSpringFallPeakLoadPercentageWkdy(final Double springFallPeakLoadPercentageWkdy) {
        this.springFallPeakLoadPercentageWkdy = springFallPeakLoadPercentageWkdy;
    }

    public Double getSpringFallPeakLoadPercentageWknd() {
        return springFallPeakLoadPercentageWknd;
    }

    public void setSpringFallPeakLoadPercentageWknd(final Double springFallPeakLoadPercentageWknd) {
        this.springFallPeakLoadPercentageWknd = springFallPeakLoadPercentageWknd;
    }


    public HourlyLoad withPeriod(final String period) {
        this.period = period;
        return this;
    }

    public HourlyLoad withSummerPeakLoadPercentageWkdy(final Double summerPeakLoadPercentageWkdy) {
        this.summerPeakLoadPercentageWkdy = summerPeakLoadPercentageWkdy;
        return this;
    }

    public HourlyLoad withSummerPeakLoadPercentageWknd(final Double summerPeakLoadPercentageWknd) {
        this.summerPeakLoadPercentageWknd = summerPeakLoadPercentageWknd;
        return this;
    }

    public HourlyLoad withWinterPeakLoadPercentageWkdy(final Double winterPeakLoadPercentageWkdy) {
        this.winterPeakLoadPercentageWkdy = winterPeakLoadPercentageWkdy;
        return this;
    }

    public HourlyLoad withWinterPeakLoadPercentageWknd(final Double winterPeakLoadPercentageWknd) {
        this.winterPeakLoadPercentageWknd = winterPeakLoadPercentageWknd;
        return this;
    }

    public HourlyLoad withSpringFallPeakLoadPercentageWkdy(final Double springFallPeakLoadPercentageWkdy) {
        this.springFallPeakLoadPercentageWkdy = springFallPeakLoadPercentageWkdy;
        return this;
    }

    public HourlyLoad withSpringFallPeakLoadPercentageWknd(final Double springFallPeakLoadPercentageWknd) {
        this.springFallPeakLoadPercentageWknd = springFallPeakLoadPercentageWknd;
        return this;
    }

    public HourlyLoad withHour(final Integer hour) {
        this.hour = hour;
        return this;
    }
}
