package com.company;

import com.company.modelwriter.NetworkParams;
import com.company.modelwriter.PowerTransferDistributionFactors;

public class Main {

    private static final int LINES_QUANTITY = 14;
    private static final String LINES_SYMBOL = "L";

    private static final int NODES_QUANTITY = 8;
    private static final String NODES_SYMBOL = "N";

    public static void main(String[] args) {
        final var fileName = "py" + NODES_QUANTITY + ".dat";
        final var fileName2 = "py.dat";
        //ModelWriter2.writeModelToFile(fileName2, NODES_QUANTITY, NODES_QUANTITY);
//        System.out.println(PowerTransferDistributionFactors.stringBuilder.toString());

        printInfo(PowerTransferDistributionFactors.generatePTDFs(14, 8));
    }

    private static void printModel() {
        printInfo(NetworkParams.writeSet("NODES", NODES_QUANTITY, NODES_SYMBOL));
        printInfo(NetworkParams.writeSet("LINES", LINES_QUANTITY, LINES_SYMBOL));
        printInfo(NetworkParams.writeParameter("line_limit", LINES_QUANTITY, LINES_SYMBOL));
        printInfo(PowerTransferDistributionFactors.generatePTDFs(LINES_QUANTITY, NODES_QUANTITY));
        printInfo(NetworkParams.writeNodesParameters(NODES_QUANTITY));
    }

    private static void printInfo(String str) {
        System.out.println(str);
        System.out.println("\n");
    }
}
