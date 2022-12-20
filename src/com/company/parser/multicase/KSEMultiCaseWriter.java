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

    public void runWithHourlyLoads() {
        hourlyLoads.forEach(hourlyLoad -> {
            final var peakPerc = hourlyLoad.getSummerPeakLoadPercentageWkdy();
            final var directoryName = "kse_" + hourlyLoad.getPeriod();
            writeMultipleCasesLMP(directoryName, peakPerc);
        });
        writeRunScriptForMultiCaseScenario();
    }

    private void writeMultipleCasesLMP(final String directoryName, double peak) {
        // create directory for a lmp case
        createMultiCaseDirectory(directoryName);

        //write model to a directory
        writeModelFile(directoryName);

        // write common data to one file
        writeCommonDataFile(directoryName);

        KSEPowerNetworkParser unconstrainedParser = new KSEPowerNetworkParser();
        final var size = unconstrainedParser.getNodes().size();
        unconstrainedParser.writeRunScripts(directoryName, size);
        // wygeneruj plik z danymi niezależnymi od modelu
        // wygeneruj normalny model bez ograniczeń
        unconstrainedParser.parseMultiStageKSECase(directoryName,"unconstrained.dat", true, -1, peak);
        // wygeneruj model z ograniczeniami
        KSEPowerNetworkParser balancedParser = new KSEPowerNetworkParser();
        balancedParser.parseMultiStageKSECase(directoryName, "balanced.dat", false, -1, peak);
        // wygeneruj 1..N modeli dla każdego węzła

        for (int i = 0; i<size; i++) {
            KSEPowerNetworkParser parser = new KSEPowerNetworkParser();
            parser.parseMultiStageKSECase(directoryName, (i+1 + ".dat"), false, i, peak);
        }
    }

    private void writeRunScriptForMultiCaseScenario() {
        final var path = Paths.get(AmplUtils.DIRECTORY_PATH_KSE,  "all.run");
        final var runScript = createRunAllScript();
        try {
            Files.deleteIfExists(path);
            Files.writeString(path, runScript, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createRunAllScript() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<hourlyLoads.size(); i++) {
            sb.append("include 'kse_" + i + "-" + (i+1) + "/batch.run'\n");
        }
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
        // todo: maybe change the model source
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_MIN_BALANCING_COST_MODEL_DIR);
        final var destination = Paths.get(AmplUtils.DIRECTORY_PATH_KSE, directory, "model_min_balancing_cost.mod");
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
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_COMMON_KSE_EXTENDED_DATA_NO_LINE_LIMITS_DIR);
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

    private Double getHourlyLoadValue(final String value) {
        return Double.parseDouble(value) / 100;
    }
}
