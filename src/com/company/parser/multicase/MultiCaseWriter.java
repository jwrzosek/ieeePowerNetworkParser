package com.company.parser.multicase;

import com.company.parser.IEEEPowerNetworkParser;
import com.company.parser.util.AmplUtils;
import com.company.parser.util.PowerNetworkUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class MultiCaseWriter {

    public MultiCaseWriter() {
    }

    public void writeMultipleCasesLMP() {
        IEEEPowerNetworkParser unconstrainedParser = new IEEEPowerNetworkParser();
        final var size = unconstrainedParser.getBuses().size();
        final var directoryName = "30nodes_60";
        createMultiCaseDirectory(directoryName);
        //write model to a directory
        writeModelFile(directoryName);
        unconstrainedParser.writeCommonData(directoryName);
        unconstrainedParser.writeRunScript(directoryName, size);
        // wygeneruj plik z danymi niezależnymi od modelu
        // wygeneruj normalny model bez ograniczeń
        unconstrainedParser.parseMultiStageCase(directoryName,"unconstrained.dat", true, 1000);
        // wygeneruj model z ograniczeniami
        IEEEPowerNetworkParser balancedParser = new IEEEPowerNetworkParser();
        balancedParser.parseMultiStageCase(directoryName, "balanced.dat", false, 1000);
        // wygeneruj 1..N modeli dla każdego węzła

        for (int i = 0; i<size; i++) {
            IEEEPowerNetworkParser parser = new IEEEPowerNetworkParser();
            parser.parseMultiStageCase(directoryName, (i+1 + ".dat"), false, i);
        }
    }

    private void createMultiCaseDirectory(String dirName) {
        File multiCaseDirectory = new File(AmplUtils.DIRECTORY_PATH + "\\" + dirName);
        if (multiCaseDirectory.exists()) {
            System.out.println("Directory already exists");
            return;
        }
        if (!multiCaseDirectory.mkdir()) {
            System.out.println("Directory already exists");
        }
    }


    private void writeModelFile(final String directory) {
        final var origin = Path.of(PowerNetworkUtils.MULTI_STAGE_MODEL_DIR);
        final var destination = Paths.get(AmplUtils.DIRECTORY_PATH, directory, "model.mod");
        try {
            final List<String> modelData = Files.readAllLines(origin);
            final var modelString = String.join("\n", modelData);
            Files.deleteIfExists(destination);
            Files.writeString(destination, modelString, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
