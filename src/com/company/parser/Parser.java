package com.company.parser;

import com.company.parser.multicase.MultiCaseWriter;

public class Parser {
    public static void main(String[] args) {
        System.out.println("\nHello from parser ;)");
        //IEEEPowerNetworkParser parser = new IEEEPowerNetworkParser();
        //parser.parseMultiStageCase("multiStageCaseTmp.dat", false);

        MultiCaseWriter multiCaseWriter = new MultiCaseWriter();
        multiCaseWriter.runWithHourlyLoads();
    }
}
