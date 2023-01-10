package com.company;

import com.ampl.AMPL;
import com.ampl.DataFrame;
import com.ampl.Environment;
import com.ampl.Parameter;
import com.ampl.Variable;
import com.company.parser.util.AmplUtils;
import com.company.parser.util.PowerNetworkUtils;
import com.company.results.Result;
import com.company.results.ResultUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Ampl {

    private static Map<String, Map<String, Double>> loads = new HashMap<>();
    static {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(key -> loads.put(key, new HashMap<>())
                );
    }
    private static Map<String, List<Result>> results = new HashMap<>();
    static {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(key -> results.put(key, new ArrayList<>())
        );
    }

    public static void main(String[] args) {
        Environment env = new Environment("C:\\AMPLCOMMUNITY\\ampl.mswin64");
        try (AMPL ampl = new AMPL(env);) {
            ampl.setOption("solver", "cplex");
            ampl.setOption("cplex_options", "mipgap 0");

            calculateAll("C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_temp\\", ampl);
        }

        calculateLmpPrices();
        addLoads();
        saveResults();
    }

    private static void addLoads() {
        loads.keySet()
                .forEach(Ampl::addLoad);
    }

    private static void addLoad(final String key) {
        final var dataSetLoad = loads.get(key);
        dataSetLoad.keySet().forEach(k -> {
            final var nodeNumber = Integer.parseInt(k);
            final var load = dataSetLoad.get(k);
            final var result = results.get(key).stream()
                    .filter(r -> r.getNodeNumber() == nodeNumber)
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);
            result.setLoad(load);
        });
    }


    private static void calculateLmpPrices() {
        results.keySet()
                .forEach(Ampl::calculateLmpPrice);
    }

    private static void calculateLmpPrice(final String key) {
        final var balancedResult = results.get(key).stream()
                .filter(result -> result.getResultName().contains("balanced"))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        final var balancedTotalBalancingCost = balancedResult.getTotalBalancingCost();

        results.get(key)
                .forEach(result -> {
                    final var resultName = result.getResultName();
                    if (!resultName.equals("balanced.dat") && !resultName.equals("unconstrained.dat")) {
                        var lmpPrice = result.getTotalBalancingCost() - balancedTotalBalancingCost;
                        result.withLmpPrice(lmpPrice);
                    }
                });
    }

    private static void saveResults() {
        results.keySet()
                .forEach(Ampl::saveResultsForSingleDataSet);
    }

    private static void saveResultsForSingleDataSet(String dataSetName) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Result file for ").append(dataSetName).append(":\n")
                .append("# Time: ").append(LocalDateTime.now().toString()).append("\n\n");

        final var sortedResults = results.get(dataSetName)
                .stream()
                .sorted(Comparator.comparing(Result::getNodeNumber))
                .collect(Collectors.toList());
        sortedResults.forEach(result -> sb
                .append(String.format(ResultUtil.RESULT_NUMBER_FORMAT, result.getNodeNumber()))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, result.getResultName()))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getObjective())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, result.getUniformPrice()))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getLmpPrice())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getTotalBalancingCost())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.6f", result.getLoad())))
                .append("\n"));
        final var stringFormatResultsData = sb.append("# Result file end ---").toString();
        saveResultsToFile(stringFormatResultsData, dataSetName);
    }

    private static void saveResultsToFile(final String stringFormatData, final String fileName) {
        final var fileNameWithExtension = fileName + ".txt";
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE_TEMP, fileNameWithExtension);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, stringFormatData, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calculateAll(final String baseDirectory, AMPL ampl) {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(dataSetName -> {
                            final var folder = "kse_" + dataSetName;
                            final var directory = baseDirectory + folder;
                            File file = new File(directory);
                            final var files = file.listFiles();
                            final var dataFiles = getDataFilesNames(files);
                            dataFiles.forEach(fileName -> {
                                try {
                                    calculateSingeScenario(directory, fileName, dataSetName, ampl);
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

    private static void calculateSingeScenario(final String directory,
                                               final String fileName,
                                               final String dataSetName,
                                               AMPL ampl) throws IOException {
        final var modelFileDir = directory + "\\" + "model_min_balancing_cost_for_kse.mod";
        final var dataFileDir = directory + "\\" + fileName;
        final var commonDataFileDir = directory + "\\" + "common.dat";
        final var commonUnconstrainedDataFileDir = directory + "\\" + "commonUnconstrained.dat";

        // read model file
        ampl.read(modelFileDir);
        // read data files *for unconstrained read different common file
        if (fileName.equals("unconstrained.dat")) {
            ampl.readData(dataFileDir);
            ampl.readData(commonUnconstrainedDataFileDir);
        } else {
            ampl.readData(commonDataFileDir);
            ampl.readData(dataFileDir);
        }
        // solve problem
        ampl.solve();

        // add result object to result list
        final var result = performDataCalculations(ampl, dataFileDir, dataSetName);
        results.get(dataSetName).add(result);

        // reset ampl
        ampl.reset();
    }

    private static Result performDataCalculations(final AMPL ampl, final String dataFileDir, final String dataSetName) {
        Result result = new Result();

        final var objective = getObjective(ampl);
        final var uniformPrice = findUniformPrice(ampl, dataFileDir);
        final var totalBalancingCost = findTotalCost(ampl, dataFileDir);

        final var fileName = Path.of(dataFileDir).getFileName().toString();

        if (fileName.equals("balanced.dat") && loads.get(dataSetName).keySet().isEmpty()) {
            findLoad(ampl, dataSetName);
        }


        if (fileName.equals("balanced.dat") || fileName.equals("unconstrained.dat")) {
            result.withNodeNumber(0);
        } else {
            final var nodeNumberLMP = fileName.split("\\.")[0];
            result.withNodeNumber(Integer.parseInt(nodeNumberLMP));
        }

        return result
                .withResultName(fileName)
                .withObjective(objective)
                .withUniformPrice(uniformPrice)
                .withTotalBalancingCost(totalBalancingCost);
    }

    private static Double getObjective(final AMPL ampl) {
        return ampl.getObjective("Q").value();
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

    private static void findLoad(final AMPL ampl, final String dataSetName) {
        Parameter load = ampl.getParameter("Pa_load");
        DataFrame df = load.getValues();
        for (int i = 0; i < df.getNumRows(); i++) {
            final var rowByIndex = df.getRowByIndex(i);
            final var node = (String) rowByIndex[0];
            final var loadValue = (Double) rowByIndex[2];
            loads.get(dataSetName).put(node.substring(1), loadValue);
        }
    }


}
