package com.company.parser;

import com.company.parser.multicase.KSEMultiCaseWriter;

public class KSEParser {
    public static void main(String[] args) {
        System.out.println("\nHello from KSE parser ;)");

        // multiple case scenario with demand peaks
        KSEMultiCaseWriter multiCaseWriter = new KSEMultiCaseWriter();
        multiCaseWriter.runWithKSEDemandPeaks();
    }
}