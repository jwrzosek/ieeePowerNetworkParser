package com.company;

import com.ampl.AMPL;
import com.ampl.DataFrame;
import com.ampl.Environment;
import com.ampl.Parameter;
import com.ampl.Variable;
import com.company.parser.exceptions.DataNotFoundException;
import com.company.parser.util.AmplUtils;
import com.company.parser.util.PowerNetworkUtils;
import com.company.results.PowerGeneration;
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

    private static Map<String, List<PowerGeneration>> generations = new HashMap<>();
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

            // for testing
//            calculateAll("C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_temp", ampl);

            // for kse:
//            calculateAll(AmplUtils.DIRECTORY_PATH_KSE, ampl);

            // for test power networks:
            calculateAll(AmplUtils.DIRECTORY_PATH, ampl);

        }
        final var amplCalculationNanoTime = measureTimeSince(startTime, "Ampl calculation time");

        // calculate necessary results values
        calculateLmpPrices();
        calculateLpPlusPrices();
        addLoads();

        // save results
        saveResults();

        // calculate summary results
        calculateSummaryResults();
        // save summary results
        saveSummaryResults();

        measureTimeSince(amplCalculationNanoTime, "Result calculation time");

        measureTimeSince(startTime, "Total execution time");
    }

    private static long measureTimeSince(final long sinceTime, final String description) {
        long nanoTime = System.nanoTime();
        long totalExecutionTime = nanoTime - sinceTime;
        final var timeInSeconds = totalExecutionTime / 1000000000;
        final var minutes = timeInSeconds / 60;
        final var seconds = timeInSeconds - (minutes * 60);
        final long miliseconds = totalExecutionTime / 1000000;
        ;

        System.out.println(LocalDateTime.now() + " --- " + description + " in sec: "
                + timeInSeconds + " (in minutes: " + minutes + "min " + seconds + "sec " + ((minutes == 0) ? " in milisec: " : ""));
        return nanoTime;
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

                    summary.withMinLpPlusPrice(findMinLpPlusPrice(results));
                    summary.withAverageLpPlusPrice(findArithmeticAverageLpPlusPrice(results));
                    summary.withMedianLpPlusPrice(findLpPlusMedianPrice(results));
                    summary.withMaxLpPlusPrice(findMaxLpPlusPrice(results));

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


                    final var totalSystemIncomeLpPlus = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(Ampl::calculatePartialSystemIncomeLpPlus)
                            .sum();
                    summary.withTotalSystemIncomeLpPlus(totalSystemIncomeLpPlus);

                    final var totalSystemIncomeUP = results.stream()
                            .filter(Ampl::isNodeResult)
                            .mapToDouble(r -> calculatePartialSystemIncomeUP(r, summary))
                            .sum();
                    summary.withTotalSystemIncomeUP(totalSystemIncomeUP);


                    summary.withTotalLMPProfitOverUP(findTotalLMPProfitOverUP(results));

                    summary.withTotalLpPlusProfitOverUP(findTotalLpPlusProfitOverUP(results));

                    summary.withAverageBuyerLmpPrice(findAverageBuyerLmpPrice((summary)));
                    summary.withAverageBuyerLpPlusPrice(findAverageBuyerLpPlusPrice(summary));

                    summary.withConstraintsCost(findCostOfConstraints(summary));

                    summary.withSystemSurplusUP(calculateSystemSurplusUP(summary));
                    summary.withSystemSurplusLMP(calculateSystemSurplusLMP(summary));
                    summary.withSystemSurplusLpPlus(calculateSystemSurplusLpPlus(summary));


                    summary.withSuppliersProfitUP(summary.getTotalSystemIncomeUP() - summary.getTotalBalancingCostUnconstrained());
                    summary.withSuppliersProfitLMP(0.0);
                    summary.withSuppliersProfitLpPlus(calculateSuppliersProfitLpPlus(results));

                    summary.withSystemProfitUP(0.0);
                    summary.withSystemProfitLMP(summary.getTotalSystemIncomeLMP() - summary.getTotalBalancingCostConstrained());
                    summary.withSystemProfitLpPlus(calculateSystemProfitLpPlus(summary));

                    summary.withNumberOfCompetitiveNodes(calculateNumberOfCompetitiveNodes(results));


                    summaryResults.put(key, summary);
                });
    }

    private static Integer calculateNumberOfCompetitiveNodes(final List<Result> results) {
        return (int) results.stream().filter(Result::isCompetitive).count();
    }

    private static Double calculateSystemProfitLpPlus(final SummaryResult summary) {
        return summary.getTotalSystemIncomeLpPlus() - summary.getTotalBalancingCostConstrained() - summary.getSuppliersProfitLpPlus();
    }

    private static Double calculateSuppliersProfitLpPlus(final List<Result> results) {
        return results.stream()
                .filter(Result::isCompetitive)
                .mapToDouble(r -> r.getUniformPrice() * r.getLoad())
                .sum();
    }

    private static Double calculateSystemSurplusUP(final SummaryResult summary) {
        //todo: balancing cost unconstrained or constrained
        final var totalIncome = summary.getTotalSystemIncomeUP();
        //final var balancingCost = summary.getTotalBalancingCostUnconstrained();
        final var balancingCost = summary.getTotalBalancingCostConstrained();
        return totalIncome - balancingCost;
    }

    private static Double calculateSystemSurplusLMP(final SummaryResult summary) {
        final var totalIncome = summary.getTotalSystemIncomeLMP();
        final var balancingCost = summary.getTotalBalancingCostConstrained();
        return totalIncome - balancingCost;
    }

    private static Double calculateSystemSurplusLpPlus(final SummaryResult summary) {
        final var totalIncome = summary.getTotalSystemIncomeLpPlus();
        final var balancingCost = summary.getTotalBalancingCostConstrained();
        return totalIncome - balancingCost;
    }

    private static boolean isNodeResult(final Result result) {
        return !result.getResultName().equals("unconstrained.dat")
                && !result.getResultName().equals("balanced.dat");
    }

    private static Double findTotalLMPProfitOverUP(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Ampl::calculatePartialLMPProfitOverUP)
                .sum();
    }

    private static Double findTotalLpPlusProfitOverUP(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Ampl::calculatePartialLpPlusProfitOverUP)
                .sum();
    }

    private static Double calculatePartialLpPlusProfitOverUP(Result result) {
        return (result.getUniformPrice() - result.getLpPlusPrice()) * result.getLoad();
    }


    private static Double findAverageBuyerLmpPrice(final SummaryResult summary) {
        final var totalSystemIncomeLMP = summary.getTotalSystemIncomeLMP();
        final var totalLoad = summary.getTotalLoad();
        if (totalLoad == null || totalSystemIncomeLMP == null) {
            throw new DataNotFoundException("total load or total system income lmp values of summary result missing");
        }
        return totalSystemIncomeLMP / totalLoad;
    }

    private static Double findAverageBuyerLpPlusPrice(final SummaryResult summary) {
        final var totalSystemIncomeLpPlus = summary.getTotalSystemIncomeLpPlus();
        final var totalLoad = summary.getTotalLoad();
        if (totalLoad == null || totalSystemIncomeLpPlus == null) {
            throw new DataNotFoundException("total load or total system income lmp values of summary result missing");
        }
        return totalSystemIncomeLpPlus / totalLoad;
    }

    private static Double findCostOfConstraints(final SummaryResult summary) {
        final var balancingCostConstrained = summary.getTotalBalancingCostConstrained();
        final var balancingCostUnconstrained = summary.getTotalBalancingCostUnconstrained();
        if (balancingCostConstrained == null || balancingCostUnconstrained == null) {
            throw new DataNotFoundException("balancing costs result missing");
        }
        return balancingCostConstrained - balancingCostUnconstrained;
    }

    private static Double findMinLmpPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .min()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Double findMinLpPlusPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLpPlusPrice)
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

    private static Double findArithmeticAverageLpPlusPrice(final List<Result> results) {
        final var lmpNodesQuantity = results.stream()
                .filter(Ampl::isNodeResult)
                .count();
        final var lpPlusSum = results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLpPlusPrice)
                .sum();
        return lpPlusSum / lmpNodesQuantity;
    }


    private static Double findMaxLmpPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Double findMaxLpPlusPrice(final List<Result> results) {
        return results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLpPlusPrice)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Double findLMPMedianPrice(final List<Result> results) {
        DoubleStream sortedLmpPrices = results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLmpPrice)
                .sorted();
        int nodesQuantity = (int) results.stream().filter(Ampl::isNodeResult).count();
        double median = nodesQuantity % 2 == 0 ?
                sortedLmpPrices.skip((nodesQuantity / 2) - 1).limit(2).average().getAsDouble() :
                sortedLmpPrices.skip(nodesQuantity / 2).findFirst().getAsDouble();
        return median;
    }

    private static Double findLpPlusMedianPrice(final List<Result> results) {
        DoubleStream sortedLpPlusPrices = results.stream()
                .filter(Ampl::isNodeResult)
                .mapToDouble(Result::getLpPlusPrice)
                .sorted();
        int nodesQuantity = (int) results.stream().filter(Ampl::isNodeResult).count();
        double median = nodesQuantity % 2 == 0 ?
                sortedLpPlusPrices.skip((nodesQuantity / 2) - 1).limit(2).average().getAsDouble() :
                sortedLpPlusPrices.skip(nodesQuantity / 2).findFirst().getAsDouble();
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

    /**
     * Calculates nodal system income based on LP+ price.
     */
    private static Double calculatePartialSystemIncomeLpPlus(Result result) {
        return result.getLpPlusPrice() * result.getLoad();
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

    private static void calculateLpPlusPrices() {
        results.keySet()
                .forEach(Ampl::calculateLpPlusPrice);
    }

    private static void calculateLpPlusPrice(final String key) {
        results.get(key).forEach(result -> {
                    final var nodeNumber = result.getNodeNumber();
                    if (!nodeNumber.equals(0)) {
                        final var balanced = generations.get("balanced.dat")
                                .stream()
                                .filter(g -> g.getNodeNumber().equals(nodeNumber))
                                .findAny()
                                .orElseThrow(NoSuchElementException::new);
                        final var unconstrained = generations.get("unconstrained.dat")
                                .stream()
                                .filter(g -> g.getNodeNumber().equals(nodeNumber))
                                .findAny()
                                .orElseThrow(NoSuchElementException::new);
                        //final var diff = balanced.getGenerationValue() - unconstrained.getGenerationValue();
                        if (balanced.getGenerationValue() > unconstrained.getGenerationValue()) {
                            //theoretically should be UP
                            result.withLpPlusPrice((double) result.getUniformPrice());
                            result.setCompetitive(true);
                        } else {
                            //theoretically should be LP+
                            result.withLpPlusPrice(result.getLmpPrice());
                            result.setCompetitive(false);
                        }
                    }
                }
        );
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

        sb.append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "dataSetName"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "total_load"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "UP_u"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "UP_c"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_min"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_arith_avg"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "LMP_avg_buyer_cost"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_median"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP_max"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LP+_min"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LP+_arith_avg"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "LP+_avg_buyer_cost"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LP+_median"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LP+_max"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "bal_cost_c"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "bal_cost_u"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "constraints_cost"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "income_UP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "income_LMP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "income_LP+"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "surplus_UP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "surplus_LMP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "surplus_LP+"))

                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sys_profit_UP"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sys_profit_LMP"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sys_profit_LP+"))

                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sup_profit_UP"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sup_profit_LMP"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "sup_profit_LP+"))

                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "lmp_prof_over_up"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "lp+_prof_over_up"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "num_of_comp_nodes"))
                .append("\n");

        sb.append("Sorted by default (by data set name):").append("\n");
        summaryResults.values().stream()
                .sorted(Comparator.comparing(summaryResult -> Integer.parseInt(summaryResult.getDataSetName().split("_")[0])))
                .forEach(summaryResult -> sb
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, summaryResult.getDataSetName()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalLoad())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceUnconstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceConstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getAverageBuyerLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getAverageBuyerLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostConstrained())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostUnconstrained())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getConstraintsCost())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeUP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLMP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLpPlus())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusUP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusLMP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitLMP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitLMP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLMPProfitOverUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLpPlusProfitOverUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, summaryResult.getNumberOfCompetitiveNodes()))
                        .append("\n"));
        sb.append("End of summary sorted by total load").append("\n\n\n");

        sb.append("Sorted by total load:").append("\n");
        summaryResults.values().stream()
                .sorted(Comparator.comparing(SummaryResult::getTotalLoad))
                .forEach(summaryResult -> sb
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, summaryResult.getDataSetName()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalLoad())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceUnconstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, summaryResult.getUniformPriceConstrained()))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getAverageBuyerLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLmpPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMinLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getAverageLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getAverageBuyerLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMedianLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getMaxLpPlusPrice())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostConstrained())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalBalancingCostUnconstrained())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getConstraintsCost())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeUP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLMP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getTotalSystemIncomeLpPlus())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusUP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusLMP())))
                        .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", summaryResult.getSystemSurplusLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitLMP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSystemProfitLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitLMP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getSuppliersProfitLpPlus())))

                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLMPProfitOverUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, String.format("%.2f", summaryResult.getTotalLpPlusProfitOverUP())))
                        .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, summaryResult.getNumberOfCompetitiveNodes()))
                        .append("\n"));
        sb.append("End of summary sorted by total load").append("\n\n");

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
        sb
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "node_nr"))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, "result_name"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "objective"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "UP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LMP"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "LP+"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "is_competitive"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "total_bal_cost"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "node_load"))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, "total_load"))
                .append("\n");
        sortedResults.forEach(result -> sb
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, result.getNodeNumber()))
                .append(String.format(ResultUtil.RESULT_SET_NAME_FORMAT, result.getResultName()))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getObjective())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, result.getUniformPrice()))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getLmpPrice())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getLpPlusPrice())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, (result.isCompetitive() ? 1 : 0)))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.2f", result.getTotalBalancingCost())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.5f", result.getLoad())))
                .append(String.format(ResultUtil.RESULT_VARIABLE_FORMAT, String.format("%.5f", result.getTotalLoad())))
                .append("\n"));
        final var stringFormatResultsData = sb.append("# Result file end ---").toString();
        saveResultsToFile(stringFormatResultsData, dataSetName);
    }

    private static void saveResultsToFile(final String stringFormatData, final String fileName) {
        final var fileNameWithExtension = fileName + ".txt";
        // for kse:
//        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, fileNameWithExtension);
        // for test power networks:
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, fileNameWithExtension);
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
                            // for kse:
