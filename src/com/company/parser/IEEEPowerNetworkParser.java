package com.company.parser;

import com.company.parser.exceptions.DataNotFoundException;
import com.company.parser.exceptions.TooManyNumbersException;
import com.company.parser.model.Branch;
import com.company.parser.model.Bus;
import com.company.parser.model.Generator;
import com.company.parser.model.HourlyLoad;
import com.company.parser.util.InfoUtils;
import com.company.parser.util.PowerNetworkUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IEEEPowerNetworkParser {

    private List<String> powerNetworkDataLines = new ArrayList<>();
    private List<String> hourlyLoadDataLines = new ArrayList<>();
    private List<Bus> buses = new ArrayList<>();
    private List<Branch> branches = new ArrayList<>();
    private List<HourlyLoad> hourlyLoads = new ArrayList<>();

    public IEEEPowerNetworkParser() {
        InfoUtils.printInfo("> Parsing ieee power network data model started...");
        //readDataFile(PowerNetworkUtils.DIR_30_NODES);
        readDataFile("resources/powernetworkdata/ieee30nodesnetworkTmp.txt");

        // parsing hourly load data file
        InfoUtils.printInfo("> parsing hourly load data...");
        readHourlyLoadDataFile(PowerNetworkUtils.HOURLY_LOAD_DATA_10HOURS);
        parseHourlyLoadDataLines();
        InfoUtils.printInfo("> parsing hourly load data finished");

        InfoUtils.printInfo("> analyzing network of " + getNumberOfNodes() + " nodes and " + getNumberOfBranches() + " branches");
        InfoUtils.printInfo("> parsing bus data...");
        parseBusDataLines();
        InfoUtils.printInfo("> number of generators in the network: " + getNumberOfGenerators());
        InfoUtils.printInfo("> parsing bus data finished");

        InfoUtils.printInfo("> parsing branch data...");
        parseBranchDataLines();
        InfoUtils.printInfo("> parsing branch data finished");

        InfoUtils.printInfo("> Parsing finished");
        InfoUtils.printInfo("> Parsing ieee power network data model finished successfully");
    }

    public void parseMultiStageCase(final String directoryName, final String filename, boolean unconstrained, int lmpNode) {
        InfoUtils.printInfo("> Writing AMPL model file...");
        MultiStageModelAmplWriter modelAmplWriter = new MultiStageModelAmplWriter(unconstrained);
        modelAmplWriter.writeAmplFullMultiStageModel(directoryName, filename, buses, branches, hourlyLoads, getNumberOfGenerators(), lmpNode);
        InfoUtils.printInfo("> AMPL model file writing finished");
    }

    public void writeCommonData(String directory) {
        MultiStageModelAmplWriter modelAmplWriter = new MultiStageModelAmplWriter(true);
        modelAmplWriter.writeCommonDataToFile(buses, directory);
    }

    public void writeRunScript(final String directory, final int size) {
        MultiStageModelAmplWriter modelAmplWriter = new MultiStageModelAmplWriter(true);
        modelAmplWriter.writeRunScriptToFile(directory, size);
    }

    public void parse() {
        InfoUtils.printInfo("> Parsing ieee power network data model started...");
        readDataFile(PowerNetworkUtils.DIR_14_NODES);

        InfoUtils.printInfo("> analyzing network of " + getNumberOfNodes() + " nodes and " + getNumberOfBranches() + " branches");
        InfoUtils.printInfo("> parsing bus data...");
        parseBusDataLines();
        InfoUtils.printInfo("> parsing bus data finished");

        InfoUtils.printInfo("> parsing branch data...");
        parseBranchDataLines();
        InfoUtils.printInfo("> parsing branch data finished");

        InfoUtils.printInfo("> Parsing finished");
        InfoUtils.printInfo("> Parsing ieee power network data model finished successfully");

        InfoUtils.printInfo("> Writing AMPL model file...");
        ModelAmplWriter modelAmplWriter = new ModelAmplWriter();
        modelAmplWriter.writeAmplModelToFile("model.dat", buses, branches);
        InfoUtils.printInfo("> AMPL model file writing finished");
    }

    public void parseWithHourlyLoad() {
        InfoUtils.printInfo("> Parsing ieee power network data model started...");
        readDataFile(PowerNetworkUtils.DIR_30_NODES);

        // parsing hourly load data file
        InfoUtils.printInfo("> parsing hourly load data...");
        readHourlyLoadDataFile(PowerNetworkUtils.HOURLY_LOAD_DATA_24_HOURS);
        parseHourlyLoadDataLines();
        InfoUtils.printInfo("> parsing hourly load data finished");

        InfoUtils.printInfo("> analyzing network of " + getNumberOfNodes() + " nodes and " + getNumberOfBranches() + " branches");
        InfoUtils.printInfo("> parsing bus data...");
        parseBusDataLines();
        InfoUtils.printInfo("> number of generators in the network: " + getNumberOfGenerators());
        InfoUtils.printInfo("> parsing bus data finished");

        InfoUtils.printInfo("> parsing branch data...");
        parseBranchDataLines();
        InfoUtils.printInfo("> parsing branch data finished");

        InfoUtils.printInfo("> Parsing finished");
        InfoUtils.printInfo("> Parsing ieee power network data model finished successfully");

        InfoUtils.printInfo("> Writing AMPL model file...");
        ModelAmplWriter modelAmplWriter = new ModelAmplWriter();
        modelAmplWriter.writeAmplModelWithHourlyLoadsToFile("hourlyLoadsTmp.dat", buses, branches, hourlyLoads, getNumberOfGenerators());
        InfoUtils.printInfo("> AMPL model file writing finished");
    }

    private void readDataFile(final String directory) {
        Path path = Paths.get(directory);
        try {
            powerNetworkDataLines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHourlyLoadDataFile(final String directory) {
        Path path = Paths.get(directory);
        try {
            hourlyLoadDataLines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getNumberOfNodes() {
        final var busData = powerNetworkDataLines.stream()
                .filter(line -> line.contains(PowerNetworkUtils.CDF_BUS_SECTION_START_INDICATOR))
                .findAny()
                .orElseThrow(() -> new DataNotFoundException("Number of nodes data line not found"));
        return findNumberOfDataEntities(busData);
    }

    private long getNumberOfGenerators() {
        return buses.stream()
                .map(Bus::getGenerators)
                .flatMap(Collection::stream)
                .map(Generator::getGenerationMW)
                .filter(generation -> generation > 0.01)
                .count();
    }

    private Integer getNumberOfBranches() {
        final var busData = powerNetworkDataLines.stream()
                .filter(line -> line.contains(PowerNetworkUtils.CDF_BRANCH_SECTION_START_INDICATOR))
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
            if (line.contains(PowerNetworkUtils.CDF_BRANCH_SECTION_START_INDICATOR) && !branchDataStarted) {
                branchDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.CDF_SECTION_END_INDICATOR) && branchDataStarted) {
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
        return dc ? calculateAdmittanceDC(branch) : calculateAdmittanceAC(branch);
    }

    private Double calculateAdmittanceDC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        return 1/r;
    }

    private Double calculateAdmittanceAC(Branch branch) {
        Double r = branch.getBranchResistanceR();
        Double x = branch.getBranchReactanceX();
        Double conductanceG = r / ((r * r) + (x * x));
        Double susceptanceOm = x / ((r * r) + (x * x));
        return Math.sqrt((conductanceG * conductanceG) + (susceptanceOm * susceptanceOm));
    }

    private void parseBusDataLines() {
        boolean busDataStarted = false;
        for (String line : powerNetworkDataLines) {
            if (line.contains(PowerNetworkUtils.CDF_BUS_SECTION_START_INDICATOR) && !busDataStarted) {
                busDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.CDF_SECTION_END_INDICATOR) && busDataStarted) {
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
        final var busNumber = busDataLine.substring(0, 4).trim();
        bus.withBusNumber(Integer.parseInt(busNumber));
        final var busName = busDataLine.substring(5, 17).trim();
        bus.withBusName(busName);
        final var loadFlowAreaNumber = busDataLine.substring(18, 20).trim();
        bus.withLoadFlowAreaNumber(Integer.parseInt(loadFlowAreaNumber));
        final var lossZoneNumber = busDataLine.substring(20, 23).trim();
        bus.withLossZoneNumber(Integer.parseInt(lossZoneNumber));
        final var type = busDataLine.substring(24, 26).trim();
        bus.withType(Integer.parseInt(type));
        final var finalVoltage = busDataLine.substring(27, 33).trim();
        bus.withFinalVoltage(Double.parseDouble(finalVoltage));
        final var finalAngle = busDataLine.substring(33, 40).trim();
        bus.withFinalAngle(Double.parseDouble(finalAngle));
        final var loadMW = busDataLine.substring(40, 49).trim();
        bus.withLoadMW(Double.parseDouble(loadMW));
        final var loadMVAR = busDataLine.substring(49, 59).trim();
        bus.withLoadMVAR(Double.parseDouble(loadMVAR));
        final var generationMW = busDataLine.substring(59, 67);
        bus.withGenerator(new Generator()
                .withBusNumber(Integer.parseInt(busNumber))
                .withGenerationMW(Double.parseDouble(generationMW))
        );
        final var generationMVAR = busDataLine.substring(67, 75).trim();
        bus.withGenerationMVAR(Double.parseDouble(generationMVAR));
        final var baseKV = busDataLine.substring(76, 84).trim();
        bus.withBaseKV(Double.parseDouble(baseKV));
        final var desiredVolts = busDataLine.substring(84, 90).trim();
        bus.withDesiredVolts(Double.parseDouble(desiredVolts));
        final var maximumMVARorVoltageLimit = busDataLine.substring(90, 98).trim();
        bus.withMaximumMVARorVoltageLimit(Double.parseDouble(maximumMVARorVoltageLimit));
        final var minimumMVARorVoltageLimit = busDataLine.substring(98, 106).trim();
        bus.withMinimumMVARorVoltageLimit(Double.parseDouble(minimumMVARorVoltageLimit));
        final var shuntConductanceG = busDataLine.substring(106, 114).trim();
        bus.withShuntConductanceG(Double.parseDouble(shuntConductanceG));
        final var shuntSusceptanceB = busDataLine.substring(114, 122).trim();
        bus.withShuntSusceptanceB(Double.parseDouble(shuntSusceptanceB));
        final var remoteControlledBusNumber = busDataLine.substring(123, 127).trim();
        bus.withRemoteControlledBusNumber(Integer.parseInt(remoteControlledBusNumber));

        return bus;
    }

    private void parseHourlyLoadDataLines() {
        boolean hourlyLoadDataStarted = false;
        for (String line : hourlyLoadDataLines) {
            if (line.contains(PowerNetworkUtils.HOURLY_LOAD_DATA_START_INDICATOR) && !hourlyLoadDataStarted) {
                hourlyLoadDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.CDF_SECTION_END_INDICATOR) && hourlyLoadDataStarted) {
                hourlyLoadDataStarted = false;
            }
            if (hourlyLoadDataStarted) {
                final var hourlyLoad = parseHourlyLoad(line);
                hourlyLoads.add(hourlyLoad);
            }
        }
    }

    private HourlyLoad parseHourlyLoad(final String hourlyLoadDataLine) {
        HourlyLoad hourlyLoad = new HourlyLoad();
        final var period = hourlyLoadDataLine.substring(0, 9).trim();
        hourlyLoad.withPeriod(period);
        // winter 9,12 -> 18,21
        // summer 27,30 -> 36,39
        // spring 45,48 -> 54,57
        final var winterPeakLoadPercentageWkdy = hourlyLoadDataLine.substring(9, 12).trim();
        hourlyLoad.withWinterPeakLoadPercentageWkdy(getHourlyLoadValue(winterPeakLoadPercentageWkdy));
        final var winterPeakLoadPercentageWknd = hourlyLoadDataLine.substring(18, 21).trim();
        hourlyLoad.withWinterPeakLoadPercentageWknd(getHourlyLoadValue(winterPeakLoadPercentageWknd));

        final var summerPeakLoadPercentageWkdy = hourlyLoadDataLine.substring(27, 30).trim();
        hourlyLoad.withSummerPeakLoadPercentageWkdy(getHourlyLoadValue(summerPeakLoadPercentageWkdy));
        final var summerPeakLoadPercentageWknd = hourlyLoadDataLine.substring(36, 39).trim();
        hourlyLoad.withSummerPeakLoadPercentageWknd(getHourlyLoadValue(summerPeakLoadPercentageWknd));

        final var springFallPeakLoadPercentageWkdy = hourlyLoadDataLine.substring(45, 48).trim();
        hourlyLoad.withSpringFallPeakLoadPercentageWkdy(getHourlyLoadValue(springFallPeakLoadPercentageWkdy));
        final var springFallPeakLoadPercentageWknd = hourlyLoadDataLine.substring(54, 57).trim();
        hourlyLoad.withSpringFallPeakLoadPercentageWknd(getHourlyLoadValue(springFallPeakLoadPercentageWknd));

        return hourlyLoad;
    }

    /**
     * Returns parsed percentage value of peak load in 0.xx - 1.00 format.
     */
    private Double getHourlyLoadValue(final String value) {
        return Double.parseDouble(value) / 100;
    }

    public List<Bus> getBuses() {
        return buses;
    }
}
