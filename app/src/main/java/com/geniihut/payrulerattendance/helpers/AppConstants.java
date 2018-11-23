package com.geniihut.payrulerattendance.helpers;


import java.util.Calendar;

/**
 * Created by mezakitchie on 3/3/2015.
 */
public class AppConstants {

    public static final String DATE_FORMAT_MONTH_FULL = "MMMM";
    public static final String DATE_FORMAT_API_INPUT = "MM/dd/yy";
    public static final String TIME_FORMAT_API_INPUT = "HH:mm:ss";
    public static final String DATE_FORMAT_API_INPUT_YYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_API_OUTPUT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyy";
    public static final String DATE_FORMAT_DISPLAY_WITHOUT_YEAR = "MMM dd";
    public static final String DATE_FORMAT_LOCAL_TIME = "MMM dd, yyyy hh:mm:ss aa";
    public static final String DATE_FORMAT_TIME_IN_LOGS = "yyyy-MM-dd HH:mm:ss";

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    public static int REQUEST_SET_RETRY_POLICY_MILLI = 20 * 1000;

    // ImageLoader Constants
    public static final boolean PAUSE_ON_SCROLL = false;

    public static final boolean PAUSE_ON_FLING = true;
    // Volley GetRequest Tag
    public static final String GET_REQUEST_TAG = "GetRequest";
    public static final String REQUEST_TAG_LOGIN = "LoginRequest";
    public static final String REQUEST_TAG_SYNC_LOGS = "SyncLogsRequest";

    //ONRESULT Code
    public static final int RESULT_CODE_PROFILE = 200;

    public static final int CALENDAR_START_DAY = Calendar.MONDAY;
    public static String[] DAYS_OF_WEEK = {"SUN","MON","TUE","WED","THU","FRI","SAT"};

    public static int REQUEST_CODE_CHECK_SETTINGS = 2;
}

