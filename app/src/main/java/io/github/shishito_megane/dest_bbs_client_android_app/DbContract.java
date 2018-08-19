package io.github.shishito_megane.dest_bbs_client_android_app;


import android.provider.BaseColumns;

public final class DbContract {

    // disable constructor
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {}

    public static class MemberTable implements BaseColumns {

        // db info
        public static final String DATABASE_NAME = "DestBBS.db";
        public static final String TABLE_NAME = "member";
        public static final String ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DETAIL = "detail";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CALENDAR = "calender";
        public static final String COLUMN_STATUS = "status";
    }
}
