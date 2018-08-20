package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class Persons {

    public static int personId;

}

public class PersonActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        // hide navigation var
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // get PERSON_ID
        Intent intent = getIntent();
        Persons.personId = intent.getIntExtra(
                HomeActivity.PERSON_ID,
                0
        );
        Log.d("DB", "選択: "+ String.valueOf(Persons.personId));

        // get PERSON_IMAGE_ID
        final String personImageId = getPersonImageId(Persons.personId);
        int imageIdInt = getResources().getIdentifier(personImageId, "drawable", getPackageName());
        // set person image
        ImageView imageViewPersonImage = findViewById(R.id.imageViewPerson);
        imageViewPersonImage.setImageResource(imageIdInt);

        // get person name
        final String personName = getPersonName(Persons.personId);
        // set person name (person id)
        TextView textViewPersonID = findViewById(R.id.textViewPersonName);
        textViewPersonID.setText(personName);

        // get PERSON_DETAIL
        final String personDetail = getPersonDetail(Persons.personId);
        // set person name (person id)
        TextView textViewPersonDetail = findViewById(R.id.textViewPersonDetail);
        textViewPersonDetail.setText(personDetail);

        // call button function
        Button dialogCall = findViewById(R.id.buttonCall);
        dialogCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CallDialogFlagment dialog = new CallDialogFlagment();
                dialog.show(getFragmentManager(), "call");
            }
        });

        // call button function
        Button dialogItsMe = findViewById(R.id.buttonItsMe);
        dialogItsMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpdateStatusDialogFlagment dialog = new UpdateStatusDialogFlagment();
                dialog.show(getFragmentManager(), "itsme");
            }
        });
    }

    // Set Menu on the Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.personactivity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // process when menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.menuPersonDelete:
                DeletePersonFlagment dialog = new DeletePersonFlagment();
                Bundle args = new Bundle();
                args.putInt("personId",Persons.personId);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "delete_person");
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
    public String getPersonImageId(
            int Id
    ) {

        List<String> memberImageList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_IMAGE,
        };
        String selection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cursor = db.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Log.d("DB", "該当メンバーの画像のレコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberImageId = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_IMAGE)
            );
            memberImageList.add(memberImageId);
        }
        mDbHelper.close();

        return memberImageList.get(0);
    }
    public String getPersonName(
            int Id
    ) {

        List<String> memberNameList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_NAME,
        };
        String selection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cursor = db.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Log.d("DB", "該当メンバーの名前のレコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_NAME)
            );
            memberNameList.add(memberName);
        }
        mDbHelper.close();

        return memberNameList.get(0);
    }
    public String getPersonDetail(
            int Id
    ) {

        List<String> memberDetailList = new ArrayList<>();
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // select column
        String[] projection = {
                DbContract.MemberTable.ID,
                DbContract.MemberTable.COLUMN_DETAIL,
        };
        String selection = DbContract.MemberTable.ID + " = ?"; // WHERE 句
        String[] selectionArgs = { String.valueOf(Id) };
        Cursor cursor = db.query(
                DbContract.MemberTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Log.d("DB", "該当メンバーの詳細レコード数:"+String.valueOf(cursor.getCount()));

        while(cursor.moveToNext()) {
            String memberDetail = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbContract.MemberTable.COLUMN_DETAIL)
            );
            memberDetailList.add(memberDetail);
        }
        mDbHelper.close();

        return memberDetailList.get(0);
    }
}
