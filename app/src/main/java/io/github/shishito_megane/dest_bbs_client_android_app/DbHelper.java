package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static io.github.shishito_megane.dest_bbs_client_android_app.DbContract.MemberTable;

public class DbHelper extends SQLiteOpenHelper {

    // if schema change, VERSION increment
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = MemberTable.DATABASE_NAME;

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + MemberTable.TABLE_NAME + " ("
            + MemberTable.ID + " INTEGER PRIMARY KEY,"
            + MemberTable.COLUMN_NAME + " TEXT,"
            + MemberTable.COLUMN_DETAIL + " TEXT,"
            + MemberTable.COLUMN_IMAGE + " TEXT,"
            + MemberTable.COLUMN_ADDRESS + " TEXT,"
            + MemberTable.COLUMN_CALENDAR + " TEXT,"
            + MemberTable.COLUMN_STATUS + " TEXT"
            + ")";

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + MemberTable.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // If not exist SQLite file
        db.execSQL(SQL_CREATE_TABLE);
        Log.d("DB", "データベースが作成されました");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // VERSION が上がった場合に実行されます。本サンプルでは単純に DROP して CREATE し直します。
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // VERSION が下がった場合に実行されます。本サンプルでは単純に DROP して CREATE し直します。
        onUpgrade(db, oldVersion, newVersion);
    }
}
