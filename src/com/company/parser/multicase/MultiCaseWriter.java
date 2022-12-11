package com.company.parser.multicase;

import com.company.parser.IEEEPowerNetworkParser;
import com.company.parser.util.AmplUtils;

import java.io.File;

public class MultiCaseWriter {

    public MultiCaseWriter() {
    }

    public void writeMultipleCasesLMP() {
        IEEEPowerNetworkParser unconstrainedParser = new IEEEPowerNetworkParser();
        final var size = unconstrainedParser.getBuses().size();
        final var directoryName = "30nodes_55";
        createMultiCaseDirectory(directoryName);
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


    private void createBatchRunScript() {

    }
}
