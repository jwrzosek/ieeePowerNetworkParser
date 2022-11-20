package com.company.parser;

import com.company.parser.model.Branch;
import com.company.parser.model.Bus;
import com.company.parser.util.AmplUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ModelAmplWriter {

    public void writeAmplModelToFile(final String fileName, final List<Bus> buses, final List<Branch> branches) {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH, fileName);
        final var fullModel = createFullModel(buses, branches);
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, fullModel, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFullModel(final List<Bus> buses, final List<Branch> branches) {
        StringBuilder sb = new StringBuilder();
        final var modelInfo = generateAmplModelInfo(buses.size(), branches.size());
        final var busInfo = generateSet("BUSES", buses.size(), AmplUtils.BUS_SYMBOL);
        final var busParameters = generateBusParameters(buses);
        final var pgenParameter = generatePgenParameter(buses);
        final var qBusParameter = generateQBusParameter2(buses, branches);
        final var yabParameter = generateYabParameter2(buses, branches);
        return sb
                .append(modelInfo)
                .append(busInfo)
                .append(busParameters)
                .append(pgenParameter)
                .append(qBusParameter)
                .append(yabParameter)
                .toString();
    }

    private String generateYabParameter2(List<Bus> buses, List<Branch> branches) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Y_ab:\n"));
        sb.append(String.format("%-12s", " "));
        for (int i = 1; i < buses.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(buses, branches);

        for (int i = 0; i < buses.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < buses.size(); j++) {
                sb.append(String.format("%-12s", admittanceArray[i][j] != null ? formatFPVariables(admittanceArray[i][j]) : 0.0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString().replace(",", ".");
    }

    private Double[][] getAdmittanceArray(List<Bus> buses, List<Branch> branches) {
        final var numberOfBuses = buses.size();
        Double[][] admittanceArray = initializeAdmittanceArray(numberOfBuses);
        for (int i = 0; i < buses.size(); i++) {
            final var tapBusNumber = i + 1;
            final var links = branches.stream().filter(branch -> branch.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < buses.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(branch -> branch.getzBusNumber() == zBusNumber).findFirst();
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

    @Deprecated
    private String generateYabParameter(List<Bus> buses, List<Branch> branches) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Y_ab:\n"));
        sb.append("\t\t\t");
        for (int i = 1; i < buses.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < buses.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            final var links = branches.stream().filter(branch -> branch.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < buses.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(branch -> branch.getzBusNumber() == zBusNumber).findFirst();
                sb.append(String.format("%-12s", first.isPresent() ? getYabStringValue(first.get(), AmplUtils.DC_MODEL_ON) : 0));
            }
            sb.append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString().replace(",", ".");
    }

    @Deprecated
    private String getYabStringValue(Branch branch, boolean dc) {
        Double Yab = dc ? calculateYabDC(branch) : calculateYabAC(branch);
        return String.format("%.4f", Yab);
    }

    @Deprecated
    private Double calculateYabDC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        return 1/r;
    }

    @Deprecated
    private Double calculateYabAC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        Double x = branch.getBranchReactanceX();
        Double Gab = r / ((r * r) + (x * x));
        Double Omab = x / ((r * r) + (x * x));
        return Math.sqrt((Gab * Gab) + (Omab * Omab));
    }

    private String generateQBusParameter2(List<Bus> buses, List<Branch> branches) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Q_ab:\n"));
        sb.append(String.format("%-12s", " "));
        for (int i = 1; i < buses.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        final var admittanceArray = getAdmittanceArray(buses, branches);

        for (int i = 0; i < buses.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            for (int j = 0; j < buses.size(); j++) {
                sb.append(String.format("%-12s", admittanceArray[i][j] != null ? 99999 : 0));
            }
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    @Deprecated
    private String generateQBusParameter(List<Bus> buses, List<Branch> branches) {
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param Q_ab:\n"));
        sb.append("\t\t\t");
        for (int i = 1; i < buses.size() + 1; i++) {
            sb.append(String.format("%-12s", AmplUtils.BUS_SYMBOL + i));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);

        for (int i = 0; i < buses.size(); i++) {
            final var tapBusNumber = i + 1;
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(tapBusNumber).append("\t\t");
            final var links = branches.stream().filter(branch -> branch.getTapBusNumber() == tapBusNumber).collect(Collectors.toList());
            for (int j = 0; j < buses.size(); j++) {
                final var zBusNumber = j + 1;
                final var first = links.stream().filter(branch -> branch.getzBusNumber() == zBusNumber).findFirst();
                sb.append(String.format("%-12s", first.isPresent() ? 99999 : 0));
            }
            sb.append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generateBusParameters(List<Bus> buses) {
        //final var params = List.of("Ka_load", "Ka_gen", "Pa_load", "Pa_loadMax", "Pa_genMax", "theta", "V");
        final var params = List.of("Ka_load", "Ka_gen", "Pa_loadMin", "Pa_loadMax", "Pa_genMax", "V");
        StringBuilder sb = new StringBuilder(String.format("%-12s", "param:"));
        for (String param : params) {
            sb.append(String.format("%-12s", param));
        }
        sb.append(AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var bus : buses) {
            i++;
            final var offerPrice = getRandomInteger(50, 300);
            sb.append(AmplUtils.DEFAULT_PARAM_SEPARATOR).append(AmplUtils.BUS_SYMBOL).append(i).append("\t\t")
                    .append(String.format("%-12s", offerPrice))
                    .append(String.format("%-12s", offerPrice - 20))
                    .append(String.format("%-12s", bus.getLoadMW() - 0.5*bus.getLoadMW()))
                    .append(String.format("%-12s", bus.getLoadMW()))
                    .append(String.format("%-12s", bus.getGenerationMW()))
                    //.append(String.format("%-12s", bus.getFinalAngle()))
                    .append(String.format("%-12s", bus.getFinalVoltage()))
                    .append("\n");
        }
        sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR);
        return sb.toString();
    }

    private String generatePgenParameter(List<Bus> buses) {
        final var paramName = "Pa_gen";
        StringBuilder sb = new StringBuilder("param " + paramName + AmplUtils.DEFAULT_PARAM_EQUALS_SIGN);
        int i = 0;
        for (var bus : buses) {
            i++;
            sb.append("\t" + AmplUtils.BUS_SYMBOL).append(i);
            sb.append("\t\t").append(bus.getGenerationMW());
            sb.append("\n");
        }

        return sb.append(AmplUtils.DEFAULT_PARAM_END_SEPARATOR).toString();
    }

    private String generateSet(final String name,final int quantity, final String symbol) {
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
        return String.format("%.4f", var);
    }

}
