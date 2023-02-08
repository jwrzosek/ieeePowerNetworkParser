package com.company.parser;

import com.company.parser.multicase.MultiCaseWriter;

public class Parser {
    public static void main(String[] args) {
        MultiCaseWriter multiCaseWriter = new MultiCaseWriter();
        multiCaseWriter.runWithHourlyLoads();
    }
}
