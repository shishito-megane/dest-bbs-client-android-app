package io.github.shishito_megane.dest_bbs_client_android_app;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static io.github.shishito_megane.dest_bbs_client_android_app.DbContract.MemberTable;

public class HomeActivity extends AppCompatActivity {

    public static final String PERSON_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ID";

    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // hide navigation var
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // set welcome msg
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String default_lab_name = getString(R.string.pref_default_lab_name);
        String lab_name = prefs.getString("lab_name", default_lab_name);
        String welcome_msg = getString(R.string.welcome_msg, lab_name);

        // display welcome msg
        TextView textView = findViewById(R.id.textViewWelcomeMsg);
        textView.setText(welcome_msg);

        // get member ids
        List<Integer> memberIdListTmp = getMemberIdList();
        Log.d("DB", "長さ:"+String.valueOf(memberIdListTmp.size()));
        if (memberIdListTmp.size() == 0){
            saveData(
                    getString(R.string.default_person_name),
                    getString(R.string.default_person_detail),
                    getString(R.string.default_person_image_id),
                    getString(R.string.pref_default_address),
                    getString(R.string.default_person_calendar),
                    getString(R.string.default_person_status)
            );
            memberIdListTmp = getMemberIdList();
        }
        final List<Integer> memberIdList = memberIdListTmp;

        // get member name
        final List<String> memberNameList = getMemberNameList();
        Log.d("DB", "長さ:"+String.valueOf(memberNameList.size()));

        // get member name
        final List<String> memberimageList = getMemberImageList();
        Log.d("DB", "長さ:"+String.valueOf(memberimageList.size()));

        // for-each, convert memberID to R.drawable.XX and convert to int, register array
        // for-each, member名をR.drawable.名前としてintに変換してarrayに登録
        // Resource ID (Member Image ID)
        final List<Integer> memberImageIntegerList = new ArrayList<>();
        for (String id : memberimageList) {
            int imageId = getResources().getIdentifier(
                    id, "drawable", getPackageName());
            memberImageIntegerList.add(imageId);
        }

        // generation GridView instance
        GridView gridview = findViewById(R.id.gridViewMember);

        // generation GridAdapter instance, inherited BaseAdapter
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
        MemberAdapter adapter = new MemberAdapter(
                this.getApplicationContext(),
                R.layout.grid_item_member,
                memberNameList,
                memberImageIntegerList
        );

        // set gridView adapter
        gridview.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(
                    AdapterView parent,
                    View v,
                    int position,
                    long id
            ) {
                Intent intent = new Intent(getApplication(), PersonActivity.class);
                intent.putExtra(PERSON_ID, memberIdList.get(position));
                startActivity( intent );
            }
        };

        gridview.setOnItemClickListener(mMessageClickedHandler);

        // set progress bar (calender get test )
        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.get_calender_toast));

        // test calender
        preCheckCalender();

    }

    // Set Menu on the Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.homeactivity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // process when menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.menuPersonAdd:
                Intent intent_person_add = new Intent(
                        this,
                        PersonAddActivity.class
                );
                startActivity(intent_person_add);
                return true;
            case R.id.menuSetting:
                // transition HomeActivity
                Intent intent_setting = new Intent(
                        this,
                        SettingsActivity.class
                );
                startActivity(intent_setting);
                return true;
            case R.id.menuHelp:
                Intent intent_help = new Intent(
                        this,
                        HelpActivity.class
                );
                startActivity(intent_help);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // DB
    public List<Integer> getMemberIdList() {

        List<Integer> memberIdList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                MemberTable.ID
        };
        Cursor cursor = db.query(
                MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーIDの総数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            int memberID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(MemberTable.ID)
            );
            memberIdList.add(memberID);
        }
        cursor.close();
        mDbHelper.close();

        return memberIdList;
    }
    public List<String> getMemberNameList() {

        List<String> memberNameList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                MemberTable.ID,
                MemberTable.COLUMN_NAME
        };
//        String selection = MemberTable.ID + " = ?"; // WHERE 句
//        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cursor = db.query(
                MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーの名前の総数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberName = cursor.getString(
                    cursor.getColumnIndexOrThrow(MemberTable.COLUMN_NAME)
            );
            memberNameList.add(memberName);
        }
        cursor.close();
        mDbHelper.close();
        return memberNameList;
    }
    public List<String> getMemberImageList() {

        List<String> memberImageList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                MemberTable.ID,
                MemberTable.COLUMN_IMAGE,
        };
        Cursor cursor = db.query(
                MemberTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        Log.d("DB", "メンバーの画像の総数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberImageID = cursor.getString(
                    cursor.getColumnIndexOrThrow(MemberTable.COLUMN_IMAGE)
            );
            memberImageList.add(memberImageID);
        }
        cursor.close();
        mDbHelper.close();
        return memberImageList;
    }
    public void saveData(
            String name,
            String detail,
            String image,
            String address,
            String calender,
            String status
    ){
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("detail", detail);
        values.put("image", image);
        values.put("address", address);
        values.put("calender", calender);
        values.put("status", status);

        long newRowId = db.insert("member", null, values);
        Log.d("DB", "挿入"+name+String.valueOf(newRowId));

        db.close();
        mDbHelper.close();
    }

    // calender
    private void preCheckCalender() {

        // No Internet connection
        if (!isDeviceOnline()) {
            // display toast
            Toast toast = Toast.makeText(
                    this,
                    R.string.no_internet_connection_msg,
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
        else {
            new CalendetTestTask().execute();
        }
    }

    /**
     * 現在、端末がネットワークに接続されているかを確認する。
     *
     * @return ネットワークに接続されている場合にはtrueを、そうでない場合にはfalseを返す。
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 非同期で　Google Calendar のテストを行うタスク。
     */
    private class CalendetTestTask extends AsyncTask<Integer, Integer, Integer> {

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
            final ContentResolver cr = getContentResolver();
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
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Integer cal) {

            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }

            // check calender list
            if (cal < 1){
                // display toast
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.err_get_calender_toast),
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
            else {
                // display toast
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.finish_get_calender_toast),
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            // display toast
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.cancelled_get_calender_toast),
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }
    }
}


