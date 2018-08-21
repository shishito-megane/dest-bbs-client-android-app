package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
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

    ProgressDialog mProgress;
    StatusRegistrationTask register;

    public Dialog onCreateDialog(Bundle savedInstanceState){

        int defaultItem = 0;
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.update_enter_room_title);

        // get ags (personCalenderID, from DB)
        final String calenderId = getArguments().getString("personCalender");

        builder.setSingleChoiceItems(
                R.array.status_entering_room_array,
                defaultItem,
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                checkedItems.clear();
                checkedItems.add(which);
                // The 'which' argument contains the index position
                // of the selected item
            }
        });

        builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // nothing checked err
                if (!checkedItems.isEmpty()) {
                    Log.d("checkedItem:", "" + checkedItems.get(0));
                }

                int statusCodeInt = checkedItems.get(0);
                Resources res = getResources();
                TypedArray array = res.obtainTypedArray(R.array.status_entering_room_value);
                String statusCode =  array.getString(statusCodeInt);
                array.recycle();

                // add recording function
                // set progress bar (calender get test )
                mProgress = new ProgressDialog(getActivity());
                mProgress.setMessage(getString(R.string.registering_status));

                // register = new StatusRegistrationTask(calenderId);
                register = new StatusRegistrationTask(calenderId, statusCode);
                register.execute();

                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        R.string.finish_update_status,
                        Toast.LENGTH_SHORT
                );
                toast.show();

                // transition HomeActivity
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                startActivity(intent);
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

                // transition HomeActivity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });

        builder.show();

        return builder.create();
    }

    // Calender
    /**
     * 非同期で　Google Calendar に在室状況を登録するクラス
     */
    private class StatusRegistrationTask extends AsyncTask<Long, Integer, Long> {

        String calenderOwnerId;
        String statusCode;

        /**
         * コンストラクタ
         */
        private StatusRegistrationTask(String calenderOwnerId, String statusCode) {
            super();
            this.calenderOwnerId   = calenderOwnerId;
            this.statusCode = statusCode;
        }

        private Exception mLastError = null;

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
//        private static final int CALENDAR_PROJECTION_IDX_NAME = 1;
//        private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_NAME = 2;
//        private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE = 3;
//        private static final int CALENDAR_PROJECTION_IDX_CALENDAR_COLOR = 4;
//        private static final int CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME = 5;
//        private static final int CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL = 6;
//        private static final int CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE = 7;
//        private static final int CALENDAR_PROJECTION_IDX_VISIBLE = 8;
//        private static final int CALENDAR_PROJECTION_IDX_SYNC_EVENTS = 9;
//        private static final int CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT = 10;

        // 非同期処理
        @Override
        protected Long doInBackground(Long... calenderOwnerIds) {

            final String eventColorKey = "4";
//            final long calenderOwnerId = calenderOwnerId;

            // get calenderId from calenderOwnerId
            final long calenderId = this.getCalenderId(
                    this.calenderOwnerId
            );
            Log.d("カレンダー","ローカルcalenderId: "+String.valueOf(calenderId));

            // get now time
            final long now = new Date().getTime();

            // set end time (1 min)
            final long anHourLater = now;

            Long eventId = this.addEvent(
                    calenderId,
                    this.statusCode,
                    "イベントの説明",
                    eventColorKey,
                    now,
                    anHourLater
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
            final ContentResolver cr = getActivity().getContentResolver();
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
            final ContentResolver cr = getActivity().getContentResolver();

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
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Long eventId) {

            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
            else {
                // display toast
                Toast toast = Toast.makeText(
                        getActivity(),
                        getString(R.string.finish_get_calender_toast),
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();


            // display toast
            Toast toast = Toast.makeText(
                    getActivity(),
                    getString(R.string.cancelled_get_calender_toast),
                    Toast.LENGTH_SHORT
            );
            toast.show();

        }
    }
}
