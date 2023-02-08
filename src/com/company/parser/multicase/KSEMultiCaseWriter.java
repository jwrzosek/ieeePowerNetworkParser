package com.company.parser.multicase;

import com.company.parser.KSEPowerNetworkParser;
import com.company.parser.model.HourlyLoad;
import com.company.parser.util.AmplUtils;
import com.company.parser.util.PowerNetworkUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class KSEMultiCaseWriter {

    private List<String> hourlyLoadDataLines = new ArrayList<>();
    private List<HourlyLoad> hourlyLoads = new ArrayList<>();

    public KSEMultiCaseWriter() {
        readHourlyLoadDataFile(PowerNetworkUtils.HOURLY_LOAD_DATA_24_HOURS);
        parseHourlyLoadDataLines();
    }

    public void runWithKSEDemandPeaks() {
        PowerNetworkUtils.kseDemandPeaks.keySet()
                .forEach(key -> {
                    final var peakDemand = PowerNetworkUtils.kseDemandPeaks.get(key);
                    final var directoryName = "kse_" + key;
                    writeMultipleCasesLMPWithHourlyLoadPeak(directoryName, peakDemand);
                }
        );
        writeRunScriptForMultiCaseScenarioWithDemandPeaks();
    }

    private void writeMultipleCasesLMPWithHourlyLoadPeak(final String directoryName, double demandPeak) {
        // create directory for a lmp case
        createMultiCaseDirectory(directoryName);

        //write model to a directory
        writeModelFile(directoryName);

        // write common data files
        writeCommonDataFile(directoryName);
        writeCommonDataFileUnconstrained(directoryName);

        // generate model without constraints
        KSEPowerNetworkParser unconstrainedParser = new KSEPowerNetworkParser();
        final var size = unconstrainedParser.getNodes().size();
        unconstrainedParser.writeRunScripts(directoryName, size);
        unconstrainedParser.parseMultiStageKSECase(directoryName, AmplUtils.UNCONSTRAINED_DAT_FILE, true, -1, demandPeak);

        // generate model with constraints
        KSEPowerNetworkParser balancedParser = new KSEPowerNetworkParser();
        balancedParser.parseMultiStageKSECase(directoryName, AmplUtils.BALANCED_DAT_FILE, false, -1, demandPeak);

        // generate 1..N models for each node
        for (int i = 0; i<size; i++) {
            KSEPowerNetworkParser parser = new KSEPowerNetworkParser();
            parser.parseMultiStageKSECase(directoryName, (i+1 + ".dat"), false, i, demandPeak);
        }
    }

    private void writeRunScriptForMultiCaseScenarioWithDemandPeaks() {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE,  AmplUtils.ALL_RUN_FILE);
        final var runScript = createRunAllScriptForDemandPeaks();
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createRunAllScriptForDemandPeaks() {
        StringBuilder sb = new StringBuilder();
        PowerNetworkUtils.kseDemandPeaks
                .keySet()
                .forEach(key -> sb.append("include 'kse_").append(key).append("/batch.run'\n"));
        return sb.toString();
    }

    private void readHourlyLoadDataFile(final String directory) {
        Path path = Paths.get(directory);
        try {
            hourlyLoadDataLines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMultiCaseDirectory(String dirName) {
        File multiCaseDirectory = new File(AmplUtils.DIRECTORY_PATH_KSE + "\\" + dirName);
        if (multiCaseDirectory.exists()) {
            System.out.println("Directory already exists");
            return;
        }
        if (!multiCaseDirectory.mkdir()) {
            System.out.println("Directory already exists");
        }
    }

    private void writeModelFile(final String directory) {
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_MIN_BALANCING_COST_MODEL_FOR_KSE_DIR);
        final var destination = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directory, "model_min_balancing_cost_for_kse.mod");
        try {
            final List<String> modelData = Files.readAllLines(origin);
            final var modelString = String.join("\n", modelData);
            Files.deleteIfExists(destination);
            Files.writeString(destination, modelString, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCommonDataFile(final String directory) {
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_COMMON_KSE_V2_DIR);
        final var destination = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directory, "common.dat");
        try {
            final List<String> modelData = Files.readAllLines(origin);
            final var modelString = String.join("\n", modelData);
            Files.deleteIfExists(destination);
            Files.writeString(destination, modelString, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCommonDataFileUnconstrained(final String directory) {
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_COMMON_KSE_V2_UNCONSTRAINED_DIR);
        final var destination = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directory, "commonUnconstrained.dat");
        try {
            final List<String> modelData = Files.readAllLines(origin);
            final var modelString = String.join("\n", modelData);
            Files.deleteIfExists(destination);
            Files.writeString(destination, modelString, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private Double getHourlyLoadValue(final String value) {
        return Double.parseDouble(value) / 100;
    }

    @Deprecated
    public void runWithHourlyLoads() {
        hourlyLoads.forEach(hourlyLoad -> {
            final var peakPerc = hourlyLoad.getSummerPeakLoadPercentageWkdy();
            final var directoryName = "kse_" + hourlyLoad.getPeriod();
            writeMultipleCasesLMPWithHourlyLoadPeak(directoryName, peakPerc);
        });
        writeRunScriptForMultiCaseScenarioWithHourlyLoads();
    }

    @Deprecated
    private void writeRunScriptForMultiCaseScenarioWithHourlyLoads() {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE,  "all.run");
        final var runScript = createRunAllScriptForHourlyLoads();
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private String createRunAllScriptForHourlyLoads() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<hourlyLoads.size(); i++) {
            sb.append("include 'kse_" + i + "-" + (i+1) + "/batch.run'\n");
        }
        return sb.toString();
    }
}
