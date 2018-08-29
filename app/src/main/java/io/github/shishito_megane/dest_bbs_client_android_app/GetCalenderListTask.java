package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

public class GetCalenderListTask extends AsyncTask<Integer, Integer, Integer> {

    private Context context;

    public GetCalenderListTask(
            Context context
    ){
        this.context = context;
    }

    // プロジェクション配列。
    // 取得したいプロパティの一覧を指定する。
    private final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CALENDAR_TIME_ZONE,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            CalendarContract.Calendars.OWNER_ACCOUNT,
    };

    // プロジェクション配列のインデックス。
    // パフォーマンス向上のために、動的に取得せずに、静的に定義しておく。
    private static final int CALENDAR_PROJECTION_IDX_ID = 0;
    private static final int CALENDAR_PROJECTION_IDX_NAME = 1;
    private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_NAME = 2;
    private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE = 3;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_COLOR = 4;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME = 5;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL = 6;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE = 7;
    private static final int CALENDAR_PROJECTION_IDX_VISIBLE = 8;
    private static final int CALENDAR_PROJECTION_IDX_SYNC_EVENTS = 9;
    private static final int CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT = 10;

    // 非同期処理
    @Override
    protected Integer doInBackground(Integer... params) {

        int calContentValue = getCalenderList();
        return calContentValue;
    }

    private int getCalenderList()  {

        // クエリ条件を設定する
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        // クエリを発行してカーソルを取得する
        final ContentResolver cr = this.context.getContentResolver();
        final Cursor cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        // カレンダーの総数取得
        int calContentValue = cur.getCount();
        Log.d("カレンダー", "総数: "+String.valueOf(calContentValue));

        // ログ出力 (Header)
        final StringBuilder sbHeader = new StringBuilder();
        for (final String property : CALENDAR_PROJECTION) {
            sbHeader.append(property).append(',');
        }
        Log.d("カレンダー", sbHeader.toString());

        while (cur.moveToNext()) {
            // カーソルから各プロパティを取得する
            final long id = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
            final String name = cur.getString(CALENDAR_PROJECTION_IDX_NAME);
            final String accountName = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME);
            final String accountType = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE);
            final int calendarColor = cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_COLOR);
            final String calendarDisplayName = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME);
            final int calendarAccessLevel = cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL);
            final String calendarTimeZone = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE);
            final int visible = cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE);
            final int syncEvents = cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS);
            final String ownerAccount = cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT);

            // ログ出力 (Body)
            final StringBuilder sbBody = new StringBuilder();
            sbBody.append(id).append(',');
            sbBody.append(name).append(',');
            sbBody.append(accountName).append(',');
            sbBody.append(accountType).append(',');
            sbBody.append(String.format("0x%08X", calendarColor)).append(',');
            sbBody.append(calendarDisplayName).append(',');
            sbBody.append(calendarAccessLevel).append(',');
            sbBody.append(calendarTimeZone).append(',');
            sbBody.append(visible).append(',');
            sbBody.append(syncEvents).append(',');
            sbBody.append(ownerAccount).append(',');
            Log.d("カレンダー", sbBody.toString());
        }

        cur.close();
        return calContentValue;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Integer cal) {


        // check calender list
        if (cal < 1){
            // display toast
            Toast toast = Toast.makeText(
                    this.context,
                    this.context.getString(R.string.err_get_calender_toast),
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
        else {
            // display toast
            Toast toast = Toast.makeText(
                    this.context,
                    this.context.getString(R.string.finish_get_calender_toast),
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
    }

    @Override
    protected void onCancelled() {

        // display toast
        Toast toast = Toast.makeText(
                this.context,
                this.context.getString(R.string.cancelled_get_calender_toast),
                Toast.LENGTH_SHORT
        );
        toast.show();
    }
}
