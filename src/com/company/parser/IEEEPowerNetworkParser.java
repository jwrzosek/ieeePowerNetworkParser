package com.company.parser;

import com.company.parser.model.Generator;
import com.company.parser.model.HourlyLoad;
import com.company.parser.model.Line;
import com.company.parser.model.Node;
import com.company.parser.util.PowerNetworkUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IEEEPowerNetworkParser {

    private List<String> powerNetworkDataLines = new ArrayList<>();
    private List<String> hourlyLoadDataLines = new ArrayList<>();

    private List<Node> transmissionNodes = new ArrayList<>();
    private List<Line> nodeLines = new ArrayList<>();
    private List<HourlyLoad> hourlyLoads = new ArrayList<>();

    public IEEEPowerNetworkParser() {
        //readDataFile(PowerNetworkUtils.DIR_30_NODES);
        powerNetworkDataLines = readDataLinesFromFile(PowerNetworkUtils.POWER_TEST_NETWORK_DATA_SOURCE);

        // parsing hourly load data file
        hourlyLoadDataLines = readDataLinesFromFile(PowerNetworkUtils.HOURLY_LOAD_DATA_TEMP);

        parseHourlyLoadDataLines();
        parseNodesDataLines();
        parseLinesDataLines();
    }

    public void parseMultiStageCase(final String directoryName, final String filename, boolean unconstrained, int lmpNode, double peak) {
        MultiStageModelAmplWriter modelAmplWriter = new MultiStageModelAmplWriter(unconstrained);
        modelAmplWriter.writeAmplFullMultiStageModel(directoryName, filename, transmissionNodes, nodeLines, hourlyLoads, getNumberOfGenerators(), lmpNode, peak);
    }

    public void writeRunScripts(final String directory, final int size) {
        MultiStageModelAmplWriter modelAmplWriter = new MultiStageModelAmplWriter(true);
        modelAmplWriter.writeRunScriptForBatchToFile(directory, size);
        modelAmplWriter.writeSingleRunScriptToFile(directory, size);
    }

    private List<String> readDataLinesFromFile(final String directory) {
        Path path = Paths.get(directory);
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    private long getNumberOfGenerators() {
        return transmissionNodes.stream()
                .map(Node::getGenerators)
                .flatMap(Collection::stream)
                .map(Generator::getGenerationMW)
                .filter(generation -> generation > 0.01)
                .count();
    }

    private void parseLinesDataLines() {
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
                final var branch = parseLineDataLine(line);
                nodeLines.add(branch);
            }
        }
    }

    private Line parseLineDataLine(final String line) {
        Line nodeLine = new Line();

        final var tapBusNumber = line.substring(0, 4).trim();
        nodeLine.withTapBusNumber(Integer.parseInt(tapBusNumber));
        final var zBusNumber = line.substring(5, 9).trim();
        nodeLine.withzBusNumber(Integer.parseInt(zBusNumber));
        final var loadFlowArea = line.substring(10, 12).trim();
        nodeLine.withLoadFlowArea(Integer.parseInt(loadFlowArea));
        final var lossZone = line.substring(13, 15).trim();
        nodeLine.withLossZone(Integer.parseInt(lossZone));
        final var circuit = line.substring(16, 17).trim();
        nodeLine.withCircuit(Integer.parseInt(circuit));
        final var type = line.substring(18, 19).trim();
        nodeLine.withType(Integer.parseInt(type));
        final var branchResistanceR = line.substring(19, 28).trim();
        nodeLine.withBranchResistanceR(Double.parseDouble(branchResistanceR));
        final var branchReactanceX = line.substring(28, 39).trim();
        nodeLine.withBranchReactanceX(Double.parseDouble(branchReactanceX));
        final var lineChargingB = line.substring(39, 49).trim();
        nodeLine.withLineChargingB(Double.parseDouble(lineChargingB));
        final var lineMVARatingNo1 = line.substring(50, 55).trim();
        nodeLine.withLineMVARatingNo1(Integer.parseInt(lineMVARatingNo1));
        final var lineMVARatingNo2 = line.substring(56, 61).trim();
        nodeLine.withLineMVARatingNo2(Integer.parseInt(lineMVARatingNo2));
        final var lineMVARatingNo3 = line.substring(62, 67).trim();
        nodeLine.withLineMVARatingNo3(Integer.parseInt(lineMVARatingNo3));
        final var controlBusNumber = line.substring(69, 72).trim();
        nodeLine.withControlBusNubmer(Integer.parseInt(controlBusNumber));
        final var side = line.substring(73, 74).trim();
        nodeLine.withSide(Integer.parseInt(side));
        final var transformerFinalTurnsRatio = line.substring(76, 81).trim();
        nodeLine.withTransformerFinalTurnsRatio(Double.parseDouble(transformerFinalTurnsRatio));
        final var transformerPhaseShifterFinalAngle = line.substring(83, 89).trim();
        nodeLine.withTransformerPhaseShifterFinalAngle(Double.parseDouble(transformerPhaseShifterFinalAngle));
        final var minimumTapOrPhaseShift = line.substring(89, 96).trim();
        nodeLine.withMinimumTapOrPhaseShift(Double.parseDouble(minimumTapOrPhaseShift));
        final var maximumTapOrPhaseShift = line.substring(96, 103);
        nodeLine.withMaximumTapOrPhaseShift(Double.parseDouble(maximumTapOrPhaseShift));
        final var stepSize = line.substring(104, 110).trim();
        nodeLine.withStepSize(Double.parseDouble(stepSize));
        final var minimumVoltageMVARorMWLimit = line.substring(111, 118).trim();
        nodeLine.withMinimumVoltageMVARorMWLimit(Double.parseDouble(minimumVoltageMVARorMWLimit));
        final var maximumVoltageMVARorMWLimit = line.substring(118, 121).trim();
        nodeLine.withMaximumVoltageMVARorMWLimit(Double.parseDouble(maximumVoltageMVARorMWLimit));
        final var admittance = getAdmittanceValue(nodeLine, false);
        nodeLine.withAdmittance(admittance);
        return nodeLine;
    }

    private Double getAdmittanceValue(Line nodeLine, boolean dc) {
        return dc ? calculateAdmittanceDC(nodeLine) : calculateAdmittanceAC(nodeLine);
    }

    private Double calculateAdmittanceDC(Line nodeLine) {
        Double r = nodeLine.getBranchResistanceR();
        return 1/r;
    }

    private Double calculateAdmittanceAC(Line nodeLine) {
        Double r = nodeLine.getBranchResistanceR();
        Double x = nodeLine.getBranchReactanceX();
        Double conductanceG = r / ((r * r) + (x * x));
        Double susceptanceOm = x / ((r * r) + (x * x));
        return Math.sqrt((conductanceG * conductanceG) + (susceptanceOm * susceptanceOm));
    }

    private void parseNodesDataLines() {
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
                transmissionNodes.add(bus);
            }
        }
    }

    private Node parseBusDataLine(final String busDataLine) {
        Node node = new Node();
        final var busNumber = busDataLine.substring(0, 4).trim();
        node.withBusNumber(Integer.parseInt(busNumber));
        final var busName = busDataLine.substring(5, 17).trim();
        node.withBusName(busName);
        final var loadFlowAreaNumber = busDataLine.substring(18, 20).trim();
        node.withLoadFlowAreaNumber(Integer.parseInt(loadFlowAreaNumber));
        final var lossZoneNumber = busDataLine.substring(20, 23).trim();
        node.withLossZoneNumber(Integer.parseInt(lossZoneNumber));
        final var type = busDataLine.substring(24, 26).trim();
        node.withType(Integer.parseInt(type));
        final var finalVoltage = busDataLine.substring(27, 33).trim();
        node.withFinalVoltage(Double.parseDouble(finalVoltage));
        final var finalAngle = busDataLine.substring(33, 40).trim();
        node.withFinalAngle(Double.parseDouble(finalAngle));
        final var loadMW = busDataLine.substring(40, 49).trim();
        node.withLoadMW(Double.parseDouble(loadMW));
        final var loadMVAR = busDataLine.substring(49, 59).trim();
        node.withLoadMVAR(Double.parseDouble(loadMVAR));
        final var generationMW = busDataLine.substring(59, 67).trim();
//        if (node.getBusNumber() == 1) {
//            node.withGenerator(new Generator()
//                    .withBusNumber(Integer.parseInt(busNumber))
//                    .withGenerationMW(40.0)
//                    .withVariableCost(310)
//            );
//            node.withGenerator(new Generator()
//                    .withBusNumber(Integer.parseInt(busNumber))
//                    .withGenerationMW(170.0)
//                    .withVariableCost(612)
//            );
//        } else {
//            node.withGenerator(new Generator()
//                    .withBusNumber(Integer.parseInt(busNumber))
//                    .withGenerationMW(Double.parseDouble(generationMW))
//                    .withVariableCost(getRandomInteger(300, 900))
//            );
//        }

        node.withGenerator(new Generator()
                .withBusNumber(Integer.parseInt(busNumber))
                .withGenerationMW(Double.parseDouble(generationMW))
                .withVariableCost(getRandomInteger(300, 900))
        );
        final var generationMVAR = busDataLine.substring(67, 75).trim();
        node.withGenerationMVAR(Double.parseDouble(generationMVAR));
        final var baseKV = busDataLine.substring(76, 84).trim();
        node.withBaseKV(Double.parseDouble(baseKV));
        final var desiredVolts = busDataLine.substring(84, 90).trim();
        node.withDesiredVolts(Double.parseDouble(desiredVolts));
        final var maximumMVARorVoltageLimit = busDataLine.substring(90, 98).trim();
        node.withMaximumMVARorVoltageLimit(Double.parseDouble(maximumMVARorVoltageLimit));
        final var minimumMVARorVoltageLimit = busDataLine.substring(98, 106).trim();
        node.withMinimumMVARorVoltageLimit(Double.parseDouble(minimumMVARorVoltageLimit));
        final var shuntConductanceG = busDataLine.substring(106, 114).trim();
        node.withShuntConductanceG(Double.parseDouble(shuntConductanceG));
        final var shuntSusceptanceB = busDataLine.substring(114, 122).trim();
        node.withShuntSusceptanceB(Double.parseDouble(shuntSusceptanceB));
        final var remoteControlledBusNumber = busDataLine.substring(123, 127).trim();
        node.withRemoteControlledBusNumber(Integer.parseInt(remoteControlledBusNumber));

        return node;
    }

    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
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

        final var hour = period.split("-")[1];
        hourlyLoad.withHour(Integer.parseInt(hour));

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

    public List<Node> getTransmissionNodes() {
        return transmissionNodes;
    }

    public List<HourlyLoad> getHourlyLoads() {
        return hourlyLoads;
    }
}
