package com.company;

import com.ampl.AMPL;
import com.ampl.DataFrame;
import com.ampl.Environment;
import com.ampl.Variable;
import com.company.parser.util.PowerNetworkUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Ampl {


    public static void main(String[] args) throws IOException {
        Environment env = new Environment("C:\\AMPLCOMMUNITY\\ampl.mswin64");
        try (AMPL ampl = new AMPL(env);) {
            ampl.setOption("solver", "cplex");
            ampl.setOption("cplex_options", "mipgap 0");

            calculateAll("C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_temp\\", ampl);
        }
    }

    public static void calculateAll(final String baseDirectory, AMPL ampl) {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(key -> {
                            final var folder = "kse_" + key;
                            final var directory = baseDirectory + folder;
                            File file = new File(directory);
                            final var files = file.listFiles();
                            final var dataFiles = getDataFilesNames(files);
                            dataFiles.forEach(fileName -> {
                                try {
                                    calculateSingeScenario(directory, fileName, ampl);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                );


    }

    private static List<String> getDataFilesNames(final File[] files) {
        return Arrays.stream(files)
                .map(File::getName)
                .filter(fileName -> fileName.contains(".dat"))
                .filter(fileName -> !fileName.contains("common"))
                .collect(Collectors.toList());
    }

    private static void calculateSingeScenario(final String directory, final String fileName, AMPL ampl) throws IOException {
        final var modelFileDir = directory + "\\" + "model_min_balancing_cost_for_kse.mod";
        final var dataFileDir = directory + "\\" + fileName;
        final var commonDataFileDir = directory + "\\" + "common.dat";

        ampl.read(modelFileDir);
        if (fileName.contains("unconstrained")) {
            ampl.readData(dataFileDir);
        } else {
            ampl.readData(commonDataFileDir);
            ampl.readData(dataFileDir);
        }
        ampl.solve();
        performDataCalculations(ampl, dataFileDir);
        ampl.reset();
    }

    private static void performDataCalculations(final AMPL ampl, final String dataFileDir) {
        final var uniformPrice = findUniformPrice(ampl, dataFileDir);
        final var totalCost = findTotalCost(ampl, dataFileDir);
    }

    private static Double findTotalCost(final AMPL ampl, final String dataFileDir) {
        Variable totalCost = ampl.getVariable("total_cost");
        DataFrame df = totalCost.getValues();
        final var totalCostColumn = df.getColumn("total_cost.val");
        final var value = Arrays.stream(totalCostColumn)
                .map(Object::toString)
                .mapToDouble(Double::parseDouble)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        System.out.println("\nTotalCost for " + Path.of(dataFileDir).getFileName() + ": " + value);
        return value;
    }

    private static Integer findUniformPrice(final AMPL ampl, final String dataFileDir) {
        Variable prices = ampl.getVariable("prices");
        DataFrame df = prices.getValues();
        final var pricesColumn = df.getColumn("prices.val");
        final var value = Arrays.stream(pricesColumn)
                .map(Object::toString)
                .mapToDouble(Double::parseDouble)
                .mapToInt(d -> (int) Math.round(d))
                .max()
                .orElseThrow(NoSuchElementException::new);
        System.out.println("\nUniform price for " + Path.of(dataFileDir).getFileName() + ": " + value);
        return value;
    }


}
