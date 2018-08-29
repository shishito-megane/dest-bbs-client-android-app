package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class StatusRegistrationCalenderTask extends AsyncTask<Long, Integer, Long> {

    private String calenderOwnerId;
    private String statusCode;
    private Context context; // XXX: Listenerをおいて対処する

    StatusRegistrationCalenderTask(
            String calenderOwnerId,
            String statusCode,
            Context context
    ) {
        super();
        this.calenderOwnerId   = calenderOwnerId;
        this.statusCode = statusCode;
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

    // 非同期処理
    @Override
    protected Long doInBackground(Long... calenderOwnerIds) {

        String eventColorKey;

        // set color key
        if (this.statusCode.equals("入室")){
            eventColorKey = "7";    // Peacock(水色)
        }
        else {
            eventColorKey = "6";    // Tangerine(オレンジ色)
        }


        // get calenderId from calenderOwnerId
        final long calenderId = this.getCalenderId(
                this.calenderOwnerId
        );
        Log.d("カレンダー","ローカルcalenderId: "+String.valueOf(calenderId));

        final long startTime = new Date().getTime() + 2 * 60 * 1000;
        final long endTime = startTime;

        Long eventId = this.addEvent(
                calenderId,
                this.statusCode,
                "",
                eventColorKey,
                startTime,
                endTime
        );
        return eventId;
    }

    private long getCalenderId(String calenderOwnerId) {

        List<Long> calenderOwnerIdList = new ArrayList<>();

        // クエリ条件を設定する
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;
        final String selection = CalendarContract.Calendars.OWNER_ACCOUNT + "= ?";
        final String[] selectionArgs = { calenderOwnerId };
        final String sortOrder = null;

        // クエリを発行してカーソルを取得する
        final ContentResolver cr = this.context.getContentResolver();
        final Cursor cur = cr.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        // カレンダーの総数取得
        int calContentValue = cur.getCount();
        Log.d("カレンダー", "総数: " + String.valueOf(calContentValue));

        // check
        if (calContentValue == 1){
            Log.d("カレンダー", "総数が1なので良い" );
        } else {
            Log.e("カレンダー", "総数が1ではない" );
        }

        Log.d("カレンダー", "ID: "+calenderOwnerId);
        while (cur.moveToNext()) {
            final long calenderId = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
            calenderOwnerIdList.add(calenderId);
        }

        cur.close();
        return  calenderOwnerIdList.get(0);
    }

    private long addEvent(
            final long calendarId,
            final String title,
            final String description,
            final String colorKey,
            final long startMillis,
            final long endMillis
    ) {
        final ContentResolver cr = this.context.getContentResolver();

        final ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_COLOR_KEY, colorKey);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);

        final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        final long eventId = Long.parseLong(uri.getLastPathSegment());
        return eventId;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Long eventId) {

        Log.d("カレンダー", "在室状況を記録" );

        // display toast
        Toast toast = Toast.makeText(
                this.context,
                this.context.getString(R.string.finish_update_status),
                Toast.LENGTH_SHORT
        );
        toast.show();

    }

    @Override
    protected void onCancelled() {

        Log.d("カレンダー", "在室状況の記録をキャンセル" );

        // display toast
        Toast toast = Toast.makeText(
                this.context,
                this.context.getString(R.string.cancelled_update_status),
                Toast.LENGTH_SHORT
        );
        toast.show();
    }

//    private Listener listener;
//
//    void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    interface Listener {
//        void onSuccess(int count);
//    }
}