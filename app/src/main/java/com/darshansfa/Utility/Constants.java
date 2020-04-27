package com.darshansfa.Utility;


import com.darshansfa.Configuration.Configuration;

/**
 * Created by Ranjan on 20-11-2015.
 */

public class Constants {

    String brand = Configuration.BRAND_NAME;

    //public static String Host = "http://sfapony-qa-web.us-east-1.elasticbeanstalk.com/";
    public static String Host = "http://darshan.gladminds.co/";
    private static final int APP_BUILD = 3;

    public static String IMAGE_PATH;

    static {
        switch (APP_BUILD) {
            case 1:
                //Host = "http://mfsales-dev-web-new.us-east-1.elasticbeanstalk.com/";
                Host = "http://sfapony-dev-web.us-east-1.elasticbeanstalk.com/pony/";
                IMAGE_PATH = "DEV";
                break;
            case 2:
                //Host = "http://qa.philips.gladminds.co/";
                Host = "http://sfapony-qa-web.us-east-1.elasticbeanstalk.com/";
                IMAGE_PATH = "QA";
                break;
            case 3:
                //Host = "http://philips.gladminds.co/";
                Host = "http://darshan.gladminds.co";
                IMAGE_PATH = "PROD";
                break;
            case 4:
                Host = "http://192.168.1.84:8000/";
                IMAGE_PATH = "LOCAL";
                break;
        }
    }

    //Mint.initAndStartSession(this.getApplication(), "513b43fa");
    public static final String SPLUNK_KEY = "513b43fa"; // Dev

    public static class Pref {
        public static final String LOCALITY = "LOCALITY";
        public static final String AVG = "AVG";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";

        public static final String PJPID = "pjpid";

        public static final String PASSWORD = "password";
        public static final String GCM_REG_ID = "gcm_reg_id";
        public static final String REPORT_LAST_SYNC = "REPORT_LAST_SYNC";

        public static final String TIMESTAMP_PARTS = "TIMESTAMP_PARTS";
        public static final String TIMESTAMP_ASSOCIATE_PARTS = "TIMESTAMP_ASSOCIATE_PARTS";
        public static final String TIMESTAMP_FOCUS_PARTS = "TIMESTAMP_FOCUS_PARTS";
        public static final String TIMESTAMP_RETAILER = "TIMESTAMP_RETAILER";
        public static final String TIMESTAMP_STOCK = "TIMESTAMP_STOCK";
        public static final String TIMESTAMP_ORDER = "TIMESTAMP_ORDER";
        public static final String TIMESTAMP_OUTSTANDING = "TIMESTAMP_OUTSTANDING";
        public static final String TIMESTAMP_AVG_PARTS = "TIMESTAMP_AVG_PARTS";
        public static final String TIMESTAMP_PENDING_ORDER = "TIMESTAMP_PENDING_ORDER";
        public static final String ORDER_NO = "Order_no";

        public static final String AVG_PARTS_OFFSET = "AVG_PARTS_OFFSET";
        public static final String LOCATION_UPDATE_RUNNING = "location_update_running";

        public static final String TOKEN = "token";
        public static final String LOGIN_TYPE = "login_type";
        public static final String DSR_ID = "dsr_id";
        //public static final String DISTRIBUTOR_ID = "dist_id";
        public static final String DISTRIBUTOR_ID = "depot_code";
        public static final String RETAILER_ID = "retailer_id";
        public static final String DEPO_CODE = "depo_code";
        public static final String DEPO_NAME = "depo_name";

        public static final String DEPOT_CODE = "depot_code";

        public static final String RETAILER_NAME = "retailer_name";
        public static final String NOTE_TYPE = "note_type";

        public static final String TODAY = "today";
        public static final String LOCALITY_ID = "locality_id";
        public static final String LAT = "LAT";
        public static final String LONG = "LONG";


    }


    public static final String STATUS_PENDING = "pending";
    public static final String UPDATE = "update";
    public static final String ORDER_OPEN = "OPEN ORDER";//shipped
    public static final String ORDER_SHIPPED = "SHIPPED ORDER";//ALLOCATED
    public static final String ORDER_PARTIAL_SHIPPED = "PARTIALLY SHIPPED ORDER";
    public static final String ORDER_ALLOCATED = "ALLOCATED";
    public static final String ORDER_PENDING = "PENDING";


    public static final String PATH_DAILY_NOTE = "daily_note";
    public static final String PATH_RETAILER_NOTE = "retailer_note";


    public static final String PATH_DAILY_NOTE_UPDATE = "update_daily_note";
    public static final String PATH_RETAILER_NOTE_UPDATE = "update_retailer_note";


    public static final String DATE_CHANGE = "date_change";
    public static final String ON_BACK_PRESS = "on_back_press";
    public static final String RETAILER_LOCATION_CHANGE = "retailer_location_change";

    public static final String STATUS_DONE = "done";
    public static final String DSR_LOGIN = "dsr";
    public static final String RETAILER_LOGIN = "retailer";


    public static final String DAILY_NOTE = "daily";

    public static final String ADV_COLLECTION = "avd_collection";
    public static final String COLLECTION_AMOUNT = "collection_amount";
    public static final String RETAILER_NOTE = "retailer";

    public static final String NOTE_HEAD = "note_head";


    public static final String INVOICE_ID = "invoice";


    public static final String GET_RETAILER_OUTSTANDING = "/sfa/index.php?action=getalloutstandings&retailer_code=600003";


    //For Weekdays
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";
    public static final String NOTE_TYPE = "note_type";
    public static final String NOTE_ID = "note_id";

    public static final String CURRENT_MONTH = "current month";
    public static final String THREE_MONTH = "3 month";


    public static class API {

        final String[] items = {"Sync ALL", "Product", "Order", "Retailer", "Outstanding", "PJPSchedule",
                "Product Avg", "Stock", "Focus Product", "Back Order"};
        public static final int PRODUCT = 1;
        public static final int ORDERS = 2;
        public static final int RETAILERS = 3;
        public static final int OUTSTANDING = 4;
        public static final int PJP = 5;
        public static final int PRODUCT_AVG = 6;
        public static final int STOCK = 7;
        public static final int FOCUS_PARTS_DSR = 8;
        public static final int PENDING = 9;
        public static final int COLLECTION = 10;


        public static final int ORDERS_RETAILER = 10;
    }

    public static final int DOWNLOADING = 1;
    public static final int DONE = 2;
    public static final int WAITING = 3;
    public static final int ERROR = 4;


    public static final int STATUS_SUBMIT = 1;
    public static final int STATUS_NOT_SUBMIT = 0;


    public static final String LOGIN_DSR = "dsr";
    public static final String LOGIN_RETAILER = "retailer";


}
