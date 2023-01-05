package com.company.parser;

import com.company.parser.model.HourlyLoad;
import com.company.parser.model.KseGenerator;
import com.company.parser.model.KseLine;
import com.company.parser.model.KseNode;
import com.company.parser.util.AmplUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MultiStageKSEModelAmplWriter {

    private final boolean unconstrained;

    public MultiStageKSEModelAmplWriter(final boolean unconstrained) {
        this.unconstrained = unconstrained;
    }

    public void writeAmplFullMultiStageModel(final String directoryName, final String fileName, final List<KseNode> buses,
                                             final List<KseLine> branches, final List<HourlyLoad> hourlyLoads,
                                             final List<KseGenerator> generators, int lmpNode, double peak) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_TEMP, directoryName, fileName);
        final var fullModel = createFullMultiStageModel(buses, branches, hourlyLoads, generators, lmpNode, peak);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullMultiStageModel(final List<KseNode> nodes, final List<KseLine> transmissionLines,
                                             final List<HourlyLoad> hourlyLoads, final List<KseGenerator> generators,
                                             int lmpNode, double peak) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(nodes.size(), transmissionLines.size(), peak);
        //final var busInfo = generateSet(AmplUtils.BUS_NAME, nodes.size(), AmplUtils.BUS_SYMBOL);
        //final var generatorsInfo = generateSet(AmplUtils.GENERATOR_NAME, generators.size(), AmplUtils.GENERATOR_SYMBOL);
        //final var hourlyLoadInfo = generateSet(AmplUtils.TIME_PERIOD_NAME, hourlyLoads.size(), AmplUtils.TIME_PERIOD_SYMBOL);
        //final var busParameters = generateBusParametersForMultiStageCase(nodes, generators);
        final var loadParameter = generateMultiStageLoadMWData(nodes, hourlyLoads, lmpNode, peak);
        final var pgenParameter = generateGeneratorsPgenParameter(nodes, hourlyLoads, generators);
        final var qBusParameter = generateQBusParameter(nodes, transmissionLines);
        final var voltageParameter = generateVParameter(nodes, transmissionLines);
        //final var yabParameter = generateAdmittanceParameter(nodes, transmissionLines);
        return sb
                .append(modelInfo)
                //.append(busInfo)
                //.append(generatorsInfo)
                //.append(hourlyLoadInfo)
                //.append(busParameters)
                .append(loadParameter)
                .append(pgenParameter)
                .append(qBusParameter)
                .append(voltageParameter)
                //.append(yabParameter)
                .toString();
    }

    private String generateBusParametersForMultiStageCase(final List<KseNode> nodes, final List<KseGenerator> generators) {
        final var params = List.of("Ka_load", "Ka_gen", "V");
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + ":"));
        sb.append(AmplUtils.PARAM_BEGIN);
        for (String param : params) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, param));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var node : nodes) {
            i++;
            final var offerPrice = getRandomInteger(50, 300);
            final var generation = generators.stream()
                    .filter(generator -> generator.getNodeNumber().equals(node.getNumber()))
                    .map(KseGenerator::getGenerationMax)
                    .filter(gen -> gen > 0)
                    .findFirst().orElse(0);
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i))
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT,  generation > 0 ? offerPrice - 20 : 999999))
                    .append(String.format(AmplUtils.PARAM_FORMAT,  220))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateAdmittanceParameter(final List<KseNode> nodes, final List<KseLine> transmissionLines) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_ADMITTANCE_SYMBOL + ":\n"));
        sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < nodes.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(nodes, transmissionLines);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL+tapBusNumber));
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? formatFPVariables(admittanceArray[i][j]) : 0.0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateQBusParameter(final List<KseNode> nodes, final List<KseLine> transmissionLines) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_LINE_CAPACITY_SYMBOL + ":\n"));
        sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < nodes.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(nodes, transmissionLines);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + tapBusNumber));
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format(
                        AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ?
                                getLineLimit(transmissionLines, i+1, j+1) : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateVParameter(final List<KseNode> nodes, final List<KseLine> transmissionLines) {
        StringBuilder sb = new StringBuilder(
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_VOLTAGE_SYMBOL + ":\n");
        sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < nodes.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var voltageArray = getVoltageArray(nodes, transmissionLines);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + tapBusNumber));
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format(
                        AmplUtils.PARAM_FORMAT, voltageArray[i][j] != null ?
                                voltageArray[i][j] : 0));
                                //getVoltage(transmissionLines, i+1, j+1) : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private Double[][] getAdmittanceArray(List<KseNode> buses, List<KseLine> branches) {
        final var numberOfBuses = buses.size();
        Double[][] admittanceArray = initializeAdmittanceArray(numberOfBuses);
        for (int i = 0; i < buses.size(); i++) {
            final var toNodeNumber = i + 1;
            final var links = branches.stream().filter(branch -> branch.getFromNodeNumber() == toNodeNumber).collect(Collectors.toList());
            for (int j = 0; j < buses.size(); j++) {
                final var fromNodeNumber = j + 1;
                final var first = links.stream().filter(branch -> branch.getToNodeNumber() == fromNodeNumber).findFirst();
                if (first.isPresent()) {
                    final var admittance = first.get().getAdmittance();
                    admittanceArray[i][j] = admittance;
                    admittanceArray[j][i] = admittance;
                }
            }
        }
        return admittanceArray;
    }

    private Integer[][] getVoltageArray(List<KseNode> buses, List<KseLine> lines) {
        final var numberOfBuses = buses.size();
        Integer[][] voltageArray = initializeVoltageArray(numberOfBuses);
        for (int i = 0; i < buses.size(); i++) {
            final var toNodeNumber = i + 1;
            final var links = lines.stream().filter(branch -> branch.getFromNodeNumber() == toNodeNumber).collect(Collectors.toList());
            for (int j = 0; j < buses.size(); j++) {
                final var fromNodeNumber = j + 1;
                final var first = links.stream().filter(branch -> branch.getToNodeNumber() == fromNodeNumber).findFirst();
                if (first.isPresent()) {
                    voltageArray[i][j] = first.get().getVoltageSource();
                    voltageArray[j][i] = first.get().getVoltageDestination();
                }
            }
        }
        return voltageArray;
    }

    private Double[][] initializeAdmittanceArray(int numberOfBuses) {
        Double[][] admittanceArray = new Double[numberOfBuses][numberOfBuses];
        for (int i = 0; i<numberOfBuses; i++) {
            for (int j = 0; j<numberOfBuses; j++) {
                admittanceArray[i][j] = null;
            }
        }
        return admittanceArray;
    }

    private Integer[][] initializeVoltageArray(int numberOfBuses) {
        Integer[][] voltageArray = new Integer[numberOfBuses][numberOfBuses];
        for (int i = 0; i<numberOfBuses; i++) {
            for (int j = 0; j<numberOfBuses; j++) {
                voltageArray[i][j] = null;
            }
        }
        return voltageArray;
    }

    private String generateGeneratorsPgenParameter(final List<KseNode> nodes, final List<HourlyLoad> hourlyLoads,
                                                   final List<KseGenerator> generators) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PGEN_MAX_SYMBOL + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN));

        for (int h = 0; h<hourlyLoads.size(); h++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, get3DimHourStartLabel(h+1))).append(AmplUtils.PARAM_BEGIN);

            for (int i = 1; i < generators.size() + 1; i++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.GENERATOR_SYMBOL + i));
            }
            sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

            for (int i = 0; i < nodes.size(); i++) {
                final var tapBusNumber = i + 1;
                sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + tapBusNumber));
                for (int j = 0; j < generators.size(); j++) {
                    final var generator = generators.get(j);
                    sb.append(String.format(
                            AmplUtils.PARAM_FORMAT,
                            generator.getNodeNumber() - 1 == i ? generator.getGenerationMax() : 0)
                    );
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateMultiStageLoadMWData(final List<KseNode> nodes, final List<HourlyLoad> hourlyLoads,
                                              final int lmpNode, final double peak) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PLOAD_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));

        for (int i = 1; i < hourlyLoads.size() + 1; i++) {
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.TIME_PERIOD_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            final var node = nodes.get(i);
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL+tapBusNumber));
            for (int j = 0; j < hourlyLoads.size(); j++) {
                final var load = node.getLoadSr() * peak;
                sb.append(String.format(
                        AmplUtils.PARAM_FORMAT,
                        formatFPVariables(i == lmpNode ? load + 1.0 : load))
                );
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private Integer getLineLimit(List<KseLine> lines, int node1, int node2) {
        if (unconstrained) {
            return 9999;
        }
        return  lines.stream()
                .filter(branch -> (branch.getFromNodeNumber() == node1 && branch.getToNodeNumber() == node2) || (branch.getFromNodeNumber() == node2 && branch.getToNodeNumber() == node1))
                .map(KseLine::getLineCapacity)
                .findAny()
                .orElse(99999);
    }

    private String generateSet(final String name, final int quantity, final String symbol) {
        StringBuilder sb = new StringBuilder("set " + name + " :=");
        for (int i = 0; i < quantity; i++) {
            sb.append(AmplUtils.DEFAULT_SET_SEPARATOR).append(symbol).append(i + 1);
        }
        sb.append(";\n\n");
        return sb.toString();
    }


    private String generateAmplModelInfo(final int numberOfBuses, final int numberOfBranches, final double peak) {
        DateTimeFormatter dataPattern = DateTimeFormatter.ofPattern(AmplUtils.INFO_SECTION_DATA_PATTERN);
        return "# " + numberOfBuses + "-nodes power network with " + numberOfBranches + " branches\n" +
                "# Author: Jakub Wrzosek\n" +
                "# Created: " + LocalDateTime.now().format(dataPattern) + "\n" +
                "# with peak: " + peak + "\n\n";
    }

    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private String formatFPVariables(final Double var) {
        return String.format("%.5f", var).replace(",", ".");
    }

    private String get3DimHourStartLabel(int hourNumber) {
        return "[*,*,H" + hourNumber+ "]:";
    }

    public void writeRunScriptForBatchToFile(final String directoryName, final int size) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directoryName, "batch.run");
        final var runScript = createRunScriptForBatch(size, directoryName);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createRunScriptForBatch(int size, final String directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("printf \"Results for KSE power network:\\n\" >> " + directory + "/results.out;\n\n");

        sb.append("reset;\n")
                .append("model " + directory + "/model_min_balancing_cost.mod\n")
                .append("data " + directory + "/common.dat\n")
                .append("data " + directory + "/unconstrained").append(".dat\n")
                .append("option solver cplex ;")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced_U:\", Q >> " + directory + "/results.out;\n\n");
        sb.append("reset;\n")
                .append("model " + directory + "/model_min_balancing_cost.mod\n")
                .append("data " + directory + "/common.dat\n")
                .append("data " + directory + "/balanced").append(".dat\n")
                .append("option solver cplex ;")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced:\", Q >> " + directory + "/results.out;\n\n");
        for (int i = 0; i<size; i++) {
            sb.append("reset;\n")
                    .append("model " + directory + "/model_min_balancing_cost.mod\n")
                    .append("data " + directory + "/common.dat\n")
                    .append("data " + directory + "/").append(i + 1).append(".dat\n")
                    .append("option solver cplex ;")
                    .append("solve;\n")
                    .append("printf \"%-12s %.2f\\n\", \"Q"+ (i+1) + ":\", Q >> " + directory + "/results.out;\n\n");
        }
        sb.append("printf \"----> RESULT SET END FOR KSE POWER NETWORK\\n\\n\" >> " + directory + "/results.out;\n");
        return sb.toString();
    }

    public void writeSingleRunScriptToFile(final String directoryName, final int size) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directoryName, "single.run");
        final var runScript = createSingleRunScript(size, directoryName);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createSingleRunScript(int size, final String directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("printf \"Results for KSE power network:\\n\" >> results_single.out;\n\n");

        sb.append("reset;\n")
                .append("model model_min_balancing_cost.mod\n")
                .append("data common.dat\n")
                .append("data unconstrained").append(".dat\n")
                .append("option solver cplex ;")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced_U:\", Q >> results_single.out;\n\n");
        sb.append("reset;\n")
                .append("model model_min_balancing_cost.mod\n")
                .append("data common.dat\n")
                .append("data balanced").append(".dat\n")
                .append("option solver cplex ;")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced:\", Q >> results_single.out;\n\n");
        for (int i = 0; i<size; i++) {
            sb.append("reset;\n")
                    .append("model model_min_balancing_cost.mod\n")
                    .append("data common.dat\n")
                    .append("data ").append(i + 1).append(".dat\n")
                    .append("option solver cplex ;")
                    .append("solve;\n")
                    .append("printf \"%-12s %.2f\\n\", \"Q"+ (i+1) + ":\", Q >> results_single.out;\n\n");
        }
        sb.append("printf \"----> RESULT SET END FOR KSE POWER NETWORK\\n\\n\" >> results_single.out;\n");
        return sb.toString();
    }
}
