package com.company.parser;

import com.company.parser.multicase.KSEMultiCaseWriter;

public class KSEParser {
    public static void main(String[] args) {
        System.out.println("\nHello from KSE parser ;)");

        // one case
        //KSEPowerNetworkParser parser = new KSEPowerNetworkParser();
        //parser.parseMultiStageKSECase("1", "1.dat", false, 1000, 0.50);

        // multiple case scenario

        KSEMultiCaseWriter multiCaseWriter = new KSEMultiCaseWriter();
        multiCaseWriter.runWithHourlyLoads();
    }
}