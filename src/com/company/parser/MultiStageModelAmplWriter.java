package com.company.parser;

import com.company.parser.model.Generator;
import com.company.parser.model.HourlyLoad;
import com.company.parser.model.Line;
import com.company.parser.model.Node;
import com.company.parser.util.AmplUtils;
import com.company.parser.util.PowerNetworkUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MultiStageModelAmplWriter {

    private final boolean unconstrained;

    public MultiStageModelAmplWriter(final boolean unconstrained) {
        this.unconstrained = unconstrained;
    }

    public void writeAmplFullMultiStageModel(final String directoryName, final String fileName, final List<Node> nodes,
                                             final List<Line> nodeLines, final List<HourlyLoad> hourlyLoads, long numberOfGenerators,
                                             int lmpNode, double peak) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, directoryName, fileName);
        final var fullModel = createFullMultiStageModel(nodes, nodeLines, hourlyLoads, numberOfGenerators, lmpNode, peak);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullMultiStageModel(final List<Node> nodes, final List<Line> nodeLines,
                                             final List<HourlyLoad> hourlyLoads, long numberOfGenerators, int lmpNode, double peak) {
        StringBuilder sb = new StringBuilder();

        final var modelInfo = generateAmplModelInfo(nodes.size(), nodeLines.size(), peak);
        sb.append(modelInfo);

        final var busInfo = generateSet(AmplUtils.BUS_NAME, nodes.size(), AmplUtils.BUS_SYMBOL);
        final var generatorsInfo = generateSet(AmplUtils.GENERATOR_NAME, (int) numberOfGenerators, AmplUtils.GENERATOR_SYMBOL);
        final var hourlyLoadInfo = generateSet(AmplUtils.TIME_PERIOD_NAME, hourlyLoads.size(), AmplUtils.TIME_PERIOD_SYMBOL);
        final var qBusParameter = generateQBusParameter(nodes, nodeLines);
        final var loadParameter = generateMultiStageLoadMWData(nodes, hourlyLoads, lmpNode, peak);
        final var pGenMaxParameter = generateGeneratorsPgenMaxParameter(nodes, hourlyLoads);
        final var pGenMinParameter = generateGeneratorsPgenMinParameter(nodes, hourlyLoads);
        final var voltageParameter = generateVParameter(nodes);
        final var generatorVariableCost = generateVariableCost(findGenerators(nodes));
        final var yabParameter = generateAdmittanceParameter(nodes, nodeLines);

        //final var busParameters = generateBusParametersForMultiStageCase(nodes); // todo: ????
        return sb
                .append(busInfo)
                .append(generatorsInfo)
                .append(hourlyLoadInfo)
                .append(qBusParameter)
                //.append(generatorVariableCost)
                //.append(busParameters)
                .append(loadParameter)
                .append(pGenMaxParameter)
                .append(pGenMinParameter)
                .append(voltageParameter)
                .append(yabParameter)
                .toString();
    }

    void writeRunScriptForBatchToFile(final String directory, final int size) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, directory, "batch.run");
        final var runScript = createRunScript(size, directory);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createRunScript(int size, final String directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("printf \"Results:\\n\" >> " + directory + "/results.out;\n\n");

        sb.append("reset;\n")
                .append("model " + directory + "/model_min_balancing_cost.mod\n")
                .append("data " + directory + "/commonUnconstrained.dat\n")
                .append("data " + directory + "/unconstrained").append(".dat\n")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced_U:\", Q >> " + directory + "/results.out;\n\n");
        sb.append("reset;\n")
                .append("model " + directory + "/model_min_balancing_cost.mod\n")
                .append("data " + directory + "/common.dat\n")
                .append("data " + directory + "/balanced").append(".dat\n")
                .append("solve;\n")
                .append("printf \"%-12s %.2f\\n\", \"balanced:\", Q >> " + directory + "/results.out;\n\n");
        for (int i = 0; i < size; i++) {
            sb.append("reset;\n")
                    .append("model " + directory + "/model_min_balancing_cost.mod\n")
                    .append("data " + directory + "/common.dat\n")
                    .append("data " + directory + "/").append(i + 1).append(".dat\n")
                    .append("solve;\n")
                    .append("printf \"%-12s %.2f\\n\", \"Q" + (i + 1) + ":\", Q >> " + directory + "/results.out;\n\n");
        }
        sb.append("printf \"----> RESULT SET END\\n\\n\" >> " + directory + "/results.out;\n");
        return sb.toString();
    }

    void writeSingleRunScriptToFile(final String directoryName, final int size) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, directoryName, "single.run");
        final var runScript = createSingleRunScript(size, directoryName);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createSingleRunScript(int size, final String directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("printf \"Results for KSE power network:\\n\" >> results_single.out;\n\n");

        sb.append("reset;\n")
                .append("model model_min_balancing_cost.mod\n")
                .append("data commonUnconstrained.dat\n")
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
        for (int i = 0; i < size; i++) {
            sb.append("reset;\n")
                    .append("model model_min_balancing_cost.mod\n")
                    .append("data common.dat\n")
                    .append("data ").append(i + 1).append(".dat\n")
                    .append("option solver cplex ;")
                    .append("solve;\n")
                    .append("printf \"%-12s %.2f\\n\", \"Q" + (i + 1) + ":\", Q >> results_single.out;\n\n");
        }
        sb.append("printf \"----> RESULT SET END FOR KSE POWER NETWORK\\n\\n\" >> results_single.out;\n");
        return sb.toString();
    }

    private String generateAdmittanceParameter(List<Node> nodes, List<Line> nodeLines) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_ADMITTANCE_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < nodes.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(nodes, nodeLines);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? formatFPVariables(admittanceArray[i][j]) : 0.0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private Double[][] getAdmittanceArray(List<Node> nodes, List<Line> nodeLines) {
        final var numberOfBuses = nodes.size();
        Double[][] admittanceArray = initializeAdmittanceArray(numberOfBuses);
        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            final var links = nodeLines.stream().filter(nodeLine -> nodeLine.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < nodes.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(nodeLine -> nodeLine.getzBusNumber() == zBusNumber).findFirst();
                if (first.isPresent()) {
                    final var admittance = first.get().getAdmittance() / 100;
                    admittanceArray[i][j] = admittance;
                    admittanceArray[j][i] = admittance;
                }
            }
        }
        return admittanceArray;
    }

    private Double[][] initializeAdmittanceArray(int numberOfBuses) {
        Double[][] admittanceArray = new Double[numberOfBuses][numberOfBuses];
        for (int i = 0; i < numberOfBuses; i++) {
            for (int j = 0; j < numberOfBuses; j++) {
                admittanceArray[i][j] = null;
            }
        }
        return admittanceArray;
    }

    private String generateQBusParameter(List<Node> nodes, List<Line> nodeLines) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_LINE_CAPACITY_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < nodes.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(nodes, nodeLines);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ?
                        getLineLimit(nodeLines, i + 1, j + 1)
                        : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateBusParametersForMultiStageCase(List<Node> nodes) {
        final var params = List.of("Ka_load", "Ka_gen", "V");
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + ":"));
        for (String param : params) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, param));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var bus : nodes) {
            i++;
            final var offerPrice = getRandomInteger(50, 300);
            final var generation = bus.getGenerators().stream().map(Generator::getGenerationMW).filter(gen -> gen > 0.001).findFirst().orElse(0.0D);
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT, generation > 0.01 ? offerPrice - 20 : 999999))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateMultiStageLoadMWData(List<Node> nodes, List<HourlyLoad> hourlyLoads, int lmpNode, double peak) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PLOAD_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));

        for (int i = 1; i < hourlyLoads.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.TIME_PERIOD_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < nodes.size(); i++) {
            final var tapBusNumber = i + 1;
            final var bus = nodes.get(i);
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < hourlyLoads.size(); j++) {
                double load;
                if (PowerNetworkUtils.IS_SUMMER) {
                    load = bus.getLoadMW() * peak;// * PowerNetworkUtils.POWER_SUMMER_PERCENTAGE;
                } else { // isWINTER
                    load = bus.getLoadMW() * peak * PowerNetworkUtils.POWER_WINTER_PERCENTAGE;
                }
                sb.append(String.format(
                        AmplUtils.PARAM_FORMAT,
                        formatFPVariables(i == lmpNode ? load + 1.0 : load))
                );
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateGeneratorsPgenMaxParameter(List<Node> nodes, List<HourlyLoad> hourlyLoads) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PGEN_MAX_SYMBOL + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN));

        final var generators = findGenerators(nodes);
        for (int h = 0; h < hourlyLoads.size(); h++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, get3DimHourStartLabel(h + 1)));

            for (int i = 1; i < generators.size() + 1; i++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.GENERATOR_SYMBOL + i));
            }
            sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

            for (int i = 0; i < nodes.size(); i++) {
                final var tapBusNumber = i + 1;
                sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
                for (final Generator generator : generators) {
                    sb.append(String.format(
                            AmplUtils.PARAM_FORMAT,
                            generator.getBusNumber() - 1 == i ? generator.getGenerationMW() : 0)
                    );
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateGeneratorsPgenMinParameter(List<Node> nodes, List<HourlyLoad> hourlyLoads) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PGEN_MIN_SYMBOL + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN));

        final var generators = findGenerators(nodes);
        for (int h = 0; h < hourlyLoads.size(); h++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, get3DimHourStartLabel(h + 1)));

            for (int i = 1; i < generators.size() + 1; i++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.GENERATOR_SYMBOL + i));
            }
            sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

            for (int i = 0; i < nodes.size(); i++) {
                final var tapBusNumber = i + 1;
                sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
                for (final Generator generator : generators) {
                    final var minGeneration = BigDecimal.valueOf(generator.getGenerationMW() * PowerNetworkUtils.P_MIN_PERCENTAGE)
                            .setScale(2, RoundingMode.DOWN)
                            .doubleValue();
                    sb.append(String.format(
                            AmplUtils.PARAM_FORMAT,
                            generator.getBusNumber() - 1 == i ? minGeneration : 0)
                    );
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateSet(final String name, final int quantity, final String symbol) {
        StringBuilder sb = new StringBuilder("set " + name + " :=");
        for (int i = 0; i < quantity; i++) {
            sb.append(AmplUtils.DEFAULT_SET_SEPARATOR).append(symbol).append(i + 1);
        }
        sb.append(";\n\n");
        return sb.toString();
    }

    private String generateVariableCost(final List<Generator> generators) {
        final var params = List.of(AmplUtils.PARAM_VARIABLE_COST_SYMBOL);
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + ":"));
        sb.append(AmplUtils.PARAM_BEGIN);
        for (String param : params) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, param));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var generator : generators) {
            i++;
            final var variableCost = generator.getVariableCost();
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.GENERATOR_SYMBOL + i))
                    .append(String.format(AmplUtils.PARAM_FORMAT, variableCost))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateVParameter(final List<Node> nodes) {
        final var params = List.of(AmplUtils.PARAM_VOLTAGE_SYMBOL);
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
            final var voltage = node.getFinalVoltage();
            sb.append(AmplUtils.PARAM_BEGIN).append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i))
                    .append(String.format(AmplUtils.PARAM_FORMAT, voltage))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private List<Generator> findGenerators(final List<Node> nodes) {
        return nodes.stream()
                .map(Node::getGenerators)
                .flatMap(Collection::stream)
                .filter(generator -> generator.getGenerationMW() > 0.01)
                .collect(Collectors.toList());
    }

    private Integer getLineLimit(List<Line> nodeLines, int node1, int node2) {
        if (unconstrained) {
            return 9999;
        }
        return nodeLines.stream()
                .filter(nodeLine -> (nodeLine.getTapBusNumber() == node1 && nodeLine.getzBusNumber() == node2)
                        || (nodeLine.getTapBusNumber() == node2 && nodeLine.getzBusNumber() == node1))
                .map(Line::getLineMVARatingNo1)
                .findAny()
                .orElse(99999);
    }

    private String generateAmplModelInfo(final int numberOfNodes, final int numberOfLines, final double peak) {
        DateTimeFormatter dataPattern = DateTimeFormatter.ofPattern(AmplUtils.INFO_SECTION_DATA_PATTERN);
        return "# " + numberOfNodes + "-nodes power network with " + numberOfLines + " lines\n" +
                "# Author: Jakub Wrzosek\n" +
                "# Created: " + LocalDateTime.now().format(dataPattern) + "\n" +
                "# with peak: " + peak + "\n\n";
    }

    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private String formatFPVariables(final Double var) {
        return String.format("%.3f", var).replace(",", ".");
    }

    private String get3DimHourStartLabel(int hourNumber) {
        return "[*,*,H" + hourNumber + "]:";
    }
}
