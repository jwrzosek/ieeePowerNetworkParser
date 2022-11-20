package com.company.modelwriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class ModelWriter {

    private static final String DIRECTORY_PATH = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model";
    private static final String NODES_SYMBOL = "N";
    private static final String LINES_SYMBOL = "L";

    private ModelWriter() {}

    public static void writeModelToFile(String fileName, int nodesQuantity, int linesQuantity) {
        final var path = Paths.get(DIRECTORY_PATH, fileName);
        final var fullModel = createFullModel(nodesQuantity, linesQuantity);
        try {
            Files.writeString(path, fullModel, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createFullModel(int nodesQuantity, int linesQuantity) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateModelInfo(nodesQuantity);
        final var nodesSet = NetworkParams.writeSet("NODES", nodesQuantity, NODES_SYMBOL);
        final var linesSet = NetworkParams.writeSet("LINES", linesQuantity, LINES_SYMBOL);
        final var lines_limit = NetworkParams.writeParameter("line_limit", linesQuantity, LINES_SYMBOL);
        final var ptdfs = PowerTransferDistributionFactors.generatePTDFs(linesQuantity, nodesQuantity);
        final var nodesParameters = NetworkParams.writeNodesParameters(nodesQuantity);

        return sb
                .append(modelInfo).append("\n\n\n")
                .append(nodesSet)
                .append(linesSet).append("\n")
                .append(lines_limit).append("\n")
                .append(ptdfs).append("\n")
                .append(nodesParameters).append("\n")
                .toString();
    }

    private static String generateModelInfo(int nodesQuantity) {
        return "# " + nodesQuantity + "-nodes power network\n" +
                "# Author: Jakub Wrzosek\n" +
                "# Created: " + LocalDateTime.now();
    }
}
