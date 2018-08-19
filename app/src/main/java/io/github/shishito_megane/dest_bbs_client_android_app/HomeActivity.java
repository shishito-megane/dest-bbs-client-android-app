package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static io.github.shishito_megane.dest_bbs_client_android_app.DbContract.MemberTable;

public class HomeActivity extends AppCompatActivity {

    public static final String PERSON_IMAGE_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_IMAGE_ID";
    public static final String PERSON_ID = "io.github.shishito_megane.dest_bbs_client_android_app.PERSON_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hide navigation var
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // set welcome msg
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String default_lab_name = getString(R.string.lab_name);
        String lab_name = prefs.getString("lab_name", default_lab_name);
        String welcome_msg = getString(R.string.welcome_msg, lab_name);

        // display welcome msg
        TextView textView = findViewById(R.id.textViewWelcomeMsg);
        textView.setText(welcome_msg);

        // get member ids
        final List<Integer> memberIdList = getMemberIdList();
        Log.d("DB", "長さ:"+String.valueOf(memberIdList.size()));
        if (memberIdList.size() == 0){
            // TODO; デフォルト値とする
            saveData(
                    getString(R.string.member_name),
                    getString(R.string.member_detail),
                "sample_0",
                "012-345-6789",
                "xxx",
                "in"
            );
        }

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
                intent.putExtra(PERSON_IMAGE_ID, memberImageIntegerList.get(position));
                intent.putExtra(PERSON_ID, memberNameList.get(position));
                startActivity( intent );
            }
        };

        gridview.setOnItemClickListener(mMessageClickedHandler);

    }

    // メニューをActivity上に設置する
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
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
    public List<Integer> getMemberIdList() {

        List<Integer> memberIdList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

        Log.d("DB", "メンバーIDリスト:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            int memberID = cursor.getInt(
                    cursor.getColumnIndexOrThrow(MemberTable.ID)
            );
            memberIdList.add(memberID);
        }
        cursor.close();

        return memberIdList;
    }
    public List<String> getMemberNameList() {

        List<String> memberNameList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

        Log.d("DB", "メンバーの名前:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberName = cursor.getString(
                    cursor.getColumnIndexOrThrow(MemberTable.COLUMN_NAME)
            );
            memberNameList.add(memberName);
        }
        cursor.close();
        return memberNameList;
    }
    public List<String> getMemberImageList() {

        List<String> memberImageList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

        Log.d("DB", "メンバーの画像:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberName = cursor.getString(
                    cursor.getColumnIndexOrThrow(MemberTable.COLUMN_IMAGE)
            );
            memberImageList.add(memberName);
        }
        cursor.close();
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
    }
}


