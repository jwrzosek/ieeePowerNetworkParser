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

public class KSEModelParser {

    private List<String> powerNetworkNodesDataLines = new ArrayList<>();
    private List<String> powerNetworkLinesDataLines = new ArrayList<>();
    private List<String> powerNetworkGeneratorsDataLines = new ArrayList<>();
    private List<String> powerNetworkHourlyLoadDataLines = new ArrayList<>();
    private List<KseNode> nodes = new ArrayList<>();
    private List<KseLine> transmissionLines = new ArrayList<>();
    private List<KseGenerator> generators = new ArrayList<>();
    private List<HourlyLoad> hourlyLoads = new ArrayList<>();

    public KSEModelParser() {
        // read all necessary data from files
        powerNetworkNodesDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_NODES_DATA);
        powerNetworkLinesDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_LINES_DATA);
        powerNetworkGeneratorsDataLines = readDataLinesFromFile(PowerNetworkUtils.DIR_KSE_POWER_NETWORK_GENERATORS_DATA);
        powerNetworkHourlyLoadDataLines = readDataLinesFromFile(PowerNetworkUtils.HOURLY_LOAD_DATA_10HOURS);

        // parse data loaded from files
        parseHourlyLoadDataLines();

        parseNodesDataLines();
        parseTransmissionLinesDataLines();
        //parseGeneratorsDataLines();
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
        return null;
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
        return null;
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

}
