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
    public static final String HOURLY_LOAD_DATA_TEMP = "resources/timeperioddata/hourlyLoadTmp.txt";
    // todo: theoretically same as DATA_TEMP
    public static final String HOURLY_LOAD_DATA_10HOURS = "resources/timeperioddata/hourlyLoad10.txt";


    public static final String MULTI_STAGE_MODEL_DIR = "resources/models/model.mod";
    public static final String MULTI_STAGE_MIN_BALANCING_COST_MODEL_DIR = "resources/models/model_min_balancing_cost.mod";
    public static final String MULTI_STAGE_MIN_BALANCING_COST_MODEL_FOR_KSE_DIR = "resources/models/model_min_balancing_cost_for_kse.mod";
    public static final String MULTI_STAGE_COMMON_DATA_DIR = "resources/datamodels/common.dat";
    public static final String MULTI_STAGE_COMMON_DATA_UNCONSTRAINED_DIR = "resources/datamodels/common_unconstrained.dat";
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
    // for kse:
//    public static final String AMPL_MODEL_NAME = "model_min_balancing_cost_for_kse.mod";
    // for test power networks
    public static final String AMPL_MODEL_NAME = "model_min_balancing_cost.mod";
    public static final String POWER_TEST_NETWORK_DATA_SOURCE ="resources/powernetworkdata/ieee39matpower.txt";
    public static final String POWER_TEST_NETWORK_DIR = "_39nodes_winter";
    public static final boolean IS_SUMMER = false;
    public static final Double POWER_SUMMER_PERCENTAGE = 0.683636122;
    public static final Double POWER_WINTER_PERCENTAGE = 1.462766475;
    public static final Double P_MIN_PERCENTAGE = 0.55;
    public static final Map<String, Double> kseDemandPeaks = new HashMap<>();

    static {
        // for test power networks:
        kseDemandPeaks.put("1" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("2" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("3" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("4" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("5" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("6" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("7" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("8" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("9" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("10" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("11" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("12" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("13" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("14" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("15" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("16" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("17" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("18" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("19" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("20" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("21" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("22" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("23" + POWER_TEST_NETWORK_DIR, 1000000000.0);
        kseDemandPeaks.put("24" + POWER_TEST_NETWORK_DIR, 1000000000.0);

//        kseDemandPeaks.put("1_39nodes", 1000000000.0);
//        kseDemandPeaks.put("2_39nodes", 1000000000.0);
//        kseDemandPeaks.put("3_39nodes", 1000000000.0);
//        kseDemandPeaks.put("4_39nodes", 1000000000.0);
//        kseDemandPeaks.put("5_39nodes", 1000000000.0);
//        kseDemandPeaks.put("6_39nodes", 1000000000.0);
//        kseDemandPeaks.put("7_39nodes", 1000000000.0);
//        kseDemandPeaks.put("8_39nodes", 1000000000.0);
//        kseDemandPeaks.put("9_39nodes", 1000000000.0);
//        kseDemandPeaks.put("10_39nodes", 1000000000.0);
//        kseDemandPeaks.put("11_39nodes", 1000000000.0);
//        kseDemandPeaks.put("12_39nodes", 1000000000.0);
//        kseDemandPeaks.put("13_39nodes", 1000000000.0);
//        kseDemandPeaks.put("14_39nodes", 1000000000.0);
//        kseDemandPeaks.put("15_39nodes", 1000000000.0);
//        kseDemandPeaks.put("16_39nodes", 1000000000.0);
//        kseDemandPeaks.put("17_39nodes", 1000000000.0);
//        kseDemandPeaks.put("18_39nodes", 1000000000.0);
//        kseDemandPeaks.put("19_39nodes", 1000000000.0);
//        kseDemandPeaks.put("20_39nodes", 1000000000.0);
//        kseDemandPeaks.put("21_39nodes", 1000000000.0);
//        kseDemandPeaks.put("22_39nodes", 1000000000.0);
//        kseDemandPeaks.put("23_39nodes", 1000000000.0);
//        kseDemandPeaks.put("24_39nodes", 1000000000.0);


//        kseDemandPeaks.put("1_30nodes", 1000000000.0);
//        kseDemandPeaks.put("2_30nodes", 1000000000.0);
//        kseDemandPeaks.put("3_30nodes", 1000000000.0);
//        kseDemandPeaks.put("4_30nodes", 1000000000.0);
//        kseDemandPeaks.put("5_30nodes", 1000000000.0);
//        kseDemandPeaks.put("6_30nodes", 1000000000.0);
//        kseDemandPeaks.put("7_30nodes", 1000000000.0);
//        kseDemandPeaks.put("8_30nodes", 1000000000.0);
//        kseDemandPeaks.put("9_30nodes", 1000000000.0);
//        kseDemandPeaks.put("10_30nodes", 1000000000.0);
//        kseDemandPeaks.put("11_30nodes", 1000000000.0);
//        kseDemandPeaks.put("12_30nodes", 1000000000.0);
//        kseDemandPeaks.put("13_30nodes", 1000000000.0);
//        kseDemandPeaks.put("14_30nodes", 1000000000.0);
//        kseDemandPeaks.put("15_30nodes", 1000000000.0);
//        kseDemandPeaks.put("16_30nodes", 1000000000.0);
//        kseDemandPeaks.put("17_30nodes", 1000000000.0);
//        kseDemandPeaks.put("18_30nodes", 1000000000.0);
//        kseDemandPeaks.put("19_30nodes", 1000000000.0);
//        kseDemandPeaks.put("20_30nodes", 1000000000.0);
//        kseDemandPeaks.put("21_30nodes", 1000000000.0);
//        kseDemandPeaks.put("22_30nodes", 1000000000.0);
//        kseDemandPeaks.put("23_30nodes", 1000000000.0);
//        kseDemandPeaks.put("24_30nodes", 1000000000.0);
    }

// for kse:
//    static {
//        kseDemandPeaks.put("1_summer", 11365.100);
//        kseDemandPeaks.put("2_summer", 10553.063);
//        kseDemandPeaks.put("3_summer", 10455.313);
//        kseDemandPeaks.put("4_summer", 10601.050);
//        kseDemandPeaks.put("5_summer", 10581.663);
//        kseDemandPeaks.put("6_summer", 10910.725);
//        kseDemandPeaks.put("7_summer", 12626.613);
//        kseDemandPeaks.put("8_summer_p", 13802.588);
//        kseDemandPeaks.put("9_summer", 13465.450);
//        kseDemandPeaks.put("10_summer", 11867.075);
//        kseDemandPeaks.put("11_summer", 10657.075);
//        kseDemandPeaks.put("12_summer", 10456.100);
//        kseDemandPeaks.put("13_summer", 10617.938);
//        kseDemandPeaks.put("14_summer", 10283.013);
//        kseDemandPeaks.put("15_summer_d", 9981.100);
//        kseDemandPeaks.put("16_summer", 9997.438);
//        kseDemandPeaks.put("17_summer", 10160.950);
//        kseDemandPeaks.put("18_summer", 10644.050);
//        kseDemandPeaks.put("19_summer", 11466.913);
//        kseDemandPeaks.put("20_summer", 12650.075);
//        kseDemandPeaks.put("21_summer", 13481.738);
//        kseDemandPeaks.put("22_summer", 13300.688);
//        kseDemandPeaks.put("23_summer", 11865.238);
//        kseDemandPeaks.put("24_summer", 10821.013);

//        kseDemandPeaks.put("1_winter_d", 14164.800);
//        kseDemandPeaks.put("2_winter", 14231.338);
//        kseDemandPeaks.put("3_winter", 14528.713);
//        kseDemandPeaks.put("4_winter", 14463.525);
//        kseDemandPeaks.put("5_winter", 14543.588);
//        kseDemandPeaks.put("6_winter", 14798.638);
//        kseDemandPeaks.put("7_winter", 16885.375);
//        kseDemandPeaks.put("8_winter", 18913.238);
//        kseDemandPeaks.put("9_winter", 19634.825);
//        kseDemandPeaks.put("10_winter", 19900.475);
//        kseDemandPeaks.put("11_winter", 19795.700);
//        kseDemandPeaks.put("12_winter", 19975.313);
//        kseDemandPeaks.put("13_winter", 20123.650);
//        kseDemandPeaks.put("14_winter_p", 20189.963);
//        kseDemandPeaks.put("15_winter", 20035.988);
//        kseDemandPeaks.put("16_winter", 19742.963);
//        kseDemandPeaks.put("17_winter", 19942.538);
//        kseDemandPeaks.put("18_winter", 19571.038);
//        kseDemandPeaks.put("19_winter", 19079.238);
//        kseDemandPeaks.put("20_winter", 18925.263);
//        kseDemandPeaks.put("21_winter", 18229.113);
//        kseDemandPeaks.put("22_winter", 16909.513);
//        kseDemandPeaks.put("23_winter", 15692.025);
//        kseDemandPeaks.put("24_winter", 14331.775);
//    }

    private PowerNetworkUtils() {
        // private constructor for Util class
    }
}
