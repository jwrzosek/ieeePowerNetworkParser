package com.company.parser.util;

public class AmplUtils {

    public static final String DIRECTORY_PATH = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\test";
    //public static final String DIRECTORY_PATH_KSE = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse";
    //public static final String DIRECTORY_PATH_KSE = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_v2";
    public static final String DIRECTORY_PATH_KSE = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_v2";
    //todo:delete after testing
    public static final String DIRECTORY_PATH_KSE_TEMP = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_temp";
    public static final String DIRECTORY_PATH_TEMP = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\temp";

    public static final boolean DC_MODEL_ON = false;

    public static final String BUS_SYMBOL = "B";
    public static final String BUS_NAME = "BUSES";

    public static final String GENERATOR_SYMBOL = "J";
    public static final String GENERATOR_NAME = "GENERATORS";

    public static final String TIME_PERIOD_SYMBOL = "H";
    public static final String TIME_PERIOD_NAME = "HOURS";

    public static final String DEFAULT_SET_SEPARATOR = " ";
    public static final String DEFAULT_PARAM_SEPARATOR = "\t";
    public static final String DEFAULT_PARAM_END_SEPARATOR = String.format("%-4s", " ") + ";\n\n";
    public static final String DEFAULT_PARAM_EQUALS_SIGN = " :=\n";
    public static final String PARAM_FORMAT = "%-12s";
    public static final String PARAM_BEGIN = String.format("%-4s", " ");

    public static final String INFO_SECTION_DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String PARAM_SYMBOL = "param";
    public static final String PARAM_ADMITTANCE_SYMBOL = "Y_ab";
    public static final String PARAM_LINE_CAPACITY_SYMBOL = "Q_ab";
    public static final String PARAM_PGEN_MIN_SYMBOL = "p_ajhMin";
    public static final String PARAM_PGEN_MAX_SYMBOL = "p_ajhMax";
    public static final String PARAM_PLOAD_SYMBOL = "Pa_load";
    public static final String PARAM_VOLTAGE_SYMBOL = "V";
    public static final String PARAM_VARIABLE_COST_SYMBOL = "Ka_gen";
    public static final boolean UNCONSTRAINED = false;

    private AmplUtils() {
        // private constructor for Util class
    }
}
