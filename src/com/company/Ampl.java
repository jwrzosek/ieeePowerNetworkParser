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
import com.company.results.SummaryResult;

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
import java.util.stream.DoubleStream;

public class Ampl {

    private static Map<String, SummaryResult> summaryResults = new HashMap<>();
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

        long startTime = System.nanoTime();

        try (AMPL ampl = new AMPL(env)) {
            ampl.setOption("solver", "cplex");
            ampl.setOption("cplex_options", "mipgap 0");

            calculateAll("C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_temp\\", ampl);

        }

        calculateLmpPrices();
        addLoads();
        saveResults();
        calculateSummaryResults();
        saveSummaryResults();

        long elapsedTime = System.nanoTime() - startTime;
        final var timeInSeconds = elapsedTime / 1000000000;
        final var minutes = timeInSeconds / 60;
        final var seconds = timeInSeconds - (minutes * 60);
        System.out.println(LocalDateTime.now() + " --- Total execution time in sec: "
                + timeInSeconds + " (in minutes: " + minutes + "min " + seconds + "sec)");
    }

    private static void calculateSummaryResults() {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(key -> {
                    SummaryResult summary = new SummaryResult();
                    summary.withDataSetName(key);
                    final var results = Ampl.results.get(key);
                    final var unconstrainedResult = results.stream()
                            .filter(r -> r.getResultName().equals("unconstrained.dat"))
                            .findAny()
                            .orElseThrow(NoSuchElementException::new);
                    summary.withTotalBalancingCostUnconstrained(unconstrainedResult.getTotalBalancingCost());
                    summary.withUniformPriceUnconstrained(unconstrainedResult.getUniformPrice());

                    summary.withMinLmpPrice(findMinLmpPrice(results));
                    summary.withAverageLmpPrice(findArithmeticAverageLmpPrice(results));
                    summary.withMedianLmpPrice(findLMPMedianPrice(results));
                    summary.withMaxLmpPrice(findMaxLmpPrice(results));

                    final var totalLoad = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(Result::getLoad)
                            .sum();
                    summary.withTotalLoad(totalLoad);


                    final var balancedResult = results.stream()
                            .filter(r -> r.getResultName().equals("balanced.dat"))
                            .findAny()
                            .orElseThrow(NoSuchElementException::new);
                    summary.withTotalBalancingCostConstrained(balancedResult.getTotalBalancingCost());
                    summary.withUniformPriceConstrained(balancedResult.getUniformPrice());

                    final var totalSystemIncomeLMP = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(Ampl::calculatePartialSystemIncomeLMP)
                            .sum();
                    summary.withTotalSystemIncomeLMP(totalSystemIncomeLMP);

                    final var totalSystemIncomeUP = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(r -> calculatePartialSystemIncomeUP(r, summary))
                            .sum();
                    summary.withTotalSystemIncomeUP(totalSystemIncomeUP);

                    final var totalLMPProfitOverUP = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(Ampl::calculatePartialLMPProfitOverUP)
                            .sum();
                    summary.withTotalLMPProfitOverUP(totalLMPProfitOverUP);

                    summaryResults.put(key, summary);
                });
    }

    private static boolean isNodeResult(final Result result) {
        return !result.getResultName().equals("unconstrained.dat")
                && !result.getResultName().equals("balanced.dat");
    }


    private static Double findMinLmpPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .min()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Double findArithmeticAverageLmpPrice(final List<Result> results) {
        final var lmpNodesQuantity = results.stream()
                .filter(Ampl::isNodeResult)
                .count();
        final var lmpSum = results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .sum();
        return lmpSum / lmpNodesQuantity;
    }


    private static Double findMaxLmpPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Double findLMPMedianPrice(final List<Result> results) {
        DoubleStream sortedAges = results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .sorted();
        int nodesQuantity = (int) results.stream().filter(Ampl::isNodeResult).count();
        double median = nodesQuantity % 2 == 0 ?
                sortedAges.skip((nodesQuantity / 2) - 1).limit(2).average().getAsDouble() :
                sortedAges.skip(nodesQuantity / 2).findFirst().getAsDouble();
        return median;
    }

    /**
     * Calculates nodal system income based on uniform price from unconstrained system.
     */
    private static Double calculatePartialSystemIncomeUP(Result result, SummaryResult summaryResult) {
        return summaryResult.getUniformPriceUnconstrained() * result.getLoad();
    }

    /**
     * Calculates nodal system income based on lmp price.
     */
    private static Double calculatePartialSystemIncomeLMP(Result result) {
        return result.getLmpPrice() * result.getLoad();
    }

    private static Double calculatePartialLMPProfitOverUP(Result result) {
        return (result.getUniformPrice() - result.getLmpPrice()) * result.getLoad();
    }

    private static void addLoads() {
        loads.keySet()
                .forEach(key -> {
                    final var totalLoad = calculateTotalLoad(key);
                    addLoad(key, totalLoad);
                });
    }

    private static void addLoad(final String key, final Double totalLoad) {
        final var dataSetLoad = loads.get(key);
        dataSetLoad.keySet().forEach(k -> {
            final var nodeNumber = Integer.parseInt(k);
            final var load = dataSetLoad.get(k);
            final var result = results.get(key).stream()
                    .filter(r -> r.getNodeNumber() == nodeNumber)
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);
            result.setLoad(load);
            result.setTotalLoad(totalLoad);
        });
    }

    private static Double calculateTotalLoad(final String key) {
        return loads.get(key).values().stream()
                .mapToDouble(v -> v)
                .sum();
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

    private static void saveSummaryResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("Summary Result file:")
                .append("# Time: ").append(LocalDateTime.now().toString()).append("\n\n");

        sb.append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "dataSet"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "UP_u"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "UP_c"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_min"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_arith_avg"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_median"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_max"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "total_load"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "bal_cost_u"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "bal_cost_c"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "income_UP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "income_LMP"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "lmp_prof_over_up"))
                .append("\n");

        summaryResults.values().stream()
                .sorted(Comparator.comparing(SummaryResult::getTotalLoad))
                .forEach(summaryResult -> sb
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getDataSetName()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceUnconstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceConstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalLoad())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostUnconstrained())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostConstrained())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeUP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLMP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLMPProfitOverUP())))
                        .append("\n"));
