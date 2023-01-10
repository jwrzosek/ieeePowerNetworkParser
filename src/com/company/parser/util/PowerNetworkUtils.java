package com.company.parser.util;

import java.util.Map;

public class PowerNetworkUtils {

    public static final String MODEL_OUT_DIR = "resources/models/";

    public static final String MODEL_OUT_AMPL_DIR = "resources/models/";

    public static final String DIR_14_NODES = "resources/powernetworkdata/ieee14nodesnetwork.txt";
    public static final String DIR_30_NODES = "resources/powernetworkdata/ieee30nodesnetwork.txt";
    public static final String DIR_57_NODES = "resources/powernetworkdata/ieee57nodesnetwork.txt";
    public static final String DIR_118_NODES = "resources/powernetworkdata/ieee118nodesnetwork.txt";
    public static final String DIR_300_NODES = "resources/powernetworkdata/ieee300nodesnetwork.txt";

    public static final String DIR_KSE_POWER_NETWORK_NODES_DATA = "resources/powernetworkdata/kse_nodes.txt";
    public static final String DIR_KSE_POWER_NETWORK_LINES_DATA = "resources/powernetworkdata/kse_lines.txt";
    public static final String DIR_KSE_POWER_NETWORK_GENERATORS_DATA = "resources/powernetworkdata/kse_generators.txt";
    public static final String DIR_KSE_POWER_NETWORK_GENERATORS_WORKING_DATA = "resources/powernetworkdata/kse_generators_working.txt";
    public static final String DIR_KSE_POWER_NETWORK_GENERATORS_WORKING_WITH_COST_DATA = "resources/powernetworkdata/kse_generators_working_with_cost.txt";

    public static final String HOURLY_LOAD_DATA_24_HOURS = "resources/timeperioddata/hourlyLoad.txt";
    public static final String HOURLY_LOAD_DATA_10HOURS = "resources/timeperioddata/hourlyLoadTmp.txt";

    public static final String MULTI_STAGE_MODEL_DIR = "resources/models/model.mod";
    public static final String MULTI_STAGE_MIN_BALANCING_COST_MODEL_DIR = "resources/models/model_min_balancing_cost.mod";
    public static final String MULTI_STAGE_MIN_BALANCING_COST_MODEL_FOR_KSE_DIR = "resources/models/model_min_balancing_cost_for_kse.mod";
    public static final String MULTI_STAGE_COMMON_DATA_DIR = "resources/datamodels/common.dat";
    public static final String MULTI_STAGE_COMMON_KSE_DATA_DIR = "resources/datamodels/common_kse.dat";
    public static final String MULTI_STAGE_COMMON_KSE_EXTENDED_DATA_DIR = "resources/datamodels/common_kse_extended.dat";
    public static final String MULTI_STAGE_COMMON_KSE_EXTENDED_DATA_NO_LINE_LIMITS_DIR = "resources/datamodels/common_kse_no_line_limits.dat";
    public static final String MULTI_STAGE_COMMON_KSE_V2_DIR = "resources/datamodels/common_kse_v2.dat";
    public static final String MULTI_STAGE_COMMON_KSE_V2_UNCONSTRAINED_DIR = "resources/datamodels/common_kse_v2_unconstrained.dat";

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

    public static final String HOURLY_LOAD_DATA_START_INDICATOR = "---";

    public static final String KSE_NODES_SECTION_START_INDICATOR = "NODES DATA";
    public static final String KSE_LINES_SECTION_START_INDICATOR = "LINES DATA";
    public static final String KSE_GENERATORS_SECTION_START_INDICATOR = "GENERATORS DATA";
    public static final String KSE_SECTION_END_INDICATOR = "-999";


    /**
     * Demand Peaks for KSE power network for 12-07-2022 and 06-12-2022
     */
    private static final Double KSE_DEMAND_SUMMER_DOWN = 9981.1;
    private static final Double KSE_DEMAND_SUMMER_PEAK = 13802.588;
    private static final Double KSE_DEMAND_WINTER_DOWN = 14164.8;
    private static final Double KSE_DEMAND_WINTER_PEAK = 20189.963;
    public static final Map<String, Double> kseDemandPeaks = Map.of(
            "summerDown", KSE_DEMAND_SUMMER_DOWN,
            "summerPeak", KSE_DEMAND_SUMMER_PEAK,
            "winterDown", KSE_DEMAND_WINTER_DOWN,
            "winterPeak", KSE_DEMAND_WINTER_PEAK
    );

    private PowerNetworkUtils() {
        // private constructor for Util class
    }
}
