package com.company.modelwriter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PowerTransferDistributionFactors {

    public static StringBuilder stringBuilder = new StringBuilder();

    public static String generatePTDFs(int linesQuantity, int nodesQuantity) {
        StringBuilder sb = new StringBuilder("param wnl:\n\t\t\t");
        for (int i = 0; i < linesQuantity; i++) {
            sb.append(String.format("%-8s", "L" + (i+1)));
        }
        sb.append(":=\n");
        for (int i = 0; i < nodesQuantity; i++) {
            sb.append("\tN").append(i + 1).append("\t\t");
            List<BigDecimal> values = generatePTDFValues(linesQuantity);
//            stringBuilder.append("G.add_node(\"N").append(i + 1).append("\")\n");
            for (int j = 0; j < linesQuantity; j++) {
                if (values.get(j).compareTo(BigDecimal.ZERO) > 0) {
                    stringBuilder.append("G.add_edge(").append(i+1).append(", ").append(j+1 == linesQuantity ? 1 : j+1).append(")\n");
                }
                sb.append(String.format("%-8s", values.get(j)));
            }
            sb.append("\t# ").append(values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)).append("\n");
            values.clear();
        }
        sb.append("\t;\n");
        return sb.toString();
    }

    private static List<BigDecimal> generatePTDFValues(int numberOfValues) {
        ArrayList<Integer> values = new ArrayList<>();
        int limit = 1000;
        for (int i=0; i<numberOfValues; i++) {
            int sum = values.stream().reduce(0, Integer::sum);
            int value;
            if ((i+1)==numberOfValues) {
                value = limit-sum;
                values.add(value);
            } else {
                value = getRandomInteger(0, limit-sum);
                values.add(value > 400 || value < 100 ? 0 : value);
            }
        }
        Collections.shuffle(values);
        return values.stream()
                .map(v -> BigDecimal.valueOf(v/1000.0d).setScale(3))
                .collect(Collectors.toList());
    }

    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