//                             final var folder = "kse_" + dataSetName;
                            // for test power networks:
                            final var folder = dataSetName;
                            final var directory = baseDirectory + "\\" + folder;
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
                            completeMissingUniformPrices(dataSetName);
                        }
                );


    }

    private static void completeMissingUniformPrices(final String dataSetName) {
        final var unconstrainedResult = results.get(dataSetName).stream()
                .filter(r -> r.getResultName().equals("unconstrained.dat"))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        final var uniformPrice = unconstrainedResult.getUniformPrice();
        results.get(dataSetName).stream()
                .filter(r -> !r.getResultName().equals("unconstrained.dat") && !r.getResultName().equals("balanced.dat"))
                .forEach(result -> result.withUniformPrice(uniformPrice));
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
        final var modelFileDir = directory + "\\" + PowerNetworkUtils.AMPL_MODEL_NAME;
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
        final var totalBalancingCost = findTotalCost(ampl);

        final var fileName = Path.of(dataFileDir).getFileName().toString();

        if (fileName.equals("balanced.dat") && loads.get(dataSetName).keySet().isEmpty()) {
            findLoad(ampl, dataSetName);
        }

        if (fileName.equals("balanced.dat") || fileName.equals("unconstrained.dat")) {
            result.withNodeNumber(0);
            findGeneration(ampl, fileName);
            final var uniformPrice = findUniformPrice(ampl);
            result.withUniformPrice(uniformPrice);
        } else {
            final var nodeNumberLMP = fileName.split("\\.")[0];
            result.withNodeNumber(Integer.parseInt(nodeNumberLMP));
        }

        return result
                .withResultName(fileName)
                .withObjective(objective)
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

    private static void findGeneration(final AMPL ampl, final String fileName) {
        List<PowerGeneration> powerGenerations = new ArrayList<>();
        Variable Pa_gen = ampl.getVariable("Pa_gen");
        DataFrame df = Pa_gen.getValues();
        for (int i = 0; i < df.getNumRows(); i++) {
            final var rowByIndex = df.getRowByIndex(i);
            final var nodeName = (String) rowByIndex[0];
            final var periodName = (String) rowByIndex[1];
            final var value = (Double) rowByIndex[2];
            powerGenerations.add(new PowerGeneration()
                    .withNodeName(nodeName)
                    .withNodeNumber(Integer.parseInt(nodeName.substring(1)))
                    .withPeriodName(periodName)
                    .withGenerationValue(value)
            );
        }
        generations.put(fileName, powerGenerations);
    }
}
