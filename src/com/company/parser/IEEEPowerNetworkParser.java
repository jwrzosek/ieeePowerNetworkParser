package com.company.parser;

import com.company.parser.exceptions.DataNotFoundException;
import com.company.parser.exceptions.TooManyNumbersException;
import com.company.parser.model.Branch;
import com.company.parser.model.Bus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IEEEPowerNetworkParser {

    private static final String MODEL_DIR = "resources/models/";
    private static final String dir14nodes = "resources/ieee14nodesnetwork.txt";
    private static final String dir30nodes = "resources/ieee30nodesnetwork.txt";
    private static final String dir57nodes = "resources/ieee57nodesnetwork.txt";
    private static final String dir118nodes = "resources/ieee118nodesnetwork.txt";
    private static final String dir300nodes = "resources/ieee300nodesnetwork.txt";

    private List<String> powerNetworkDataLines = new ArrayList<>();
    private List<String> busDataLines = new ArrayList<>();
    private List<Bus> buses = new ArrayList<>();
    private List<Branch> branches = new ArrayList<>();

    public void parse() {
        System.out.println("> Parsing ieee power network data model started...");
        readDataFile(dir14nodes);
        System.out.println("> analyzing network of " + getNumberOfNodes() + " nodes and " + getNumberOfBranches() + " branches");
        System.out.println("> parsing bus data...");
        parseBusDataLines();
        System.out.println("> parsing bus data finished");

        System.out.println("> parsing branch data...");
        parseBranchDataLines();
        System.out.println("> parsing branch data finished");
        System.out.println("> Parsing finished");
        System.out.println("> Parsing ieee power network data model finished successfully");
        ModelAmplWriter modelAmplWriter = new ModelAmplWriter();
        modelAmplWriter.writeAmplModelToFile("model.dat", buses, branches);
    }

    private void readDataFile(String directory) {
        Path path = Paths.get(directory);
        try {
            powerNetworkDataLines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getNumberOfNodes() {
        final var busData = powerNetworkDataLines.stream()
                .filter(line -> line.contains("BUS DATA"))
                .findAny()
                .orElseThrow(() -> new DataNotFoundException("Number of nodes data line not found"));
        return findNumberOfDataEntities(busData);
    }

    private Integer getNumberOfBranches() {
        final var busData = powerNetworkDataLines.stream()
                .filter(line -> line.contains("BRANCH DATA"))
                .findAny()
                .orElseThrow(() -> new DataNotFoundException("Number of nodes data line not found"));
        return findNumberOfDataEntities(busData);
    }

    private Integer findNumberOfDataEntities(String stringToSearch) {
        Pattern integerPattern = Pattern.compile("-?\\d+");
        Matcher matcher = integerPattern.matcher(stringToSearch);

        List<String> integerList = new ArrayList<>();
        while (matcher.find()) {
            integerList.add(matcher.group());
        }
        if (integerList.size() != 1) {
            throw new TooManyNumbersException("Too many numbers in bus data line");
        }
        return Integer.parseInt(integerList.get(0));
    }

    private void parseBranchDataLines() {
        boolean branchDataStarted = false;
        for (String line : powerNetworkDataLines) {
            if (line.contains("BRANCH DATA") && !branchDataStarted) {
                branchDataStarted = true;
                continue;
            }
            if (line.contains("-999") && branchDataStarted) {
                branchDataStarted = false;
            }
            if (branchDataStarted) {
                final var branch = parseBranchDataLine(line);
                branches.add(branch);
            }
        }
    }

    private Branch parseBranchDataLine(final String line) {
        Branch branch = new Branch();

        final var tapBusNumber = line.substring(0, 4).trim();
        branch.withTapBusNumber(Integer.parseInt(tapBusNumber));
        final var zBusNumber = line.substring(5, 9).trim();
        branch.withzBusNumber(Integer.parseInt(zBusNumber));
        final var loadFlowArea = line.substring(10, 12).trim();
        branch.withLoadFlowArea(Integer.parseInt(loadFlowArea));
        final var lossZone = line.substring(13, 15).trim();
        branch.withLossZone(Integer.parseInt(lossZone));
        final var circuit = line.substring(16, 17).trim();
        branch.withCircuit(Integer.parseInt(circuit));
        final var type = line.substring(18, 19).trim();
        branch.withType(Integer.parseInt(type));
        final var branchResistanceR = line.substring(19, 28).trim();
        branch.withBranchResistanceR(Double.parseDouble(branchResistanceR));
        final var branchReactanceX = line.substring(28, 39).trim();
        branch.withBranchReactanceX(Double.parseDouble(branchReactanceX));
        final var lineChargingB = line.substring(39, 49).trim();
        branch.withLineChargingB(Double.parseDouble(lineChargingB));
        final var lineMVARatingNo1 = line.substring(50, 55).trim();
        branch.withLineMVARatingNo1(Integer.parseInt(lineMVARatingNo1));
        final var lineMVARatingNo2 = line.substring(56, 61).trim();
        branch.withLineMVARatingNo2(Integer.parseInt(lineMVARatingNo2));
        final var lineMVARatingNo3 = line.substring(62, 67).trim();
        branch.withLineMVARatingNo3(Integer.parseInt(lineMVARatingNo3));
        final var controlBusNumber = line.substring(69, 72).trim();
        branch.withControlBusNubmer(Integer.parseInt(controlBusNumber));
        final var side = line.substring(73, 74).trim();
        branch.withSide(Integer.parseInt(side));
        final var transformerFinalTurnsRatio = line.substring(76, 81).trim();
        branch.withTransformerFinalTurnsRatio(Double.parseDouble(transformerFinalTurnsRatio));
        final var transformerPhaseShifterFinalAngle = line.substring(83, 89).trim();
        branch.withTransformerPhaseShifterFinalAngle(Double.parseDouble(transformerPhaseShifterFinalAngle));
        final var minimumTapOrPhaseShift = line.substring(89, 96).trim();
        branch.withMinimumTapOrPhaseShift(Double.parseDouble(minimumTapOrPhaseShift));
        final var maximumTapOrPhaseShift = line.substring(96, 103);
        branch.withMaximumTapOrPhaseShift(Double.parseDouble(maximumTapOrPhaseShift));
        final var stepSize = line.substring(104, 110).trim();
        branch.withStepSize(Double.parseDouble(stepSize));
        final var minimumVoltageMVARorMWLimit = line.substring(111, 118).trim();
        branch.withMinimumVoltageMVARorMWLimit(Double.parseDouble(minimumVoltageMVARorMWLimit));
        final var maximumVoltageMVARorMWLimit = line.substring(118, 121).trim();
        branch.withMaximumVoltageMVARorMWLimit(Double.parseDouble(maximumVoltageMVARorMWLimit));
        final var admittance = getAdmittanceValue(branch, false);
        branch.withAdmittance(admittance);
        return branch;
    }

    private Double getAdmittanceValue(Branch branch, boolean dc) {
        return dc ? calculateYabDC(branch) : calculateYabAC(branch);
    }

    private Double calculateYabDC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        return 1/r;
    }

    private Double calculateYabAC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        Double x = branch.getBranchReactanceX();
        Double Gab = r / ((r * r) + (x * x));
        Double Omab = x / ((r * r) + (x * x));
        return Math.sqrt((Gab * Gab) + (Omab * Omab));
    }

    private void parseBusDataLines() {
        boolean busDataStarted = false;
        for (String line : powerNetworkDataLines) {
            if (line.contains("BUS DATA") && !busDataStarted) {
                busDataStarted = true;
                continue;
            }
            if (line.contains("-999") && busDataStarted) {
                busDataStarted = false;
            }
            if (busDataStarted) {
                final var bus = parseBusDataLine(line);
                buses.add(bus);
            }
        }
    }

    private Bus parseBusDataLine(final String busDataLine) {
        Bus bus = new Bus();
        final var busNumber = busDataLine.substring(0, 4).trim(); // bus number
        bus.withBusNumber(Integer.parseInt(busNumber));
        final var busName = busDataLine.substring(5, 17).trim();// bus name
        bus.withBusName(busName);
        final var loadFlowAreaNumber = busDataLine.substring(18, 20).trim(); // load flow area number
        bus.withLoadFlowAreaNumber(Integer.parseInt(loadFlowAreaNumber));
        final var lossZoneNumber = busDataLine.substring(20, 23).trim(); // loss zone number
        bus.withLossZoneNumber(Integer.parseInt(lossZoneNumber));
        final var type = busDataLine.substring(24, 26).trim(); // type
        bus.withType(Integer.parseInt(type));
        final var finalVoltage = busDataLine.substring(27, 33).trim(); // final voltage
        bus.withFinalVoltage(Double.parseDouble(finalVoltage));
        final var finalAngle = busDataLine.substring(33, 40).trim(); // final angle
        bus.withFinalAngle(Double.parseDouble(finalAngle));
        final var loadMW = busDataLine.substring(40, 49).trim(); // load MW
        bus.withLoadMW(Double.parseDouble(loadMW));
        final var loadMVAR = busDataLine.substring(49, 59).trim(); // load MVAR
        bus.withLoadMVAR(Double.parseDouble(loadMVAR));
        final var generationMW = busDataLine.substring(59, 67); // generation MW
        bus.withGenerationMW(Double.parseDouble(generationMW));
        final var generationMVAR = busDataLine.substring(67, 75).trim(); // generatrion MVAR
        bus.withGenerationMVAR(Double.parseDouble(generationMVAR));
        final var baseKV = busDataLine.substring(76, 84).trim(); // base kV
        bus.withBaseKV(Double.parseDouble(baseKV));
        final var desiredVolts = busDataLine.substring(84, 90).trim(); // desired volts
        bus.withDesiredVolts(Double.parseDouble(desiredVolts));
        final var maximumMVARorVoltageLimit = busDataLine.substring(90, 98).trim(); // maximum MVAR or voltage limit
        bus.withMaximumMVARorVoltageLimit(Double.parseDouble(maximumMVARorVoltageLimit));
        final var minimumMVARorVoltageLimit = busDataLine.substring(98, 106).trim(); // minimum MVAR or voltage limit
        bus.withMinimumMVARorVoltageLimit(Double.parseDouble(minimumMVARorVoltageLimit));
        final var shuntConductanceG = busDataLine.substring(106, 114).trim(); // shunt conductance G (per unit)
        bus.withShuntConductanceG(Double.parseDouble(shuntConductanceG));
        final var shuntSusceptanceB = busDataLine.substring(114, 122).trim(); // shunt susceptance B (per unit)
        bus.withShuntSusceptanceB(Double.parseDouble(shuntSusceptanceB));
        final var remoteControlledBusNumber = busDataLine.substring(123, 127).trim(); // remote controlled bus number
        bus.withRemoteControlledBusNumber(Integer.parseInt(remoteControlledBusNumber));

        return bus;
    }
}
