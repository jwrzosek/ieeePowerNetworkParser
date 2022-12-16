package com.company.parser;

public class KSEParser {
    public static void main(String[] args) {
        System.out.println("\nHello from KSE parser ;)");
//        KSEMultiCaseWriter multiCaseWriter = new KSEMultiCaseWriter();
//        multiCaseWriter.runWithHourlyLoads();
        KSEPowerNetworkParser parser = new KSEPowerNetworkParser();
        parser.parseMultiStageKSECase("1", "1.dat", false, 1000, 0.50);
    }
}