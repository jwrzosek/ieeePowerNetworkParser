package com.company.parser;

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

public class ModelAmplWriter {

    public void writeAmplModelToFile(final String fileName,
                                     final List<Line> lines,
                                     final List<Node> nodes) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, fileName);
        final var fullModel = createFullModel(lines, nodes);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullModel(final List<Line> lines, final List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(lines.size(), nodes.size());
        final var busInfo = generateSet(AmplUtils.BUS_NAME, lines.size(), AmplUtils.BUS_SYMBOL);
        final var busParameters = generateBusParametersWithFixedLoad(lines);
        final var pgenParameter = generatePgenParameter(lines);
        final var qBusParameter = generateQBusParameter2(lines, nodes);
        final var yabParameter = generateAdmittanceParameter(lines, nodes);
        return sb
                .append(modelInfo)
                .append(busInfo)
                .append(busParameters)
                .append(pgenParameter)
                .append(qBusParameter)
                .append(yabParameter)
                .toString();
    }

    public void writeAmplModelWithHourlyLoadsToFile(final String fileName, final List<Line> lines, final List<Node> nodes,
                                                    final List<HourlyLoad> hourlyLoads, long numberOfGenerators) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, fileName);
        final var fullModel = createFullModelWithHourlyLoads(lines, nodes, numberOfGenerators);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullModelWithHourlyLoads(final List<Line> lines, final List<Node> nodes,
                                                  long numberOfGenerators) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(lines.size(), nodes.size());
        final var busInfo = generateSet(AmplUtils.BUS_NAME, lines.size(), AmplUtils.BUS_SYMBOL);
        final var generatorsInfo = generateSet(AmplUtils.GENERATOR_NAME, (int) numberOfGenerators, AmplUtils.GENERATOR_SYMBOL);
        final var busParameters = generateBusParametersWithFixedLoadAndHourlyLoads(lines);
        final var pgenParameter = generateGeneratorsPgenParameter(lines);
        final var qBusParameter = generateQBusParameter2(lines, nodes);
        final var yabParameter = generateAdmittanceParameter(lines, nodes);
        return sb
                .append(modelInfo)
                .append(busInfo)
                .append(generatorsInfo)
                .append(busParameters)
                .append(pgenParameter)
                .append(qBusParameter)
                .append(yabParameter)
                .toString();
    }

    public void writeAmplFullMultiStageModel(final String fileName, final List<Line> lines, final List<Node> nodes,
                                             final List<HourlyLoad> hourlyLoads, long numberOfGenerators) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, fileName);
        final var fullModel = createFullMultiStageModel(lines, nodes, hourlyLoads, numberOfGenerators);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullMultiStageModel(final List<Line> lines, final List<Node> nodes,
                                             final List<HourlyLoad> hourlyLoads, long numberOfGenerators) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(lines.size(), nodes.size());
        final var busInfo = generateSet(AmplUtils.BUS_NAME, lines.size(), AmplUtils.BUS_SYMBOL);
        final var generatorsInfo = generateSet(AmplUtils.GENERATOR_NAME, (int) numberOfGenerators, AmplUtils.GENERATOR_SYMBOL);
        final var busParameters = generateBusParametersWithFixedLoadAndHourlyLoads(lines);
        final var pgenParameter = generateGeneratorsPgenParameter(lines);
        final var qBusParameter = generateQBusParameter2(lines, nodes);
        final var yabParameter = generateAdmittanceParameter(lines, nodes);
        return sb
                .append(modelInfo)
                .append(busInfo)
                .append(generatorsInfo)
                .append(busParameters)
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

    private String generateQBusParameter2(List<Line> lines, List<Node> nodes) {
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
                sb.append(String.format(AmplUtils.PARAM_FORMAT, admittanceArray[i][j] != null ? 99999 : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateBusParametersWithVariableLoad(List<Line> lines) {
        final var params = List.of("Ka_load", "Ka_gen", "Pa_loadMin", "Pa_loadMax", "Pa_genMax", "V");
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
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice - 20))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getLoadMW() - 0.5*bus.getLoadMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getLoadMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getGenerators().get(0).getGenerationMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateBusParametersWithFixedLoadAndHourlyLoads(List<Line> lines) {
        final var params = List.of("Ka_load", "Ka_gen", "Pa_load", "V");
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
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice - 20))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getLoadMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateBusParametersWithFixedLoad(List<Line> lines) {
        final var params = List.of("Ka_load", "Ka_gen", "Pa_load", "Pa_genMax", "V");
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
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice))
                    .append(String.format(AmplUtils.PARAM_FORMAT, offerPrice - 20))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getLoadMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getGenerators().get(0).getGenerationMW()))
                    .append(String.format(AmplUtils.PARAM_FORMAT, bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateGeneratorsPgenParameter(List<Line> lines) {
        StringBuilder sb = new StringBuilder(String.format(
                AmplUtils.PARAM_FORMAT,
                AmplUtils.PARAM_SYMBOL + " " + AmplUtils.PARAM_PGEN_MAX_SYMBOL + ":\n"));
        sb.append(String.format(AmplUtils.PARAM_FORMAT, " "));

        final var generators = lines.stream()
                .map(Line::getGenerators)
                .flatMap(Collection::stream)
                .filter(generator -> generator.getGenerationMW() > 0.01)
                .collect(Collectors.toList());

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
                        generator.getBusNumber()-1 == i ? generator.getGenerationMW() : 0)
                );
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }


    private String generatePgenParameter(List<Line> lines) {
        final var paramName = "Pa_gen";
        StringBuilder sb = new StringBuilder("param " + paramName + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var bus : lines) {
            i++;
            sb.append("\t" + AmplUtils.BUS_SYMBOL).append(i);
            sb.append("\t\t").append(bus.getGenerators().get(0).getGenerationMW());
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

    private String generateAmplModelInfo(final int numberOfBuses, final int numberOfBranches) {
        DateTimeFormatter dataPattern = DateTimeFormatter.ofPattern(AmplUtils.INFO_SECTION_DATA_PATTERN);
        return "# " + numberOfBuses + "-nodes power network with " + numberOfBranches + " branches\n" +
                "# Author: Jakub Wrzosek\n" +
                "# Created: " + LocalDateTime.now().format(dataPattern) + "\n\n";
    }

    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private String formatFPVariables(final Double var) {
        return String.format("%.4f", var).replace(",", ".");
    }

    @Deprecated
    private String generateYabParameter(List<Line> lines, List<Node> nodes) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Y_ab:\n"));
        sb.append("\t\t\t");
        for (int i = 1; i < lines.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            final var links = nodes.stream().filter(node -> node.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < lines.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(node -> node.getzBusNumber() == zBusNumber).findFirst();
                sb.append(String.format("%-12s", first.isPresent() ? getYabStringValue(first.get(), AmplUtils.DC_MODEL_ON) : 0));
            }
            sb.append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString().replace(",", ".");
    }

    @Deprecated
    private String getYabStringValue(Node node, boolean dc) {
        Double Yab = dc ? calculateYabDC(node) : calculateYabAC(node);
        return String.format("%.4f", Yab);
    }

    @Deprecated
    private Double calculateYabDC(Node node) {
        Double r = node.getBranchResistanceR();
        return 1/r;
    }

    @Deprecated
    private Double calculateYabAC(Node node) {
        Double r = node.getBranchResistanceR();
        Double x = node.getBranchReactanceX();
        Double Gab = r / ((r * r) + (x * x));
        Double Omab = x / ((r * r) + (x * x));
        return Math.sqrt((Gab * Gab) + (Omab * Omab));
    }

    @Deprecated
    private String generateQBusParameter(List<Line> lines, List<Node> nodes) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Q_ab:\n"));
        sb.append("\t\t\t");
        for (int i = 1; i < lines.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < lines.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            final var links = nodes.stream().filter(node -> node.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < lines.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(node -> node.getzBusNumber() == zBusNumber).findFirst();
                sb.append(String.format("%-12s", first.isPresent() ? 99999 : 0));
            }
            sb.append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }
}
