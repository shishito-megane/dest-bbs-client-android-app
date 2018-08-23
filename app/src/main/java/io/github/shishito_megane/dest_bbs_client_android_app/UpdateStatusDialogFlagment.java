package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UpdateStatusDialogFlagment extends DialogFragment {

    StatusRegistrationTask register;

    int personId;
    String calenderId;
    String oldStatusCode;
    String newStatusCode;

    public Dialog onCreateDialog(Bundle savedInstanceState){

        int defaultItem = 0;
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get ags
        personId = getArguments().getInt("personId");

        // get calenderId, statusCode
        calenderId = getPersonCalender(personId);
        oldStatusCode = getPersonStatus(personId);


        // 更新前の在室状況が入室なら
        if (oldStatusCode.equals("入室")){

            builder.setTitle(R.string.update_leaving_room_title);

            builder.setSingleChoiceItems(
                    R.array.status_leaving_room_list,
                    defaultItem,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
        }
        else {

            builder.setTitle(R.string.update_entering_room_title);

            builder.setSingleChoiceItems(
                    R.array.status_entering_room_list,
                    defaultItem,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
        }

        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // nothing checked err
                if (!checkedItems.isEmpty()) {
                    Log.d("checkedItem:", "" + checkedItems.get(0));

                    // get checked item
                    int checkedItemInt = checkedItems.get(0);

                    Resources res = getResources();
                    TypedArray array = res.obtainTypedArray(R.array.status_code_array);

                    // get new status code
                    if (oldStatusCode.equals("入室")){
                        // XXX: 入退室で項目の数が違う分，ずらす
                        newStatusCode = array.getString(checkedItemInt + 1);
                    }
                    else {
                        newStatusCode = array.getString(checkedItemInt);
                    }

                    array.recycle();

                    // add recording function
                    // DB
                    updatePersonStatus(personId, newStatusCode);

                    // calender
                    register = new StatusRegistrationTask(
                            calenderId,
                            newStatusCode,
                            getActivity()
                    );
                    register.execute();

                    // Return HomeActivity(new created) and finish this activity
                    Intent intent = new Intent(getActivity(),HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Log.w("checkedItem:", "なし");
                }
            }
        });

        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.cancelled_update_status,
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        });

        return builder.create();
    }

    // DB
    public String getPersonCalender(
            int Id
    ) {

        List<String> memberCalenderList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_CALENDAR,
        };
        String selection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cur = db.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // get total records value
        int contentValue = cur.getCount();
        Log.d("DB", "該当メンバーのカレンダーのレコード数:"+String.valueOf(contentValue));

        while(cur.moveToNext()) {
            String memberCalender = cur.getString(
                    cur.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_CALENDAR)
            );
            memberCalenderList.add(memberCalender);
        }

        // check
        if (contentValue == 1){
            Log.d("DB", "総数が1なので良い" );
        } else {
            Log.w("DB", "総数が1ではない" );
        }

        cur.close();
        mDbHelper.close();

        return memberCalenderList.get(0);
    }
    public String getPersonStatus(
            int Id
    ) {

        List<String> memberStatusList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_STATUS,
        };
        String selection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cur = db.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // get total records value
        int contentValue = cur.getCount();
        Log.d("DB", "該当メンバーのカレンダーのレコード数:"+String.valueOf(contentValue));

        while(cur.moveToNext()) {
            String memberCalender = cur.getString(
                    cur.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_STATUS)
            );
            memberStatusList.add(memberCalender);
        }

        // check
        if (contentValue == 1){
            Log.d("DB", "総数が1なので良い" );
            Log.d("DB", memberStatusList.get(0) );
        } else {
            Log.w("DB", "総数が1ではない" );
        }

        cur.close();
        mDbHelper.close();

        return memberStatusList.get(0);
    }
    public void updatePersonStatus(
            int Id,
            String statusCode
    ) {

        DbHelper mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.MemberTable.COLUMN_STATUS, statusCode);
        String selection = DbContract.MemberTable.ID + " = ?";  // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };

        db.update(
                DbContract.MemberTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        db.close();
        mDbHelper.close();
    }

    // Calender
    /**
     * 非同期で　Google Calendar に在室状況を登録するクラス
     */
    private class StatusRegistrationTask extends AsyncTask<Long, Integer, Long> {

        String calenderOwnerId;
        String statusCode;
        Context context;
        String eventColorKey;

        /**
         * コンストラクタ
         */
        private StatusRegistrationTask(
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
            final String[] selectionArgs = { calenderOwnerId};
            final String sortOrder = null;

            // クエリを発行してカーソルを取得する
            final ContentResolver cr = this.context.getContentResolver();
            final Cursor cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);

            // カレンダーの総数取得
            int calContentValue = cur.getCount();
            Log.d("カレンダー", "総数: " + String.valueOf(calContentValue));


            Log.d("カレンダー", "ID: "+calenderOwnerId);
            while (cur.moveToNext()) {
                final long calenderId = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
                calenderOwnerIdList.add(calenderId);
            }

            // check
            if (calContentValue == 1){
                Log.d("カレンダー", "総数が1なので良い" );
            } else {
                Log.w("カレンダー", "総数が1ではない" );
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
                    // XXX: getString(R.string.finish_update_status) で取得したい
                    "記録しました．",
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
                    // XXX: getString(R.string.cancelled_update_status) で取得したい
                    "記録をキャンセルしました．",
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
    }
}
