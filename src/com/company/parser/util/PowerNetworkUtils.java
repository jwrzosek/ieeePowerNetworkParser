package com.company.parser.util;

public class PowerNetworkUtils {

    public static final String MODEL_OUT_DIR = "resources/models/";

    public static final String MODEL_OUT_AMPL_DIR = "resources/models/";

    public static final String DIR_14_NODES = "resources/ieee14nodesnetwork.txt";
    public static final String DIR_30_NODES = "resources/ieee30nodesnetwork.txt";
    public static final String DIR_57_NODES = "resources/ieee57nodesnetwork.txt";
    public static final String DIR_118_NODES = "resources/ieee118nodesnetwork.txt";
    public static final String DIR_300_NODES = "resources/ieee300nodesnetwork.txt";

    /**
     * End of data section indicator in IEEE Power Network Common Data Format
     */
    public static final String CDF_SECTION_END_INDICATOR = "-999";
    /**
     * Start of BUS data section indicator in IEEE Power Network Common Data Format
     */
    public static final String CDF_BUS_SECTION_START_INDICATOR = "BUS DATA";
    /**
     * Start of BRANCH data section indicator in IEEE Power Network Common Data Format
     */
    public static final String CDF_BRANCH_SECTION_START_INDICATOR = "BRANCH DATA";

    private PowerNetworkUtils() {
        // private constructor for Util class
    }
}
