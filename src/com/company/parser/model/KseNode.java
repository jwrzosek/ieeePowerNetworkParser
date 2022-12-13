package com.company.parser.model;

import java.util.ArrayList;
import java.util.List;

public class KseNode {

    private Integer number;
    private String name;
    private Double generationCost;
    private Double loadCost;
    private Double loadMW;
    private List<Generator> generators = new ArrayList<>();

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

    public Double getLoadMW() {
        return loadMW;
    }

    public void setLoadMW(final Double loadMW) {
        this.loadMW = loadMW;
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

    public List<Generator> getGenerators() {
        return generators;
    }

    public void setGenerators(final List<Generator> generators) {
        this.generators = generators;
    }

    public void addGenerator(final Generator generator) {
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

    public KseNode withLoadMW(final Double loadMW) {
        this.loadMW = loadMW;
        return this;
    }

    public KseNode withGenerator(final Generator generator) {
        this.generators.add(generator);
        return this;
    }
}
