package com.company.parser.util;

public class AmplUtils {

    //public static final String DIRECTORY_PATH = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\intellij";
    public static final String DIRECTORY_PATH = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\30nodes";
    public static final String DIRECTORY_PATH_KSE = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse";

    public static final boolean DC_MODEL_ON = false;

    public static final String BUS_SYMBOL = "B";
    public static final String BUS_NAME = "BUSES";

    public static final String GENERATOR_SYMBOL = "J";
    public static final String GENERATOR_NAME = "GENS";

    public static final String TIME_PERIOD_SYMBOL = "H";
    public static final String TIME_PERIOD_NAME = "HOURS";

    public static final String DEFAULT_SET_SEPARATOR = " ";
    public static final String DEFAULT_PARAM_SEPARATOR = "\t";
    public static final String DEFAULT_PARAM_END_SEPARATOR = "\t;\n\n";
    public static final String DEFAULT_PARAM_EQUALS_SIGN = " :=\n";
    public static final String PARAM_FORMAT = "%-12s";
    public static final String PARAM_BEGIN = String.format("%-4s", " ");

    public static final String INFO_SECTION_DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String PARAM_SYMBOL = "param";
    public static final String PARAM_ADMITTANCE_SYMBOL = "Y_ab";
    public static final String PARAM_LINE_CAPACITY_SYMBOL = "Q_ab";
    public static final String PARAM_PGEN_MAX_SYMBOL = "p_jhMax";
    public static final String PARAM_PLOAD_SYMBOL = "Pa_load";
    public static final boolean UNCONSTRAINED = false;

    private AmplUtils() {
        // private constructor for Util class
    }
}
