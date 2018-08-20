package io.github.shishito_megane.dest_bbs_client_android_app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PersonAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_add);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personaddactivity_action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get person info
        EditText editPersonName = (EditText)findViewById(R.id.editTextPersonName);
        String personName = editPersonName.getText().toString();
        EditText editPersonAddress = (EditText)findViewById(R.id.editTextPersonAddress);
        String personAddress = editPersonAddress.getText().toString();
        EditText editTextPersonDetail = (EditText)findViewById(R.id.editTextPersonDetail);
        String personDetail = editTextPersonDetail.getText().toString();
        EditText editTextCalenderId = (EditText)findViewById(R.id.editTextCalenderId);
        String personCalenderId = editTextCalenderId.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.selectThumbnail);
        String personThumbnail = (String)spinner.getSelectedItem();

        Log.v("DB", "追加: "+personName+personAddress+personDetail+personCalenderId+personThumbnail);

        saveData(
                personName,
                personDetail,
                personThumbnail,
                personAddress,
                personCalenderId,
                getString(R.string.default_person_status)
        );

        // display toast
        Toast toast = Toast.makeText(
                this,
                R.string.finish_person_add_register,
                Toast.LENGTH_SHORT
        );
        toast.show();

        // transition HomeActivity
        Intent intent = new Intent(
               this,
                HomeActivity.class
        );
        startActivity(intent);

        return true;
    }

    // DB
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
}