//        summaryResults.keySet().forEach(key -> {
//            final var summaryResult = summaryResults.get(key);
//            sb.append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getDataSetName()))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceUnconstrained()))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceConstrained()))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLmpPrice())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLmpPrice())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLmpPrice())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLmpPrice())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalLoad())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostUnconstrained())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostConstrained())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeUP())))
//                    .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLMP())))
//                    .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLMPProfitOverUP())))
//                    .append("\n");
//        });
        final var stringFormatResultsData = sb.append("# End of summary results file ---").toString();
        saveResultsToFile(stringFormatResultsData, "summaryResults");
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
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.5f", result.getLoad())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.5f", result.getTotalLoad())))
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

    private static void calculateAll(final String baseDirectory, AMPL ampl) {
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
        final var uniformPrice = findUniformPrice(ampl);
        final var totalBalancingCost = findTotalCost(ampl);

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

    private static Double findTotalCost(final AMPL ampl) {
        Variable totalCost = ampl.getVariable("total_cost");
        DataFrame df = totalCost.getValues();
        final var totalCostColumn = df.getColumn("total_cost.val");
        return Arrays.stream(totalCostColumn)
                .map(Object::toString)
                .mapToDouble(Double::parseDouble)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Integer findUniformPrice(final AMPL ampl) {
        Variable prices = ampl.getVariable("prices");
        DataFrame df = prices.getValues();
        final var pricesColumn = df.getColumn("prices.val");
        return Arrays.stream(pricesColumn)
                .map(Object::toString)
                .mapToDouble(Double::parseDouble)
                .mapToInt(d -> (int) Math.round(d))
                .max()
                .orElseThrow(NoSuchElementException::new);
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
