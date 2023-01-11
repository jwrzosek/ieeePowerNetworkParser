package com.company.parser.util;

import java.util.HashMap;
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

    public static Map<String, Double> kseDemandHourly = new HashMap<>();
    static {
        kseDemandHourly.put("1_summer", 11365.100);
        kseDemandHourly.put("2_summer", 10553.063);
        kseDemandHourly.put("3_summer", 10455.313);
        kseDemandHourly.put("4_summer", 10601.050);
        kseDemandHourly.put("5_summer", 10581.663);
        kseDemandHourly.put("6_summer", 10910.725);
        kseDemandHourly.put("7_summer", 12626.613);
        kseDemandHourly.put("8_summer", 13802.588);
        kseDemandHourly.put("9_summer", 13465.450);
        kseDemandHourly.put("10_summer", 11867.075);
        kseDemandHourly.put("11_summer", 10657.075);
        kseDemandHourly.put("12_summer", 10456.100);
        kseDemandHourly.put("13_summer", 10617.938);
        kseDemandHourly.put("14_summer", 10283.013);
        kseDemandHourly.put("15_summer", 9981.100);
        kseDemandHourly.put("16_summer", 9997.438);
        kseDemandHourly.put("17_summer", 10160.950);
        kseDemandHourly.put("18_summer", 10644.050);
        kseDemandHourly.put("19_summer", 11466.913);
        kseDemandHourly.put("20_summer", 12650.075);
        kseDemandHourly.put("21_summer", 13481.738);
        kseDemandHourly.put("22_summer", 13300.688);
        kseDemandHourly.put("23_summer", 11865.238);
        kseDemandHourly.put("24_summer", 10821.013);

        kseDemandHourly.put("1_winter", 14164.800);
        kseDemandHourly.put("2_winter", 14231.338);
        kseDemandHourly.put("3_winter", 14528.713);
        kseDemandHourly.put("4_winter", 14463.525);
        kseDemandHourly.put("5_winter", 14543.588);
        kseDemandHourly.put("6_winter", 14798.638);
        kseDemandHourly.put("7_winter", 16885.375);
        kseDemandHourly.put("8_winter", 18913.238);
        kseDemandHourly.put("9_winter", 19634.825);
        kseDemandHourly.put("10_winter", 19900.475);
        kseDemandHourly.put("11_winter", 19795.700);
        kseDemandHourly.put("12_winter", 19975.313);
        kseDemandHourly.put("13_winter", 20123.650);
        kseDemandHourly.put("14_winter", 20189.963);
        kseDemandHourly.put("15_winter", 20035.988);
        kseDemandHourly.put("16_winter", 19742.963);
        kseDemandHourly.put("17_winter", 19942.538);
        kseDemandHourly.put("18_winter", 19571.038);
        kseDemandHourly.put("19_winter", 19079.238);
        kseDemandHourly.put("20_winter", 18925.263);
        kseDemandHourly.put("21_winter", 18229.113);
        kseDemandHourly.put("22_winter", 16909.513);
        kseDemandHourly.put("23_winter", 15692.025);
        kseDemandHourly.put("24_winter", 14331.775);
    }

    private PowerNetworkUtils() {
        // private constructor for Util class
    }
}
