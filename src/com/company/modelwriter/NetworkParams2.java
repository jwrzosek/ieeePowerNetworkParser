package com.company.modelwriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkParams2 {

    private static final String DEFAULT_SET_SEPARATOR = " ";
    private static final String DEFAULT_PARAM_SEPARATOR = "\t";

    private static final int MAX_LINE_LIMIT = 150;
    private static final int MIN_LINE_LIMIT = 0;

    private static final int MAX_DEMAND = 200;
    private static final int MIN_DEMAND = 10;

    private static final int MIN_SELL_PRICE = 20;
    private static final int MAX_SELL_PRICE = 300;

    private static final int MAX_PRODUCTION_LIMIT = 300;
    private static final int MIN_PRODUCTION_LIMIT = 0;

    private static final List<String> parametersNames = List.of("offer_price", "sell_price", "demand", "production_limit");
    private static final Map<String, String> PARAMETERS_SPACE = new HashMap<>();
    static {
        for (var name : parametersNames) {
            var format = "%-" + (name.length() + 4) + "s";
            PARAMETERS_SPACE.put(name, format);
        }
    }

    private NetworkParams2() {
    }

    public static String writeSet(String name, int quantity, String symbol) {
        StringBuilder sb = new StringBuilder("set " + name + " :=");
        for (int i = 0; i < quantity; i++) {
            sb.append(DEFAULT_SET_SEPARATOR).append(symbol).append(i + 1);
        }
        sb.append(";\n");
        return sb.toString();
    }

    public static String writeParameter(String name, int quantity, String symbol) {
        StringBuilder sb = new StringBuilder("param " + name + " :=\n");
        for (int i = 0; i < quantity; i++) {
            final var lineLimit = getRandomInteger(MIN_LINE_LIMIT, MAX_LINE_LIMIT);
            sb.append(DEFAULT_PARAM_SEPARATOR).append(symbol).append(i + 1).append("\t\t")
                    .append(String.format("%-8s", Math.max(lineLimit, 10)))
                    .append("\n");
        }
        sb.append("\t;\n");
        return sb.toString();
    }

    public static String writeNodesParameters(int nodesQuantity) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateParameterNamesLine()).append(":=\n");
        for (int i = 0; i < nodesQuantity; i++) {
            // append node name
            sb.append("\tN").append(i + 1).append("\t\t");
            // append sell price value
            var sellPrice = getRandomInteger(MIN_SELL_PRICE, MAX_SELL_PRICE);
            sb.append(String.format(PARAMETERS_SPACE.get("offer_price"), sellPrice+20   ));
            // append offer price value
            sb.append(String.format(PARAMETERS_SPACE.get("sell_price"), sellPrice));
            // append demand value
            sb.append(String.format(PARAMETERS_SPACE.get("demand"), getRandomInteger(MIN_DEMAND, MAX_DEMAND)));
            // append production limit value
            sb.append(String.format(PARAMETERS_SPACE.get("production_limit"), getRandomInteger(MIN_PRODUCTION_LIMIT, MAX_PRODUCTION_LIMIT)));

            sb.append("\n");
        }
        sb.append("\t;\n");
        return sb.toString();
    }

    private static String generateParameterNamesLine() {
        StringBuilder sb = new StringBuilder("param:\t\t");
        for (var name : parametersNames) {
            sb.append(String.format(PARAMETERS_SPACE.get(name), name));
        }
        return sb.toString();
    }


    private static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
