package com.company.parser;

import com.company.parser.model.Generator;
import com.company.parser.model.HourlyLoad;
import com.company.parser.model.Line;
import com.company.parser.model.Node;
import com.company.parser.util.AmplUtils;

import java.io.IOException;
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

    public void writeAmplFullMultiStageModel(final String directoryName, final String fileName, final List<Line> lines,
                                             final List<Node> nodes, final List<HourlyLoad> hourlyLoads, long numberOfGenerators,
                                             int lmpNode, double peak) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, directoryName, fileName);
        final var fullModel = createFullMultiStageModel(lines, nodes, hourlyLoads, numberOfGenerators, lmpNode, peak);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRunScriptForBatchToFile(final String directory, final int size) {
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
        sb.append("printf \"Results:\\n\" >> "+ directory +"/results.out;\n\n");

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
        for (int i = 0; i<size; i++) {
            sb.append("reset;\n")
                    .append("model " + directory + "/model_min_balancing_cost.mod\n")
                    .append("data " + directory + "/common.dat\n")
                    .append("data " + directory + "/").append(i + 1).append(".dat\n")
                    .append("solve;\n")
                    .append("printf \"%-12s %.2f\\n\", \"Q"+ (i+1) + ":\", Q >> " + directory + "/results.out;\n\n");
        }
        sb.append("printf \"----> RESULT SET END\\n\\n\" >> " + directory + "/results.out;\n");
        return sb.toString();
    }

    public void writeSingleRunScriptToFile(final String directoryName, final int size) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, directoryName, "single.run");
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

    private String createCommonData(final List<Line> lines) {
        StringBuilder sb = new StringBuilder();
        final var busParameters = generateBusParametersForMultiStageCase(lines);
        return sb.append(busParameters).toString();
    }

    private String createFullMultiStageModel(final List<Line> lines, final List<Node> nodes,
                                             final List<HourlyLoad> hourlyLoads, long numberOfGenerators, int lmpNode, double peak) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(lines.size(), nodes.size(), peak);
        final var busInfo = generateSet(AmplUtils.BUS_NAME, lines.size(), AmplUtils.BUS_SYMBOL);
        final var generatorsInfo = generateSet(AmplUtils.GENERATOR_NAME, (int) numberOfGenerators, AmplUtils.GENERATOR_SYMBOL);
        final var hourlyLoadInfo = generateSet(AmplUtils.TIME_PERIOD_NAME, hourlyLoads.size(), AmplUtils.TIME_PERIOD_SYMBOL);
        final var busParameters = generateBusParametersForMultiStageCase(lines);
        final var loadParameter = generateMultiStageLoadMWData(lines, hourlyLoads, lmpNode, peak);
        final var pgenParameter = generateGeneratorsPgenParameter(lines, hourlyLoads);
        final var qBusParameter = generateQBusParameter(lines, nodes);
        final var yabParameter = generateAdmittanceParameter(lines, nodes);
        return sb
                .append(modelInfo)
                .append(busInfo)
                .append(generatorsInfo)
                .append(hourlyLoadInfo)
                //.append(busParameters)
                .append(loadParameter)
                .append(pgenParameter)
                .append(qBusParameter)
                .append(yabParameter)
                .toString();
    }

    private String generateAdmittanceParameter(List<Line> lines, List<Node> nodes) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_ADMITTANCE_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < lines.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(lines, nodes);

        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < lines.size(); j++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? formatFPVariables(admittanceArray[i][j]) : 0.0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private Double[][] getAdmittanceArray(List<Line> lines, List<Node> nodes) {
        final var numberOfBuses = lines.size();
        Double[][] admittanceArray = initializeAdmittanceArray(numberOfBuses);
        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            final var links = nodes.stream().filter(node -> node.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < lines.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(node -> node.getzBusNumber() == zBusNumber).findFirst();
                if (first.isPresent()) {
                    final var admittance = first.get().getAdmittance();
                    admittanceArray[i][j] = admittance;
                    admittanceArray[j][i] = admittance;
                }
            }
        }
        return admittanceArray;
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

    private String generateQBusParameter(List<Line> lines, List<Node> nodes) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_LINE_CAPACITY_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));
        for (int i = 1; i < lines.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(lines, nodes);

        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < lines.size(); j++) {
                //sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? 77777 : 0));
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? getLineLimit(nodes, i+1, j+1) : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateBusParametersForMultiStageCase(List<Line> lines) {
        final var params = List.of("Ka_load", "Ka_gen", "V");
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + ":"));
        for (String param : params) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, param));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var bus : lines) {
            i++;
            final var offerPrice = getRandomInteger(50, 300);
            final var generation = bus.getGenerators().stream().map(Generator::getGenerationMW).filter(gen -> gen > 0.001).findFirst().orElse(0.0D);
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT,  generation > 0.01 ? offerPrice - 20 : 999999))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateMultiStageLoadMWData(List<Line> lines, List<HourlyLoad> hourlyLoads, int lmpNode, double peak) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PLOAD_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));

        for (int i = 1; i < hourlyLoads.size() + 1; i++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.TIME_PERIOD_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            final var bus = lines.get(i);
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < hourlyLoads.size(); j++) {
                //final var hourlyLoadPercentage = hourlyLoads.get(j).getSummerPeakLoadPercentageWkdy();
                //final var load = bus.getLoadMW() * hourlyLoadPercentage;
                final var load = bus.getLoadMW() * peak;
                sb.append(String.format(
                        AmplUtils.PARAM_FORMAT,
                        formatFPVariables(i == lmpNode ? load + 1.0 : load))
                );
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateGeneratorsPgenParameter(List<Line> lines, List<HourlyLoad> hourlyLoads) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PGEN_MAX_SYMBOL + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN));

        final var generators = findGenerators(lines);
        for (int h = 0; h<hourlyLoads.size(); h++) {
            sb.append(String.format(AmplUtils.PARAM_FORMAT, get3DimHourStartLabel(h+1)));

            for (int i = 1; i < generators.size() + 1; i++) {
                sb.append(String.format(AmplUtils.PARAM_FORMAT, AmplUtils.GENERATOR_SYMBOL + i));
            }
            sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

            for (int i = 0; i < lines.size(); i++) {
                final var tapBusNumber = i + 1;
                sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
                for (int j = 0; j < generators.size(); j++) {
                    final var generator = generators.get(j);
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

    private String generateSet(final String name, final int quantity, final String symbol) {
        StringBuilder sb = new StringBuilder("set " + name + " :=");
        for (int i = 0; i < quantity; i++) {
            sb.append(AmplUtils.DEFAULT_SET_SEPARATOR).append(symbol).append(i + 1);
        }
        sb.append(";\n\n");
        return sb.toString();
    }

    private List<Generator> findGenerators(final List<Line> lines) {
        return lines.stream()
                .map(Line::getGenerators)
                .flatMap(Collection::stream)
                .filter(generator -> generator.getGenerationMW() > 0.01)
                .collect(Collectors.toList());
    }

    private Integer getLineLimit(List<Node> nodes, int node1, int node2) {
        if (unconstrained) {
            return 9999;
        }
        return  nodes.stream()
                .filter(node -> (node.getTapBusNumber() == node1 && node.getzBusNumber() == node2) || (node.getTapBusNumber() == node2 && node.getzBusNumber() == node1))
                .map(Node::getLineMVARatingNo1)
                .findAny()
                .orElse(99999);
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
        return String.format("%.3f", var).replace(",", ".");
    }

    private String get3DimHourStartLabel(int hourNumber) {
        return "[*,*,H" + hourNumber+ "]:";
    }
}
