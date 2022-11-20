package com.company.parser;

public class Parser {
    public static void main(String[] args) {
        System.out.println("\nHello from parser ;)");
        IEEEPowerNetworkParser parser = new IEEEPowerNetworkParser();
        parser.parse();
    }
}
