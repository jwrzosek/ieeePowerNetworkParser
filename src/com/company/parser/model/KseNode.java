package com.company.parser.model;

import java.util.ArrayList;
import java.util.List;

public class KseNode {

    private Integer number;
    private String name;
    private Double generationCost;
    private Double loadCost;
    private Double loadMin;
    private Double loadSr;
    private Double loadMax;
    private Double demandShare;
    private List<KseGenerator> generators = new ArrayList<>();

    public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getGenerationCost() {
        return generationCost;
    }

    public void setGenerationCost(final Double generationCost) {
        this.generationCost = generationCost;
    }

    public Double getLoadCost() {
        return loadCost;
    }

    public void setLoadCost(final Double loadCost) {
        this.loadCost = loadCost;
    }

    public Double getLoadSr() {
        return loadSr;
    }

    public void setLoadSr(final Double loadSr) {
        this.loadSr = loadSr;
    }

    public List<KseGenerator> getGenerators() {
        return generators;
    }

    public void setGenerators(final List<KseGenerator> generators) {
        this.generators = generators;
    }

    public Double getLoadMin() {
        return loadMin;
    }

    public void setLoadMin(final Double loadMin) {
        this.loadMin = loadMin;
    }

    public Double getLoadMax() {
        return loadMax;
    }

    public void setLoadMax(final Double loadMax) {
        this.loadMax = loadMax;
    }

    public Double getDemandShare() {
        return demandShare;
    }

    public void setDemandShare(final Double demandShare) {
        this.demandShare = demandShare;
    }

    // ---
    public KseNode withNumber(final Integer number) {
        this.number = number;
        return this;
    }

    public KseNode withName(final String name) {
        this.name = name;
        return this;
    }

    public void addGenerator(final KseGenerator generator) {
        this.generators.add(generator);
    }

    public KseNode withGenerationCost(final Double generationCost) {
        this.generationCost = generationCost;
        return this;
    }

    public KseNode withLoadCost(final Double loadCost) {
        this.loadCost = loadCost;
        return this;
    }

    public KseNode withLoadSr(final Double loadSr) {
        this.loadSr = loadSr;
        return this;
    }

    public KseNode withLoadMin(final Double loadMin) {
        this.loadMin = loadMin;
        return this;
    }

    public KseNode withLoadMax(final Double loadMax) {
        this.loadMax = loadMax;
        return this;
    }

    public KseNode withGenerator(final KseGenerator generator) {
        this.generators.add(generator);
        return this;
    }

    public KseNode withDemandShare(final Double demandShare) {
        this.demandShare = demandShare;
        return this;
    }
}
