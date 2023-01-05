package com.company.parser;

import com.company.parser.model.HourlyLoad;
import com.company.parser.model.KseGenerator;
import com.company.parser.model.KseLine;
import com.company.parser.model.KseNode;
import com.company.parser.util.PowerNetworkUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KSEPowerNetworkParser {

    private List<String> powerNetworkNodesDataLines = new ArrayList<>();
    private List<String> powerNetworkLinesDataLines = new ArrayList<>();
    private List<String> powerNetworkGeneratorsDataLines = new ArrayList<>();
    private List<String> powerNetworkHourlyLoadDataLines = new ArrayList<>();
    private List<KseNode> nodes = new ArrayList<>();
    private List<KseLine> transmissionLines = new ArrayList<>();
    private List<KseGenerator> generators = new ArrayList<>();
    private List<HourlyLoad> hourlyLoads = new ArrayList<>();

    public KSEPowerNetworkParser() {
        // read all necessary data from files
        powerNetworkNodesDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_NODES_DATA);
        powerNetworkLinesDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_LINES_DATA);
        powerNetworkGeneratorsDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_GENERATORS_DATA);
        powerNetworkHourlyLoadDataLines = readDataLinesFromFile(PowerNetworkUtils.HOURLY_LOAD_DATA_10HOURS);

        // parse data loaded from files
        parseHourlyLoadDataLines();
        parseNodesDataLines();
        parseTransmissionLinesDataLines();
        parseGeneratorsDataLines();
    }

    public void parseMultiStageKSECase(final String directoryName, final String filename, boolean unconstrained, int lmpNode, double peak) {
        MultiStageKSEModelAmplWriter modelAmplWriter = new MultiStageKSEModelAmplWriter(unconstrained);
        modelAmplWriter.writeAmplFullMultiStageModel(directoryName, filename, nodes, transmissionLines, hourlyLoads, generators, lmpNode, peak);
    }

    private void parseNodesDataLines() {
        boolean nodesDataStarted = false;
        for (String line : powerNetworkNodesDataLines) {
            if (line.contains(PowerNetworkUtils.KSE_NODES_SECTION_START_INDICATOR) && !nodesDataStarted) {
                nodesDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.KSE_SECTION_END_INDICATOR) && nodesDataStarted) {
                nodesDataStarted = false;
            }
            if (nodesDataStarted) {
                final var node = parseNodeDataLine(line);
                nodes.add(node);
            }
        }
    }

    private KseNode parseNodeDataLine(final String line) {
        KseNode kseNode = new KseNode();

        final var nodeNumber = line.substring(0, 4).trim();
        kseNode.withNumber(Integer.parseInt(nodeNumber));
        final var nodeName = line.substring(8, 11).trim();
        kseNode.withName(nodeName);

        final var loadMin = line.substring(20, 30).trim();
        kseNode.withLoadMin(Double.parseDouble(loadMin));

        final var loadSr = line.substring(37, 47).trim();
        kseNode.withLoadSr(Double.parseDouble(loadSr));

        final var loadMax = line.substring(52, 62).trim();
        kseNode.withLoadMax(Double.parseDouble(loadMax));
        return kseNode;
    }

    private void parseTransmissionLinesDataLines() {
        boolean linesDataStarted = false;
        for (String line : powerNetworkLinesDataLines) {
            if (line.contains(PowerNetworkUtils.KSE_LINES_SECTION_START_INDICATOR) && !linesDataStarted) {
                linesDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.KSE_SECTION_END_INDICATOR) && linesDataStarted) {
                linesDataStarted = false;
            }
            if (linesDataStarted) {
                final var transmissionLine = parseTransmissionLineDataLine(line);
                transmissionLines.add(transmissionLine);
            }
        }
    }

    private KseLine parseTransmissionLineDataLine(final String line) {
        KseLine kseLine = new KseLine();

        final var fromNodeName = line.substring(0, 4).trim();
        kseLine.withFromNodeName(fromNodeName);
        final var toNodeName = line.substring(8, 12).trim();
        kseLine.withToNodeName(toNodeName);

        final var fromNodeNumber = line.substring(16, 20).trim();
        kseLine.withFromNodeNumber(Integer.parseInt(fromNodeNumber));
        final var toNodeNumber = line.substring(24, 28).trim();
        kseLine.withToNodeNumber(Integer.parseInt(toNodeNumber));

        final var lineCapacity = line.substring(32, 36).trim();
        kseLine.withLineCapacity(Integer.parseInt(lineCapacity));
        final var admittance = line.substring(44, 52).trim();
        kseLine.withAdmittance(Double.parseDouble(admittance));

        final var voltageSource = line.substring(60, 64).trim();
        kseLine.withVoltageSource(Integer.parseInt(voltageSource));
        final var voltageDestination = line.substring(76, 79).trim();
        kseLine.withVoltageDestination(Integer.parseInt(voltageDestination));

        return kseLine;
    }

    private void parseGeneratorsDataLines() {
        boolean generatorsDataStarted = false;
        for (String line : powerNetworkGeneratorsDataLines) {
            if (line.contains(PowerNetworkUtils.KSE_GENERATORS_SECTION_START_INDICATOR) && !generatorsDataStarted) {
                generatorsDataStarted = true;
                continue;
            }
            if (line.contains(PowerNetworkUtils.KSE_SECTION_END_INDICATOR) && generatorsDataStarted) {
                generatorsDataStarted = false;
            }
            if (generatorsDataStarted) {
                final var generator = parseGeneratorDataLine(line);
                generators.add(generator);
            }
        }
    }

    private KseGenerator parseGeneratorDataLine(final String line) {
        KseGenerator kseGenerator = new KseGenerator();

        final var generatorCompany = line.substring(0, 52).trim();
        kseGenerator.withGeneratorCompany(generatorCompany);
        final var powerPlantName = line.substring(52, 76).trim();
        kseGenerator.withPowerPlantName(powerPlantName);
        final var blockName = line.substring(76, 92).trim();
        kseGenerator.withBlockName(blockName);
        final var generationMin = line.substring(92, 104).trim();
        kseGenerator.withGenerationMin(generationMin.contains("-") ? 0 : Integer.parseInt(generationMin));
        final var generationMax = line.substring(104, 116).trim();
        kseGenerator.withGenerationMax(generationMax.contains("-") ? 0 : Integer.parseInt(generationMax));
        final var voltage = line.substring(116, 128).trim();
        kseGenerator.withVoltage(voltage.contains("-") ? 0 : Integer.parseInt(voltage));
        final var nodeName = line.substring(128, 136).trim();
        kseGenerator.withNodeName(nodeName);
        final var nodeNumber = line.substring(136, 139).trim();
        kseGenerator.withNodeNumber(nodeNumber.contains("-") ? 0 : Integer.parseInt(nodeNumber));
        return kseGenerator;
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

    private void parseHourlyLoadDataLines() {
        boolean hourlyLoadDataStarted = false;
        for (String line : powerNetworkHourlyLoadDataLines) {
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

    public List<KseNode> getNodes() {
        return nodes;
    }

    public void writeRunScripts(final String directoryName, final int size) {
        MultiStageKSEModelAmplWriter kseModelAmplWriter = new MultiStageKSEModelAmplWriter(true);
        kseModelAmplWriter.writeRunScriptForBatchToFile(directoryName, size);
        kseModelAmplWriter.writeSingleRunScriptToFile(directoryName, size);
    }
}
